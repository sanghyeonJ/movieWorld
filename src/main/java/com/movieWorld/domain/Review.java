package com.movieWorld.domain;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Review {

	private Long id;
    private Long userId;
    private Long movieId;
    private Integer rating;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
	
}
