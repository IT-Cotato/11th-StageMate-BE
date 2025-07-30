package com.example.stagemate.service.magazine;

import com.example.stagemate.domain.magazine.Magazine;
import com.example.stagemate.domain.magazine.MagazineStatistics;
import com.example.stagemate.repository.magazine.MagazineRepository;
import com.example.stagemate.repository.magazine.MagazineStatisticsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class MagazineStatisticsScheduler {
    private final MagazineRepository magazineRepository;
    private final MagazineStatisticsRepository magazineStatisticsRepository;

    // 애플리케이션 시작 시 매거진 통계 초기화
//    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initStatistics() {
        updateMagazineStatistics();
        log.info("애플리케이션 시작 시 매거진 통계 초기화 완료");
    }


    // 매거진 통계 업데이트 스케줄러
    // 30분 단위 실행
    @Transactional
//    @Scheduled(cron = "0 0/30 * * * *")
    public void updateMagazineStatistics() {
        Pageable pageable = PageRequest.of(0, 4);
        List<Magazine> magazines = magazineRepository.findTop4ByLikesAndScrapsSum(pageable);

        // 기존 통계 삭제
        magazineStatisticsRepository.deleteAll();

        // 매거진 통계 테이블에 저장
        magazines.stream()
                .map(MagazineStatistics::of)
                .forEach(magazineStatisticsRepository::save);
        log.info("매거진 통계 업데이트 완료. 최신 4개 매거진 통계 저장됨.");
    }
}
