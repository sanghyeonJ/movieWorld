package com.movieWorld.util;

import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

/**
 * 현재 로케일을 TMDB API language 파라미터로 변환
 * - 컨트롤러/서비스에서 공통 사용
 */
public final class TmdbLocaleUtil {

    private TmdbLocaleUtil() {}

    /**
     * 현재 요청의 로케일 기준 TMDB 언어 코드
     * ko → ko-KR, ja → ja-JP, 그 외 → ko-KR
     */
    public static String getCurrentTmdbLanguage() {
        Locale locale = LocaleContextHolder.getLocale();
        return toTmdbLanguage(locale);
    }

    /**
     * Locale을 TMDB language 파라미터로 변환
     */
    public static String toTmdbLanguage(Locale locale) {
        if (locale == null) return "ko-KR";
        String lang = locale.getLanguage();
        if ("ko".equals(lang)) return "ko-KR";
        if ("ja".equals(lang)) return "ja-JP";
        return "ko-KR";
    }
}