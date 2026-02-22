package com.movieWorld.domain;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Board {

	private Long id;
    private Long userId;
    private String title;
    private String content;
    private Integer viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
	
}
