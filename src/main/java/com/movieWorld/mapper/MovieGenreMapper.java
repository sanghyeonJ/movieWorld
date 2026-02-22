package com.movieWorld.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.movieWorld.domain.MovieGenre;

@Mapper
public interface MovieGenreMapper {

	/** 영화-장르 매핑 등록 */
    void insert(MovieGenre movieGenre);

    /** 영화 ID로 해당 영화의 장르 매핑 전체 삭제 */
    void deleteByMovieId(Long movieId);

    /** 영화 ID로 장르 매핑 목록 조회 */
    List<MovieGenre> findByMovieId(Long movieId);
	
}
