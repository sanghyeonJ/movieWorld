package com.movieWorld.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.movieWorld.domain.MovieSyncLog;

@Mapper
public interface MovieSyncLogMapper {

	/** 동기화 로그 등록 */
    void insert(MovieSyncLog log);

    /** 동기화 로그 수정 (완료 시 success_count, status 등) */
    void update(MovieSyncLog log);

    /** 전체 동기화 로그 목록 조회 (관리자 화면용) */
    List<MovieSyncLog> findAll();
	
}
