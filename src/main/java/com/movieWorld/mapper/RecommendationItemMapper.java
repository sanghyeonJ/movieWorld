package com.movieWorld.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.movieWorld.domain.RecommendationItem;

@Mapper
public interface RecommendationItemMapper {

	/** 추천 항목(영화 1개) 등록 */
    void insert(RecommendationItem item);

    /** 추천 ID로 추천된 영화 목록 조회 */
    List<RecommendationItem> findByRecommendationId(Long recommendationId);
	
}
