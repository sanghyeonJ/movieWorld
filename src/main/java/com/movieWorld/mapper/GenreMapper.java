package com.movieWorld.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.movieWorld.domain.Genre;

@Mapper
public interface GenreMapper {

	/** 장르 등록 */
    void insert(Genre genre);

    /** 장르 ID로 조회 */
	Genre findById(Long id);

    /** API 장르 ID로 조회 (동기화 시 중복 확인용) */
    Genre findByApiId(String apiId);

    /** 영화 ID로 해당 영화의 장르 목록 조회 */
    List<Genre> findByMovieId(Long movieId);
    
}
