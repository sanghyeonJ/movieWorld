package com.movieWorld.dto.request;

import lombok.Data;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 리뷰 작성/수정 요청
 */
@Data
public class ReviewRequest {

    /** TMDB 영화 ID (상세 페이지에서 넘어온 값) */
    @NotNull(message = "영화 정보가 없습니다.")
    private Integer movieId;

    /** 평점 1~5 */
    @NotNull(message = "평점을 선택하세요.")
    @Min(1) @Max(5)
    private Integer rating;

    /** 리뷰 내용 */
    @NotBlank(message = "리뷰 내용을 입력하세요.")
    private String content;
}