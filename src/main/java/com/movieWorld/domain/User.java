package com.movieWorld.domain;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class User {

	private Long id;
    private String email;
    private String password;
    private String name;
    private Long roleId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
	
}
