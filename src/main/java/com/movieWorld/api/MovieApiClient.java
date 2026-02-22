package com.movieWorld.api;

import com.movieWorld.dto.api.TmdbMovieListResponse;
import com.movieWorld.dto.api.TmdbMovieDetailResponse;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * TMDB API 호출
 * - 인기 영화, 최신 개봉작 등 목록/상세 조회
 */
@Component
@RequiredArgsConstructor
public class MovieApiClient {

    @Value("${app.movie.api.url}")
    private String apiBaseUrl;

    @Value("${app.movie.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 인기 영화 목록 (1페이지)
     * GET /movie/popular
     */
    public TmdbMovieListResponse fetchPopularMovies(String language, int page) {
        if (language == null || language.isEmpty()) language = "ko-KR";
        String today = LocalDate.now().toString();
        String url = apiBaseUrl + "/discover/movie?api_key=" + apiKey
                + "&language=" + language
                + "&sort_by=popularity.desc"
                + "&release_date.lte=" + today
                + "&page=" + page;
        return restTemplate.getForObject(url, TmdbMovieListResponse.class);
    }

    /**
     * 현재 상영작
     * GET /movie/now_playing
     */
    public TmdbMovieListResponse fetchNowPlaying(String language) {
    	if (language == null || language.isEmpty()) language = "ko-KR";
        String url = apiBaseUrl + "/movie/now_playing?api_key=" + apiKey + "&language=" + language + "&page=1";
        return restTemplate.getForObject(url, TmdbMovieListResponse.class);
    }

    /**
     * 개봉 예정작
     * GET /movie/upcoming
     */
    public TmdbMovieListResponse fetchUpcoming(String language) {
        if (language == null || language.isEmpty()) language = "ko-KR";
        String url = apiBaseUrl + "/movie/upcoming?api_key=" + apiKey + "&language=" + language + "&page=1";
        return restTemplate.getForObject(url, TmdbMovieListResponse.class);
    }
    
    /**
     * 영화 상세 (한 편)
     * GET /movie/{id}
     */
    public TmdbMovieDetailResponse fetchMovieDetail(Integer movieId, String language) {
    	if (language == null || language.isEmpty()) language = "ko-KR";
        String url = apiBaseUrl + "/movie/" + movieId + "?api_key=" + apiKey + "&language=" + language;
        return restTemplate.getForObject(url, TmdbMovieDetailResponse.class);
    }
    
    /**
     * 영화 검색 (제목)
     * GET /search/movie
     */
    public TmdbMovieListResponse searchMovies(String query, String language, int page) {
        if (language == null || language.isEmpty()) language = "ko-KR";
        if (query == null || query.trim().isEmpty()) return fetchPopularMovies(language, page);
        String encodedQuery = java.net.URLEncoder.encode(query.trim(), java.nio.charset.StandardCharsets.UTF_8);
        String url = apiBaseUrl + "/search/movie?api_key=" + apiKey + "&language=" + language + "&query=" + encodedQuery + "&page=" + page;
        return restTemplate.getForObject(url, TmdbMovieListResponse.class);
    }
    
    /** 최신순 - GET /discover/movie?sort_by=release_date.desc */
    public TmdbMovieListResponse fetchMoviesByLatest(String language, int page) {
        if (language == null || language.isEmpty()) language = "ko-KR";
        String today = LocalDate.now().toString(); 
        String url = apiBaseUrl + "/discover/movie?api_key=" + apiKey
                + "&language=" + language
                + "&sort_by=release_date.desc"
                + "&release_date.lte=" + today
                + "&page=" + page;
        return restTemplate.getForObject(url, TmdbMovieListResponse.class);
    }
    
    /** 평점순 - GET /discover/movie?sort_by=vote_average.desc */
    public TmdbMovieListResponse fetchMoviesByRating(String language, int page) {
        if (language == null || language.isEmpty()) language = "ko-KR";
        String today = LocalDate.now().toString(); 
        String url = apiBaseUrl + "/discover/movie?api_key=" + apiKey
                + "&language=" + language
                + "&sort_by=vote_average.desc"
                + "&release_date.lte=" + today
                + "&page=" + page;
        return restTemplate.getForObject(url, TmdbMovieListResponse.class);
    }
    
}