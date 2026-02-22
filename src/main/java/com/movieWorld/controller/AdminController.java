package com.movieWorld.controller;

import com.movieWorld.mapper.BoardMapper;
import com.movieWorld.mapper.MovieMapper;
import com.movieWorld.mapper.MovieSyncLogMapper;
import com.movieWorld.mapper.RecommendationMapper;
import com.movieWorld.mapper.ReviewMapper;
import com.movieWorld.mapper.UserMapper;
import com.movieWorld.service.MovieSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 관리자 전용 컨트롤러 (ADMIN 역할 필요)
 */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserMapper userMapper;
    private final MovieMapper movieMapper;
    private final ReviewMapper reviewMapper;
    private final BoardMapper boardMapper;
    private final RecommendationMapper recommendationMapper;
    private final MovieSyncLogMapper movieSyncLogMapper;
    private final MovieSyncService movieSyncService;
    private final MessageSource messageSource;

    @GetMapping
    public String dashboard(Model model) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("userCount", userMapper.count());
        stats.put("movieCount", movieMapper.count());
        stats.put("reviewCount", reviewMapper.count());
        stats.put("boardCount", boardMapper.countBySearch(""));
        stats.put("recommendationCount", recommendationMapper.count());

        model.addAttribute("stats", stats);
        model.addAttribute("syncLogs", movieSyncLogMapper.findAll().stream().limit(10).collect(Collectors.toList()));
        return "admin/dashboard";
    }

    @PostMapping("/sync")
    public ResponseEntity<Map<String, Object>> syncMovies(@RequestParam(value = "maxPages", defaultValue = "50") int maxPages,
                                                          @RequestParam(value = "language", defaultValue = "ko-KR") String language) {
        Locale locale = LocaleContextHolder.getLocale();
        Map<String, Object> response = new HashMap<>();
        try {
            movieSyncService.syncBulk(maxPages, language);
            response.put("success", true);
            response.put("message", messageSource.getMessage("admin.sync.modal.done", null, locale));
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", messageSource.getMessage("admin.sync.modal.errorWithDetail",
                    new Object[]{e.getMessage() != null ? e.getMessage() : ""}, locale));
        }
        return ResponseEntity.ok(response);
    }
}
