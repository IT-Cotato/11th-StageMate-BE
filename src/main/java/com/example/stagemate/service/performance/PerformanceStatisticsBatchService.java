package com.example.stagemate.service.performance;

import com.example.stagemate.repository.PerformanceStatisticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PerformanceStatisticsBatchService {
    private final PerformanceStatisticsRepository performanceStatisticsRepository;

    @Transactional
    public void updateStatistics() {
        //기존 통계 삭제
        performanceStatisticsRepository.deleteAllPerformanceStatistics();

        //새로운 통계 업데이트
        performanceStatisticsRepository.updateIncreasedScrapCountInBulk();
    }


}
