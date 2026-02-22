package com.movieWorld.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BoardCommentListDto {
    private Long id;
    private Long boardId;
    private Long userId;
    private String userName;
    private String content;
    private LocalDateTime createdAt;
}