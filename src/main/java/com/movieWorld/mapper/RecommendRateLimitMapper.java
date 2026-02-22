package com.movieWorld.mapper;

import java.time.LocalDate;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.movieWorld.domain.RecommendRateLimit;

@Mapper
public interface RecommendRateLimitMapper {

	/** 사용자 ID + 날짜로 조회 (일별 제한 확인용) */
    RecommendRateLimit findByUserIdAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    /** 호출 이력 등록 (첫 요청 시) */
    void insert(RecommendRateLimit rateLimit);

    /** 호출 횟수 증가 (같은 날 재요청 시) */
    void updateCount(@Param("userId") Long userId, @Param("date") LocalDate date);
	
}
