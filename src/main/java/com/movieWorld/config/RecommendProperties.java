package com.movieWorld.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.recommend")
public class RecommendProperties {

	/** 추천 받기 위한 최소 리뷰 개수 (기본 5) */
    private int minReviewCount = 5;

    /** 1일 최대 추천 요청 횟수 */
    private int rateLimitDaily = 10;

    /** 1분당 최대 추천 요청 횟수 */
    private int rateLimitPerMinute = 1;

    /** 추천 결과 캐시 사용 여부 */
    private boolean cacheEnabled = true;

    public int getMinReviewCount() {
        return minReviewCount;
    }

    public void setMinReviewCount(int minReviewCount) {
        this.minReviewCount = minReviewCount;
    }

    public int getRateLimitDaily() {
        return rateLimitDaily;
    }

    public void setRateLimitDaily(int rateLimitDaily) {
        this.rateLimitDaily = rateLimitDaily;
    }

    public int getRateLimitPerMinute() {
        return rateLimitPerMinute;
    }

    public void setRateLimitPerMinute(int rateLimitPerMinute) {
        this.rateLimitPerMinute = rateLimitPerMinute;
    }

    public boolean isCacheEnabled() {
        return cacheEnabled;
    }

    public void setCacheEnabled(boolean cacheEnabled) {
        this.cacheEnabled = cacheEnabled;
    }
	
}
