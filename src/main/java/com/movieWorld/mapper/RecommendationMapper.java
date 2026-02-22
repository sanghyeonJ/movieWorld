package com.movieWorld.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.movieWorld.domain.Recommendation;

@Mapper
public interface RecommendationMapper {

	/** 추천 결과 등록 (캐시 저장) */
    void insert(Recommendation recommendation);

    /** 사용자별 기존 추천 결과 무효화 (리뷰 변경 시 호출) */
    void invalidateByUserId(Long userId);

    /** 사용자의 유효한 추천 결과 1건 조회 (캐시 조회용) */
    Recommendation findValidByUserId(Long userId);

    /** 전체 추천 결과 수 (관리자 대시보드용) */
    int count();
}
