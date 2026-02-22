package com.movieWorld.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 상세 페이지 리뷰 목록 한 줄 (작성자명, 평점, 내용, 작성일)
 */
@Data
public class ReviewListDto {

    private Long id;
    private String userName;   // 작성자 이름
    private Integer rating;
    private String content;
    private LocalDateTime createdAt;
}