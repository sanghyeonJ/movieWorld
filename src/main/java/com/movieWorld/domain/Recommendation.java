package com.movieWorld.domain;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Recommendation {

	private Long id;
    private Long userId;
    private Integer reviewCount;
    private LocalDateTime reviewLastUpdatedAt;
    private LocalDateTime createdAt;
    private Boolean isValid;
	
}
