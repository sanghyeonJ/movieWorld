package com.movieWorld.service;

import com.movieWorld.api.GeminiRecommendClient;
import com.movieWorld.api.MovieApiClient;
import com.movieWorld.config.RecommendProperties;
import com.movieWorld.domain.Movie;
import com.movieWorld.domain.Recommendation;
import com.movieWorld.domain.RecommendationItem;
import com.movieWorld.domain.RecommendRateLimit;
import com.movieWorld.domain.Review;
import com.movieWorld.dto.api.AiRecommendItemDto;
import com.movieWorld.dto.api.TmdbMovieDetailResponse;
import com.movieWorld.dto.api.TmdbMovieListResponse;
import com.movieWorld.dto.response.RecommendResultDto;
import com.movieWorld.mapper.MovieMapper;
import com.movieWorld.mapper.RecommendRateLimitMapper;
import com.movieWorld.mapper.RecommendationItemMapper;
import com.movieWorld.mapper.RecommendationMapper;
import com.movieWorld.mapper.ReviewMapper;
import com.movieWorld.util.TmdbLocaleUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecommendService {

    private final RecommendProperties recommendProperties;
    private final ReviewMapper reviewMapper;
    private final RecommendRateLimitMapper recommendRateLimitMapper;
    private final RecommendationMapper recommendationMapper;
    private final RecommendationItemMapper recommendationItemMapper;
    private final MovieMapper movieMapper;
    private final GeminiRecommendClient geminiRecommendClient;
    private final MovieApiClient movieApiClient;
    private final MovieService movieService;

    /** 현재 리뷰 개수 (컨트롤러 요청 페이지용) */
    public int getReviewCount(Long userId) {
        if (userId == null) return 0;
        return reviewMapper.countByUserId(userId);
    }

    /** 추천 가능 여부 (리뷰 개수 + Rate Limit) */
    public boolean canRecommend(Long userId) {
        if (userId == null) return false;
        int count = reviewMapper.countByUserId(userId);
        if (count < recommendProperties.getMinReviewCount()) return false;
        RecommendRateLimit limit = recommendRateLimitMapper.findByUserIdAndDate(userId, LocalDate.now());
        if (limit != null && limit.getCount() >= recommendProperties.getRateLimitDaily()) return false;
        return true;
    }

    /** 추천 실행 (캐시 확인 → 없으면 Gemini API 호출 후 DB 저장, Rate Limit 갱신) */
    @Transactional
    public void requestRecommendation(Long userId) {
        if (!canRecommend(userId)) {
            throw new IllegalArgumentException("추천을 받을 수 없습니다. (리뷰 개수 또는 요청 횟수 확인)");
        }
        int reviewCount = reviewMapper.countByUserId(userId);
        if (recommendProperties.isCacheEnabled()) {
            Recommendation cached = recommendationMapper.findValidByUserId(userId);
            if (cached != null) {
                return;
            }
        }

        // 1) 사용자 리뷰 목록 + 영화 제목으로 프롬프트 문자열 생성
        String prompt = buildRecommendPrompt(userId);
        // 2) Gemini API 호출 → [{ title, reason }, ...]
        List<AiRecommendItemDto> aiItems = geminiRecommendClient.getRecommendations(prompt);
        log.info("Gemini 추천 개수: {}", aiItems != null ? aiItems.size() : 0);

        Recommendation rec = new Recommendation();
        rec.setUserId(userId);
        rec.setReviewCount(reviewCount);
        rec.setReviewLastUpdatedAt(LocalDateTime.now());
        rec.setIsValid(true);
        recommendationMapper.insert(rec);

        // 3) AI 추천 제목으로 TMDB 검색 → 있으면 DB 저장 후 recommendation_items에 추가
        String lang = TmdbLocaleUtil.getCurrentTmdbLanguage();
        int rank = 1;
        for (AiRecommendItemDto item : aiItems) {
            String title = item.getTitle() != null ? item.getTitle().trim() : "";
            if (title.isEmpty()) continue;
            TmdbMovieListResponse searchRes = movieApiClient.searchMovies(title, lang, 1);
            if (searchRes == null || searchRes.getResults() == null || searchRes.getResults().isEmpty()) {
                log.debug("TMDB 검색 결과 없음: {}", title);
                continue;
            }
            Integer tmdbId = searchRes.getResults().get(0).getId();
            if (tmdbId == null) continue;
            Movie movie = movieService.findOrCreateByApiId(tmdbId);
            if (movie == null) continue;
            RecommendationItem ri = new RecommendationItem();
            ri.setRecommendationId(rec.getId());
            ri.setMovieId(movie.getId());
            ri.setItemRank(rank++);
            ri.setReason(item.getReason() != null ? item.getReason() : "");
            ri.setConfidenceScore(BigDecimal.ZERO);
            recommendationItemMapper.insert(ri);
        }

        LocalDate today = LocalDate.now();
        RecommendRateLimit limit = recommendRateLimitMapper.findByUserIdAndDate(userId, today);
        if (limit == null) {
            RecommendRateLimit newLimit = new RecommendRateLimit();
            newLimit.setUserId(userId);
            newLimit.setDate(today);
            newLimit.setCount(1);
            recommendRateLimitMapper.insert(newLimit);
        } else {
            recommendRateLimitMapper.updateCount(userId, today);
        }
    }

    /** 사용자 리뷰 기반으로 Gemini에 넘길 프롬프트 문자열 생성 (로케일에 따라 한국어/일본어) */
    private String buildRecommendPrompt(Long userId) {
        Locale locale = LocaleContextHolder.getLocale();
        boolean isJapanese = locale != null && "ja".equals(locale.getLanguage());

        List<Review> reviews = reviewMapper.findByUserId(userId);
        List<String> lines = new ArrayList<>();
        for (Review r : reviews) {
            Movie m = movieMapper.findById(r.getMovieId());
            String title = m != null ? m.getTitle() : (isJapanese ? "映画" : "영화");
            String pointLabel = isJapanese ? "点" : "점";
            lines.add(title + ": " + (r.getRating() != null ? r.getRating() : 0) + pointLabel);
        }

        if (isJapanese) {
            String history = lines.isEmpty() ? "まだ評価した映画がありません。" : String.join(", ", lines);
            return "以下の映画を評価したユーザーです。\n[" + history + "]\n\n"
                    + "この趣味に合わせて、おすすめ映画を5本選んでください。"
                    + "title は映画の原題（英語）で、reason は必ず日本語で一行のおすすめ理由を書いてください。"
                    + "応答は必ず次のJSON配列のみを出力してください。説明やマークダウンは不要です。\n"
                    + "[{\"title\": \"映画の英語タイトル\", \"reason\": \"日本語で一行のおすすめ理由\"}]";
        }
        String history = lines.isEmpty() ? "아직 평가한 영화가 없습니다." : String.join(", ", lines);
        return "다음 영화를 보고 평가한 사용자입니다.\n[" + history + "]\n\n"
                + "이 취향을 반영해서 추천할 영화 5개 골라 주세요. "
                + "title은 영화 제목, reason은 반드시 한국어로 한 줄 추천 이유를 작성하세요. "
                + "응답은 반드시 아래 형식의 JSON 배열만 출력하세요. 다른 설명이나 마크다운 없이 배열만.\n"
                + "[{\"title\": \"영화제목\", \"reason\": \"한국어 한 줄 추천 이유\"}]";
    }

    /** 추천 결과 조회 (결과 페이지용). 현재 로케일에 맞춰 TMDB에서 제목·포스터를 가져와 표시. */
    public RecommendResultDto getRecommendation(Long userId) {
        RecommendResultDto dto = new RecommendResultDto();
        dto.setItems(new ArrayList<>());
        if (userId == null) return dto;
        Recommendation rec = recommendationMapper.findValidByUserId(userId);
        if (rec == null) return dto;

        String lang = TmdbLocaleUtil.getCurrentTmdbLanguage();
        List<RecommendationItem> items = recommendationItemMapper.findByRecommendationId(rec.getId());
        for (RecommendationItem item : items) {
            Movie movie = movieMapper.findById(item.getMovieId());
            RecommendResultDto.RecommendItemDto row = new RecommendResultDto.RecommendItemDto();
            row.setMovieId(item.getMovieId());
            row.setReason(item.getReason());

            if (movie != null && movie.getApiId() != null && !movie.getApiId().isBlank()) {
                try {
                    int apiId = Integer.parseInt(movie.getApiId());
                    row.setMovieDetailId(apiId);
                    TmdbMovieDetailResponse detail = movieApiClient.fetchMovieDetail(apiId, lang);
                    if (detail != null) {
                        row.setTitle(detail.getTitle() != null ? detail.getTitle() : movie.getTitle());
                        row.setPosterUrl(detail.getPosterPath() != null
                                ? "https://image.tmdb.org/t/p/w500" + detail.getPosterPath()
                                : movie.getPosterUrl());
                    } else {
                        row.setTitle(movie.getTitle());
                        row.setPosterUrl(movie.getPosterUrl());
                    }
                } catch (NumberFormatException ignored) {
                    row.setTitle(movie.getTitle());
                    row.setPosterUrl(movie.getPosterUrl());
                }
            } else {
                row.setTitle(movie != null ? movie.getTitle() : "");
                row.setPosterUrl(movie != null ? movie.getPosterUrl() : null);
            }
            dto.getItems().add(row);
        }
        return dto;
    }
}