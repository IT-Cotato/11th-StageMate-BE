package com.example.stagemate.scheduler;

import com.example.stagemate.service.performance.PerformanceCrawlingBatchService;
import com.example.stagemate.service.performance.PerformanceStatisticsBatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class PerformanceScheduler {
    private final PerformanceCrawlingBatchService performanceCrawlingBatchService;
    private final PerformanceStatisticsBatchService performanceStatisticsBatchService;


    //자정에 상영종료되는 공연 상태정보 변경
//    @Scheduled(cron = "0 0 0 * * *")
    public void updatePerformanceStatus() {
        //현재 시간기준으로 상태업데이트
        performanceCrawlingBatchService.updatePerformanceStatusBasedOnCurrentDate();
    }

    //추천 공연 업데이트
    //1시간에 한번 실행
//    @Scheduled(cron = "0 0 */1 * * *")
    public void updatePerformance() {
        performanceStatisticsBatchService.updateStatistics();
    }

}
