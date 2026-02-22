package com.movieWorld.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class RecommendRateLimit {

	private Long id;
    private Long userId;
    private LocalDate date;
    private Integer count;
    private LocalDateTime lastRequestAt;
    private LocalDateTime createdAt;
	
}
