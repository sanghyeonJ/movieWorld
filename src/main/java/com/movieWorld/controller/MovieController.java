package com.movieWorld.controller;

import com.movieWorld.api.MovieApiClient;
import com.movieWorld.dto.api.TmdbMovieDetailResponse;
import com.movieWorld.dto.response.ReviewListDto;
import com.movieWorld.service.MovieService;
import com.movieWorld.service.ReviewService;
import com.movieWorld.util.TmdbLocaleUtil;
import com.movieWorld.util.TmdbLocaleUtil;
import com.movieWorld.dto.api.TmdbMovieListResponse;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class MovieController {

    private final MovieApiClient movieApiClient;
    private final MovieService movieService;
    private final ReviewService reviewService;
    
    @GetMapping("/movies")
    public String list(
    		@RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "sort", required = false, defaultValue = "popular") String sort,
            @RequestParam(name = "page", defaultValue = "1") int page,
            Model model
    ) {
        String lang = TmdbLocaleUtil.getCurrentTmdbLanguage();
        TmdbMovieListResponse response;

        if (search != null && !search.trim().isEmpty()) {
            response = movieApiClient.searchMovies(search.trim(), lang, page);
        } else {
            switch (sort != null ? sort : "latest") {
                case "latest":
                    response = movieApiClient.fetchMoviesByLatest(lang, page);
                    break;
                case "rating":
                    response = movieApiClient.fetchMoviesByRating(lang, page);
                    break;
                default:
                	response = movieApiClient.fetchPopularMovies(lang, page);
                    break;
            }
        }

        List<TmdbMovieListResponse.TmdbMovieResult> movies = (response != null && response.getResults() != null)
                ? response.getResults() : List.of();
        int totalPages = (response != null && response.getTotalPages() != null) ? Math.max(1, response.getTotalPages()) : 1;

        model.addAttribute("movies", movies);
        model.addAttribute("searchKeyword", search != null ? search : "");
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentSort", sort != null ? sort : "latest");
        return "movies/list";
    }

    /**
     * 영화 상세 페이지
     * /movies/123 → TMDB id 123 상세 조회
     */
    @GetMapping("/movies/{id}")
    public String detail(@PathVariable("id") Integer id, Model model) {
    	String lang = TmdbLocaleUtil.getCurrentTmdbLanguage();
        TmdbMovieDetailResponse movie = movieApiClient.fetchMovieDetail(id, lang);
        if (movie == null) {
            return "redirect:/";
        }
        model.addAttribute("movie", movie);
        
        // 우리 DB 영화가 있으면 해당 영화 리뷰 목록 조회 (상세 페이지용)
        com.movieWorld.domain.Movie ourMovie = movieService.findOrCreateByApiId(id);
        List<ReviewListDto> reviews = ourMovie != null
                ? reviewService.getReviewListByMovieId(ourMovie.getId())
                : List.of();
        model.addAttribute("reviews", reviews);
        return "movies/detail";
    }
}