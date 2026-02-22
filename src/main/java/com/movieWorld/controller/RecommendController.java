package com.movieWorld.controller;

import com.movieWorld.config.CustomUserDetails;
import com.movieWorld.service.RecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * AI 추천 요청/결과 페이지
 */
@Controller
@RequestMapping("/recommend")
@RequiredArgsConstructor
public class RecommendController {

    private final RecommendService recommendService;

    /** 추천 요청 페이지 (리뷰 개수, 가능 여부 표시) */
    @GetMapping
    public String requestPage(
            @AuthenticationPrincipal CustomUserDetails user,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다.");
            return "redirect:/login";
        }
        Long userId = user.getUserId();
        int reviewCount = recommendService.getReviewCount(userId);
        boolean canRecommend = recommendService.canRecommend(userId);

        model.addAttribute("reviewCount", reviewCount);
        model.addAttribute("canRecommend", canRecommend);
        return "recommend/request";
    }

    /** 추천 실행 후 결과 페이지로 리다이렉트 */
    @PostMapping
    public String requestRecommendation(
            @AuthenticationPrincipal CustomUserDetails user,
            RedirectAttributes redirectAttributes
    ) {
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다.");
            return "redirect:/login";
        }
        try {
            recommendService.requestRecommendation(user.getUserId());
            return "redirect:/recommend/result";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/recommend";
        }
    }

    /** 추천 결과 페이지 */
    @GetMapping("/result")
    public String result(
            @AuthenticationPrincipal CustomUserDetails user,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다.");
            return "redirect:/login";
        }
        var recommendation = recommendService.getRecommendation(user.getUserId());
        model.addAttribute("recommendation", recommendation);
        return "recommend/result";
    }
}