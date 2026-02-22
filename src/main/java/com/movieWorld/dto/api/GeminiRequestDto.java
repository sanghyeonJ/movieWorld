package com.movieWorld.dto.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Gemini API 요청 Body 구조.
 * POST .../models/{model}:generateContent 에 보낼 JSON과 1:1 매핑.
 * - contents: 사용자 메시지(프롬프트)를 parts[].text 에 넣음
 * - generationConfig: 토큰 수, temperature 등 생성 옵션
 */
@Data
@Builder
public class GeminiRequestDto {

    @JsonProperty("contents")
    private List<Content> contents;

    @JsonProperty("generationConfig")
    private GenerationConfig generationConfig;

    @Data
    @Builder
    public static class Content {
        @JsonProperty("parts")
        private List<Part> parts;
    }

    @Data
    @Builder
    public static class Part {
        @JsonProperty("text")
        private String text;
    }

    @Data
    @Builder
    public static class GenerationConfig {
        @JsonProperty("maxOutputTokens")
        private Integer maxOutputTokens;
        @JsonProperty("temperature")
        private Double temperature;
    }
}
