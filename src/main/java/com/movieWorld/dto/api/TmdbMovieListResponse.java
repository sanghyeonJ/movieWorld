package com.movieWorld.dto.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * TMDB 인기 영화 API 응답 전체
 * GET /movie/popular 응답 구조
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TmdbMovieListResponse {

    @JsonProperty("results")
    private List<TmdbMovieResult> results;
    
    @JsonProperty("total_pages")
    private Integer totalPages;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TmdbMovieResult {
        @JsonProperty("id")
        private Integer id;

        @JsonProperty("title")
        private String title;

        @JsonProperty("poster_path")
        private String posterPath;

        @JsonProperty("release_date")
        private String releaseDate;

        @JsonProperty("vote_average")
        private Double voteAverage;

        @JsonProperty("overview")
        private String overview;

        @JsonProperty("genre_ids")
        private List<Integer> genreIds;
        
    }
}