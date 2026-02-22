package com.movieWorld.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.movieWorld.domain.Movie;

@Mapper
public interface MovieMapper {

	/** 영화 등록 */
    void insert(Movie movie);

    /** 영화 정보 수정 */
    void update(Movie movie);

    /** 영화 ID로 조회 */
    Movie findById(Long id);

    /** API 영화 ID로 조회 (동기화 시 중복 확인용) */
    Movie findByApiId(String apiId);

    /** 전체 영화 목록 조회 */
    List<Movie> findAll();

    /** 제목에 키워드가 포함된 영화 검색 (AI 추천 제목 → DB 매칭용, 최대 1건) */
    List<Movie> findByTitleContaining(@Param("keyword") String keyword);

    /** 전체 영화 수 (관리자 대시보드용) */
    int count();
}
