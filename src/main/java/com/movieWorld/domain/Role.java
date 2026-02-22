package com.movieWorld.domain;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Role {
	
	private Long id;
	private String name;
	private LocalDateTime createdAt;

}
