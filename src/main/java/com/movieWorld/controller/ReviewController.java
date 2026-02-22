package com.movieWorld.controller;

import com.movieWorld.config.CustomUserDetails;
import com.movieWorld.dto.request.ReviewRequest;
import com.movieWorld.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

/**
 * 리뷰 작성 (상세 페이지에서 폼 제출)
 */
@Controller
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public String write(
            @AuthenticationPrincipal CustomUserDetails user,
            @Valid @ModelAttribute ReviewRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "로그인이 필요합니다.");
            return "redirect:/login";
        }
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "입력값을 확인하세요.");
            return "redirect:/movies/" + request.getMovieId();
        }
        try {
            reviewService.saveReview(user.getUserId(), request);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/movies/" + request.getMovieId();
    }
}