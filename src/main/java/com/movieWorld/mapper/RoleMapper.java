package com.movieWorld.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.movieWorld.domain.Role;

@Mapper
public interface RoleMapper {

	// 권한 id로 조회
	Role findById(Long id);
	
	// 권한명으로 조회
	Role findByName(String name);
	
}
