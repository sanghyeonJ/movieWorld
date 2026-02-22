package com.movieWorld.service;

import com.movieWorld.api.MovieApiClient;
import com.movieWorld.domain.Movie;
import com.movieWorld.domain.MovieSyncLog;
import com.movieWorld.dto.api.TmdbMovieListResponse;
import com.movieWorld.mapper.MovieSyncLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 영화 동기화 서비스 (TMDB → DB)
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MovieSyncService {

    private final MovieApiClient movieApiClient;
    private final MovieService movieService;
    private final MovieSyncLogMapper movieSyncLogMapper;

    /**
     * TMDB 인기 영화 동기화 (벌크)
     * @param maxPages 최대 페이지 수 (1페이지 = 20편)
     * @param language 언어 코드 (예: "ko-KR")
     * @return 동기화 로그
     */
    @Transactional
    public MovieSyncLog syncBulk(int maxPages, String language) {
        MovieSyncLog syncLog = new MovieSyncLog();
        syncLog.setSyncType("BULK");
        syncLog.setTotalCount(0);
        syncLog.setSuccessCount(0);
        syncLog.setFailCount(0);
        syncLog.setStartedAt(LocalDateTime.now());
        syncLog.setStatus("RUNNING");
        movieSyncLogMapper.insert(syncLog);

        try {
            int totalProcessed = 0;
            int success = 0;
            int fail = 0;

            for (int page = 1; page <= maxPages; page++) {
                TmdbMovieListResponse response = movieApiClient.fetchPopularMovies(language, page);
                if (response == null || response.getResults() == null || response.getResults().isEmpty()) {
                    break;
                }

                for (TmdbMovieListResponse.TmdbMovieResult result : response.getResults()) {
                    if (result.getId() == null) continue;
                    totalProcessed++;
                    try {
                        Movie movie = movieService.findOrCreateByApiId(result.getId());
                        if (movie != null) {
                            success++;
                        } else {
                            fail++;
                        }
                    } catch (Exception e) {
                        log.warn("영화 동기화 실패 (TMDB id: {}): {}", result.getId(), e.getMessage());
                        fail++;
                    }
                }

                if (page % 5 == 0) {
                    Thread.sleep(200);
                }
            }

            syncLog.setTotalCount(totalProcessed);
            syncLog.setSuccessCount(success);
            syncLog.setFailCount(fail);
            syncLog.setCompletedAt(LocalDateTime.now());
            syncLog.setStatus("COMPLETED");
            movieSyncLogMapper.update(syncLog);

            log.info("영화 동기화 완료: 전체={}, 성공={}, 실패={}", totalProcessed, success, fail);
            return syncLog;
        } catch (Exception e) {
            syncLog.setCompletedAt(LocalDateTime.now());
            syncLog.setStatus("FAILED");
            syncLog.setErrorMessage(e.getMessage());
            movieSyncLogMapper.update(syncLog);
            log.error("영화 동기화 실패", e);
            throw new RuntimeException("동기화 중 오류 발생", e);
        }
    }

    /**
     * 현재 상영작 동기화
     * @param language 언어 코드 (예: "ko-KR")
     * @return 동기화 로그
     */
    @Transactional
    public MovieSyncLog syncNowPlaying(String language) {
        MovieSyncLog syncLog = new MovieSyncLog();
        syncLog.setSyncType("NOW_PLAYING");
        syncLog.setTotalCount(0);
        syncLog.setSuccessCount(0);
        syncLog.setFailCount(0);
        syncLog.setStartedAt(LocalDateTime.now());
        syncLog.setStatus("RUNNING");
        movieSyncLogMapper.insert(syncLog);

        try {
            int totalProcessed = 0;
            int success = 0;
            int fail = 0;

            TmdbMovieListResponse response = movieApiClient.fetchNowPlaying(language);
            if (response != null && response.getResults() != null) {
                for (TmdbMovieListResponse.TmdbMovieResult result : response.getResults()) {
                    if (result.getId() == null) continue;
                    totalProcessed++;
                    try {
                        Movie movie = movieService.findOrCreateByApiId(result.getId());
                        if (movie != null) {
                            success++;
                        } else {
                            fail++;
                        }
                    } catch (Exception e) {
                        log.warn("영화 동기화 실패 (TMDB id: {}): {}", result.getId(), e.getMessage());
                        fail++;
                    }
                }
            }

            syncLog.setTotalCount(totalProcessed);
            syncLog.setSuccessCount(success);
            syncLog.setFailCount(fail);
            syncLog.setCompletedAt(LocalDateTime.now());
            syncLog.setStatus("COMPLETED");
            movieSyncLogMapper.update(syncLog);

            log.info("현재 상영작 동기화 완료: 전체={}, 성공={}, 실패={}", totalProcessed, success, fail);
            return syncLog;
        } catch (Exception e) {
            syncLog.setCompletedAt(LocalDateTime.now());
            syncLog.setStatus("FAILED");
            syncLog.setErrorMessage(e.getMessage());
            movieSyncLogMapper.update(syncLog);
            log.error("현재 상영작 동기화 실패", e);
            throw new RuntimeException("동기화 중 오류 발생", e);
        }
    }

    /**
     * 개봉 예정작 동기화
     * @param language 언어 코드 (예: "ko-KR")
     * @return 동기화 로그
     */
    @Transactional
    public MovieSyncLog syncUpcoming(String language) {
        MovieSyncLog syncLog = new MovieSyncLog();
        syncLog.setSyncType("UPCOMING");
        syncLog.setTotalCount(0);
        syncLog.setSuccessCount(0);
        syncLog.setFailCount(0);
        syncLog.setStartedAt(LocalDateTime.now());
        syncLog.setStatus("RUNNING");
        movieSyncLogMapper.insert(syncLog);

        try {
            int totalProcessed = 0;
            int success = 0;
            int fail = 0;

            TmdbMovieListResponse response = movieApiClient.fetchUpcoming(language);
            if (response != null && response.getResults() != null) {
                for (TmdbMovieListResponse.TmdbMovieResult result : response.getResults()) {
                    if (result.getId() == null) continue;
                    totalProcessed++;
                    try {
                        Movie movie = movieService.findOrCreateByApiId(result.getId());
                        if (movie != null) {
                            success++;
                        } else {
                            fail++;
                        }
                    } catch (Exception e) {
                        log.warn("영화 동기화 실패 (TMDB id: {}): {}", result.getId(), e.getMessage());
                        fail++;
                    }
                }
            }

            syncLog.setTotalCount(totalProcessed);
            syncLog.setSuccessCount(success);
            syncLog.setFailCount(fail);
            syncLog.setCompletedAt(LocalDateTime.now());
            syncLog.setStatus("COMPLETED");
            movieSyncLogMapper.update(syncLog);

            log.info("개봉 예정작 동기화 완료: 전체={}, 성공={}, 실패={}", totalProcessed, success, fail);
            return syncLog;
        } catch (Exception e) {
            syncLog.setCompletedAt(LocalDateTime.now());
            syncLog.setStatus("FAILED");
            syncLog.setErrorMessage(e.getMessage());
            movieSyncLogMapper.update(syncLog);
            log.error("개봉 예정작 동기화 실패", e);
            throw new RuntimeException("동기화 중 오류 발생", e);
        }
    }
}
