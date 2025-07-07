package com.example.stagemate.scheduler;

import com.example.stagemate.domain.performance.Performance;
import com.example.stagemate.dto.data.CrawledPerformanceInfo;
import com.example.stagemate.service.crawling.InterParkCrawlingService;
import com.example.stagemate.service.performance.PerformanceBatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class CrawlerScheduler {

    private final InterParkCrawlingService interParkCrawlingService;
    private final PerformanceBatchService performanceBatchService;


    @Scheduled(cron = "0 0 */2 * * *")
    public void crawlInterPark() {
        List<CrawledPerformanceInfo> cralledPerformances = null;
        //최대 시도 횟수
        int retryCount = 0;
        boolean success = false;

        while (!success) {
            try {
                cralledPerformances = interParkCrawlingService.crawlPerformances();
                success = true; // 성공적으로 크롤링이 완료되면 루프 종료
            } catch (Exception e) {
                retryCount++;
                log.error("크롤링 중 오류 발생, 재시도 횟수: {}", retryCount, e);
                if (retryCount >= 3) {
                    log.error("크롤링 실패, 최대 재시도 횟수 초과");
                    break; // 최대 재시도 횟수를 초과하면 종료
                }

                // 재시도 전에 대기 시간을 추가할 수 있음
                try {
                    Thread.sleep(2000); // 2초 대기 후 재시도
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt(); // 인터럽트 처리
                }
            }
        }

        if (cralledPerformances != null) {
            // 크롤링 데이터와 비교하면서 변경사항이 있으면 업데이트, 기존에 없던 공연이면 삽입
            performanceBatchService.updateBatch(cralledPerformances.stream()
                    .map(Performance::from)
                    .toList());
        }
    }

    //자정에 상영종료되는 공연 상태정보 변경
    @Scheduled(cron = "0 0 0 * * *")
    public void updatePerformanceStatus() {
        //현재 시간기준으로 상태업데이트
        performanceBatchService.updatePerformanceStatusBasedOnCurrentDate();
    }
}
