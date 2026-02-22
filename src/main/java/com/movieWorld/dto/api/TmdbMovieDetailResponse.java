package com.movieWorld.dto.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * TMDB 영화 상세 API 응답
 * GET /movie/{id}
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TmdbMovieDetailResponse {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("original_title")
    private String originalTitle;

    @JsonProperty("overview")
    private String overview;

    @JsonProperty("poster_path")
    private String posterPath;

    @JsonProperty("backdrop_path")
    private String backdropPath;

    @JsonProperty("release_date")
    private String releaseDate;

    @JsonProperty("vote_average")
    private Double voteAverage;

    @JsonProperty("vote_count")
    private Integer voteCount;

    @JsonProperty("genres")
    private List<GenreInfo> genres;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GenreInfo {
        @JsonProperty("id")
        private Integer id;
        @JsonProperty("name")
        private String name;
    }
}