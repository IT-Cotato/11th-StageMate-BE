package com.example.stagemate.scheduler;

import com.example.stagemate.domain.performances.Performances;
import com.example.stagemate.dto.data.CrawledPerformanceInfo;
import com.example.stagemate.service.PerformanceService;
import com.example.stagemate.service.scheduling.InterParkCrawlerService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import java.util.List;

@RequiredArgsConstructor
@Component
public class CrawlerScheduler {

    private final InterParkCrawlerService interParkCrawlerService;
    private final PerformanceService performanceService;


    //크롤러 2시간마다 실행
    @Scheduled(cron = "0 0 */2 * * *")
    public void runCrawlerJob() {
        List<CrawledPerformanceInfo> cralledPerformances = interParkCrawlerService.crawlPerformances();

        //크롤링 데이터와 비교하면서 변경사항이 있면 업데이트, 기존에 없던 공연이면 삽입
        performanceService.updateBatch(cralledPerformances.stream()
                .map(Performances::from)
                .toList());

    }

    //자정에 상영종료되는 공연 상태정보 변경
    @Scheduled(cron = "0 0 0 * * *")
    public void updatePerformanceStatus() {
        //현재 시간기준으로 상태업데이트
        performanceService.updatePerformanceStatusBasedOnCurrentDate();
    }
}
