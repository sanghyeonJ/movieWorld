package com.movieWorld.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 스케줄러 설정 프로퍼티스
 */
@Component
@ConfigurationProperties(prefix = "app.scheduler.movie-sync")
public class SchedulerProperties {

    /** 자동 동기화 사용 여부 */
    private boolean enabled = true;

    /** 기동 시 1회 동기화 실행 (개발/검증용, 기본 false) */
    private boolean runOnStartup = false;

    /** 인기 영화 동기화 cron 표현식 */
    private String cronPopular = "0 0 2 * * *";

    /** 현재 상영작 동기화 cron 표현식 */
    private String cronNowPlaying = "0 0 3 * * *";

    /** 개봉 예정작 동기화 cron 표현식 */
    private String cronUpcoming = "0 0 4 * * *";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isRunOnStartup() {
        return runOnStartup;
    }

    public void setRunOnStartup(boolean runOnStartup) {
        this.runOnStartup = runOnStartup;
    }

    public String getCronPopular() {
        return cronPopular;
    }

    public void setCronPopular(String cronPopular) {
        this.cronPopular = cronPopular;
    }

    public String getCronNowPlaying() {
        return cronNowPlaying;
    }

    public void setCronNowPlaying(String cronNowPlaying) {
        this.cronNowPlaying = cronNowPlaying;
    }

    public String getCronUpcoming() {
        return cronUpcoming;
    }

    public void setCronUpcoming(String cronUpcoming) {
        this.cronUpcoming = cronUpcoming;
    }
}
