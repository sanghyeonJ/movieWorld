package com.movieWorld.dto.response;

import lombok.Data;

import java.util.List;

/** 추천 결과 화면용 (추천 영화 목록 + 추천 이유) */
@Data
public class RecommendResultDto {

    private List<RecommendItemDto> items;

    @Data
    public static class RecommendItemDto {
        private Long movieId;
        /** 상세 페이지 링크용 (TMDB id). /movies/{id} 에 사용 */
        private Integer movieDetailId;
        private String title;
        private String posterUrl;
        private String reason;
    }
}