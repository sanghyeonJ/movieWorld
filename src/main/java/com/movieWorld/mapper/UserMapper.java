package com.movieWorld.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.movieWorld.domain.User;

@Mapper
public interface UserMapper {

	// 회원등록
	void insert(User user);
	
	// 회원 id로 조회
	User findById(Long id);
	
	// 회원 이메일로 조회
	User findByEmail(String email);

	/** 전체 회원 수 (관리자 대시보드용) */
	int count();
}
