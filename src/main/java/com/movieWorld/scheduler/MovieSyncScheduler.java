package com.movieWorld.scheduler;

import com.movieWorld.config.SchedulerProperties;
import com.movieWorld.service.MovieSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 영화 동기화 스케줄러
 * 프로퍼티스 설정에 따라 자동으로 TMDB 영화 데이터를 동기화합니다.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class MovieSyncScheduler {

    private final MovieSyncService movieSyncService;
    private final SchedulerProperties schedulerProperties;

    /** 기동 후 1회 동기화 (run-on-startup=true일 때만, 30초 뒤 실행) */
    @EventListener(ApplicationReadyEvent.class)
    public void runSyncOnStartup() {
        if (!schedulerProperties.isRunOnStartup()) {
            return;
        }
        new Thread(() -> {
            try {
                Thread.sleep(30_000L);
                if (!schedulerProperties.isEnabled()) {
                    return;
                }
                log.info("기동 시 1회 동기화 시작 (run-on-startup)");
                movieSyncService.syncBulk(5, "ko-KR");
                log.info("기동 시 1회 동기화 완료");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("기동 시 동기화 대기 중 인터럽트", e);
            } catch (Exception e) {
                log.error("기동 시 1회 동기화 실패", e);
            }
        }, "movie-sync-startup").start();
    }

    /**
     * 인기 영화 동기화 (매일 새벽 2시)
     * cron 표현식: 초 분 시 일 월 요일
     */
    @Scheduled(cron = "${app.scheduler.movie-sync.cron.popular:0 0 2 * * *}")
    public void syncPopularMovies() {
        if (!schedulerProperties.isEnabled()) {
            log.debug("스케줄러가 비활성화되어 있어 인기 영화 동기화를 건너뜁니다.");
            return;
        }

        try {
            log.info("인기 영화 자동 동기화 시작");
            movieSyncService.syncBulk(50, "ko-KR");
            log.info("인기 영화 자동 동기화 완료");
        } catch (Exception e) {
            log.error("인기 영화 자동 동기화 실패", e);
        }
    }

    /**
     * 현재 상영작 동기화 (매일 새벽 3시)
     */
    @Scheduled(cron = "${app.scheduler.movie-sync.cron.now-playing:0 0 3 * * *}")
    public void syncNowPlayingMovies() {
        if (!schedulerProperties.isEnabled()) {
            log.debug("스케줄러가 비활성화되어 있어 현재 상영작 동기화를 건너뜁니다.");
            return;
        }

        try {
            log.info("현재 상영작 자동 동기화 시작");
            movieSyncService.syncNowPlaying("ko-KR");
            log.info("현재 상영작 자동 동기화 완료");
        } catch (Exception e) {
            log.error("현재 상영작 자동 동기화 실패", e);
        }
    }

    /**
     * 개봉 예정작 동기화 (매일 새벽 4시)
     */
    @Scheduled(cron = "${app.scheduler.movie-sync.cron.upcoming:0 0 4 * * *}")
    public void syncUpcomingMovies() {
        if (!schedulerProperties.isEnabled()) {
            log.debug("스케줄러가 비활성화되어 있어 개봉 예정작 동기화를 건너뜁니다.");
            return;
        }

        try {
            log.info("개봉 예정작 자동 동기화 시작");
            movieSyncService.syncUpcoming("ko-KR");
            log.info("개봉 예정작 자동 동기화 완료");
        } catch (Exception e) {
            log.error("개봉 예정작 자동 동기화 실패", e);
        }
    }
}
