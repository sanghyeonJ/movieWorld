package com.movieWorld.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class RecommendationItem {

	private Long id;
    private Long recommendationId;
    private Long movieId;
    private Integer itemRank;
    private String reason;
    private BigDecimal confidenceScore;
    private LocalDateTime createdAt;
	
}
