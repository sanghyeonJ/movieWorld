package com.movieWorld.service;

import com.movieWorld.domain.Genre;
import com.movieWorld.domain.Movie;
import com.movieWorld.domain.MovieGenre;
import com.movieWorld.dto.api.TmdbMovieDetailResponse;
import com.movieWorld.api.MovieApiClient;
import com.movieWorld.mapper.GenreMapper;
import com.movieWorld.mapper.MovieGenreMapper;
import com.movieWorld.mapper.MovieMapper;
import com.movieWorld.util.TmdbLocaleUtil;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * 영화 조회 / 리뷰 작성 시 TMDB id로 DB 영화 1건 확보
 */
@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieMapper movieMapper;
    private final GenreMapper genreMapper;
    private final MovieGenreMapper movieGenreMapper;
    private final MovieApiClient movieApiClient;

    /**
     * TMDB 영화 ID로 우리 DB 영화 조회.
     * 없으면 API 호출 후 DB에 저장하고 반환.
     * (리뷰 저장 시 사용)
     */
    @Transactional
    public Movie findOrCreateByApiId(Integer apiId) {
        String apiIdStr = String.valueOf(apiId);
        Movie existing = movieMapper.findByApiId(apiIdStr);
        if (existing != null) {
            return existing;
        }
        String lang = TmdbLocaleUtil.getCurrentTmdbLanguage();
        TmdbMovieDetailResponse dto = movieApiClient.fetchMovieDetail(apiId, lang);
        if (dto == null) return null;

        Movie movie = new Movie();
        movie.setApiId(apiIdStr);
        movie.setTitle(dto.getTitle());
        movie.setOriginalTitle(dto.getOriginalTitle());
        movie.setOverview(dto.getOverview());
        movie.setPosterUrl(dto.getPosterPath() != null ? "https://image.tmdb.org/t/p/w500" + dto.getPosterPath() : null);
        movie.setBackdropUrl(dto.getBackdropPath() != null ? "https://image.tmdb.org/t/p/original" + dto.getBackdropPath() : null);
        if (dto.getVoteAverage() != null) {
            movie.setRating(BigDecimal.valueOf(dto.getVoteAverage()).setScale(1, RoundingMode.HALF_UP));
        } else {
            movie.setRating(null);
        }
        movie.setVoteCount(dto.getVoteCount() != null ? dto.getVoteCount() : 0);
        if (dto.getReleaseDate() != null && !dto.getReleaseDate().isEmpty()) {
            try {
                movie.setReleaseDate(LocalDate.parse(dto.getReleaseDate(), DateTimeFormatter.ISO_LOCAL_DATE));
            } catch (Exception ignored) {}
        }
        movie.setSyncedAt(LocalDateTime.now());
        movieMapper.insert(movie);

        if (dto.getGenres() != null) {
            for (TmdbMovieDetailResponse.GenreInfo g : dto.getGenres()) {
                Genre genre = genreMapper.findByApiId(String.valueOf(g.getId()));
                if (genre == null) {
                    genre = new Genre();
                    genre.setApiId(String.valueOf(g.getId()));
                    genre.setName(g.getName());
                    genreMapper.insert(genre);
                }
                MovieGenre mg = new MovieGenre();
                mg.setMovieId(movie.getId());
                mg.setGenreId(genre.getId());
                movieGenreMapper.insert(mg);
            }
        }
        return movie;
    }
}