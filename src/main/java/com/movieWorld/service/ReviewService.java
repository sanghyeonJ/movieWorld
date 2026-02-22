package com.movieWorld.service;

import com.movieWorld.domain.Movie;
import com.movieWorld.domain.Review;
import com.movieWorld.dto.request.ReviewRequest;
import com.movieWorld.dto.response.ReviewListDto;
import com.movieWorld.mapper.ReviewMapper;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 리뷰 작성/조회
 */
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewMapper reviewMapper;
    private final MovieService movieService;

    /**
     * 상세 페이지에서 리뷰 작성 (TMDB id 기준)
     * 영화는 없으면 DB에 한 건 생성 후 리뷰 저장.
     */
    @Transactional
    public void saveReview(Long userId, ReviewRequest request) {
        Movie movie = movieService.findOrCreateByApiId(request.getMovieId());
        if (movie == null) {
            throw new IllegalArgumentException("영화 정보를 찾을 수 없습니다.");
        }
        Review review = new Review();
        review.setUserId(userId);
        review.setMovieId(movie.getId());
        review.setRating(request.getRating());
        review.setContent(request.getContent().trim());
        reviewMapper.insert(review);
    }
    
    /** 영화별 리뷰 목록 (상세 페이지용) */
    public List<ReviewListDto> getReviewListByMovieId(Long movieId) {
        if (movieId == null) return List.of();
        return reviewMapper.findReviewListByMovieId(movieId);
    }
}