package com.movieWorld.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.movieWorld.domain.Review;
import com.movieWorld.dto.response.ReviewListDto;

@Mapper
public interface ReviewMapper {

	/** 리뷰 등록 */
    void insert(Review review);

    /** 리뷰 수정 */
    void update(Review review);

    /** 리뷰 ID로 삭제 */
    void deleteById(Long id);

    /** 리뷰 ID로 조회 */
    Review findById(Long id);

    /** 사용자별 리뷰 개수 (추천 조건 검증용) */
    int countByUserId(Long userId);

    /** 사용자별 리뷰 목록 조회 */
    List<Review> findByUserId(Long userId);

    /** 영화별 리뷰 목록 조회 */
    List<Review> findByMovieId(Long movieId);
    
    /** 영화별 리뷰 목록 (작성자명 포함, 상세 페이지용) */
    List<ReviewListDto> findReviewListByMovieId(Long movieId);

    /** 전체 리뷰 수 (관리자 대시보드용) */
    int count();
}
