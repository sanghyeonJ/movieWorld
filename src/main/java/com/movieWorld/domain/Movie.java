package com.movieWorld.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Movie {

	private Long id;
    private String apiId;
    private String title;
    private String originalTitle;
    private String posterUrl;
    private String backdropUrl;
    private String overview;
    private LocalDate releaseDate;
    private BigDecimal rating;
    private Integer voteCount;
    private LocalDateTime syncedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
	
}
