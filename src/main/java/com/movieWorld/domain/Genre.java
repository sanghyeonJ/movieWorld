package com.movieWorld.domain;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Genre {

	private Long id;
    private String apiId;
    private String name;
    private LocalDateTime createdAt;
	
}
