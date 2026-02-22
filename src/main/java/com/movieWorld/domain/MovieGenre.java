package com.movieWorld.domain;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class MovieGenre {

	private Long id;
    private Long movieId;
    private Long genreId;
    private LocalDateTime createdAt;
	
}
