package com.movieWorld.domain;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class BoardComment {

	private Long id;
    private Long boardId;
    private Long userId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
	
}
