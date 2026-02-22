package com.movieWorld.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 게시판 목록 한 줄 (작성자명 포함)
 */
@Data
public class BoardListDto {

    private Long id;
    private Long userId;
    private String title;
    private String userName;
    private Integer viewCount;
    private LocalDateTime createdAt;
}