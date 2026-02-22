package com.movieWorld.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.movieWorld.dto.api.AiRecommendItemDto;
import com.movieWorld.dto.api.GeminiRequestDto;
import com.movieWorld.dto.api.GeminiResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class GeminiRecommendClient {

    private static final String BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${app.recommend.gemini.api-key:}")
    private String apiKey;

    @Value("${app.recommend.gemini.model:gemini-2.5-flash}")
    private String model;

    public List<AiRecommendItemDto> getRecommendations(String prompt) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("Gemini API 키가 설정되지 않아 추천을 건너뜁니다.");
            return Collections.emptyList();
        }
        String url = BASE_URL + "/" + model + ":generateContent?key=" + apiKey;

        GeminiRequestDto body = GeminiRequestDto.builder()
                .contents(List.of(
                        GeminiRequestDto.Content.builder()
                                .parts(List.of(GeminiRequestDto.Part.builder().text(prompt).build()))
                                .build()
                ))
                .generationConfig(GeminiRequestDto.GenerationConfig.builder()
                        .maxOutputTokens(8192)
                        .temperature(0.7)
                        .build())
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<GeminiRequestDto> entity = new HttpEntity<>(body, headers);

        String rawBody;
        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            );
            rawBody = responseEntity.getBody();
        } catch (HttpStatusCodeException e) {
            if (e.getStatusCode().value() == 429) {
                log.warn("Gemini API 429 (한도 초과). 프로젝트 한도 확인: https://aistudio.google.com/usage");
            } else {
                log.warn("Gemini API HTTP 오류: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString());
            }
            return Collections.emptyList();
        } catch (Exception e) {
            log.warn("Gemini API 호출 실패", e);
            return Collections.emptyList();
        }

        if (rawBody == null || rawBody.isBlank()) {
            log.warn("Gemini API 응답 본문이 비어 있습니다.");
            return Collections.emptyList();
        }

        GeminiResponseDto response;
        try {
            response = OBJECT_MAPPER.readValue(rawBody, GeminiResponseDto.class);
        } catch (Exception e) {
            log.warn("Gemini 응답 JSON 파싱 실패. raw(앞 500자): {}", rawBody.length() > 500 ? rawBody.substring(0, 500) + "..." : rawBody, e);
            return Collections.emptyList();
        }

        if (response == null || response.getCandidates() == null || response.getCandidates().isEmpty()) {
            log.warn("Gemini 응답에 candidates 없음. raw(앞 500자): {}", rawBody.length() > 500 ? rawBody.substring(0, 500) + "..." : rawBody);
            return Collections.emptyList();
        }

        String text;
        try {
            text = response.getCandidates().get(0).getContent().getParts().get(0).getText();
        } catch (Exception e) {
            log.warn("Gemini 응답에서 text 추출 실패. raw(앞 500자): {}", rawBody.length() > 500 ? rawBody.substring(0, 500) + "..." : rawBody, e);
            return Collections.emptyList();
        }

        if (text == null || text.isBlank()) {
            log.warn("Gemini 생성 텍스트가 비어 있습니다.");
            return Collections.emptyList();
        }

        log.info("Gemini raw: {}", text.length() > 300 ? text + "..." : text);

        text = text.trim();
        if (text.startsWith("```")) {
            text = text.replaceFirst("^```(?:json)?\\s*", "").replaceFirst("\\s*```$", "").trim();
        }
        try {
            return OBJECT_MAPPER.readValue(text, new TypeReference<>() {});
        } catch (Exception e) {
            log.warn("Gemini 추천 JSON 배열 파싱 실패. text: {}", text.length() > 300 ? text.substring(0, 300) + "..." : text, e);
            return Collections.emptyList();
        }
    }
}
