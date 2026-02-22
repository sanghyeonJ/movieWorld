package com.movieWorld.domain;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class MovieSyncLog {

	private Long id;
    private String syncType;
    private Integer totalCount;
    private Integer successCount;
    private Integer failCount;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private String status;
    private String errorMessage;
	
}
