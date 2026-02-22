package com.movieWorld.controller;

import com.movieWorld.api.MovieApiClient;
import com.movieWorld.util.TmdbLocaleUtil;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {
	
	private final MovieApiClient movieApiClient;

	@GetMapping("/")
	public String home(org.springframework.ui.Model model) {
		// 인기 영화 (API)
		String lang = TmdbLocaleUtil.getCurrentTmdbLanguage();
	    var popular = movieApiClient.fetchPopularMovies(lang, 1);
        model.addAttribute("popularMovies", popular != null ? popular.getResults() : List.of());

        // 현재 상영작 = 최신 개봉작 느낌으로 사용 가능
        var nowPlaying = movieApiClient.fetchNowPlaying(lang);
        model.addAttribute("latestMovies", nowPlaying != null ? nowPlaying.getResults() : List.of());
		return "home";
	}
	
	@GetMapping("/login")
	public String loginPage() {
		return "login";
	}
	
}
