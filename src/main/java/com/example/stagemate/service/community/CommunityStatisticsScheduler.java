package com.example.stagemate.service.community;

import com.example.stagemate.repository.community.CommunityStatisticsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class CommunityStatisticsScheduler {
    private final CommunityStatisticsRepository communityStatisticsRepository;

    // 애플리케이션 시작 시 커뮤니티 통계 초기화
    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initStatistics() {
        updateCommunityStatistics();
        log.info("애플리케이션 시작 시 커뮤니티 통계 초기화 완료");
    }

//     1시간마다 스케줄러 실행
    @Transactional
    @Scheduled(cron = "0 0 */1 * * *")
    public void updateCommunityStatistics() {
        // 기존 통계 삭제
        communityStatisticsRepository.deleteAllStatistics();
        // 커뮤니티 통계 업데이트
        communityStatisticsRepository.updateCommunityStatistics();
        log.info("커뮤니티 통계 업데이트 완료.");
    }
}
