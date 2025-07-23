package com.example.stagemate.dto.response;

import com.example.stagemate.domain.performance.Performance;
import com.example.stagemate.domain.performance.PerformanceStatistics;

public record RecommendedPerformanceResponse(
        PerformanceDetailResponse performanceDetailResponse,
        Long increasedScrapCount
) {
    public static RecommendedPerformanceResponse from(Performance performance, PerformanceStatistics performanceStatistics) {
        return new RecommendedPerformanceResponse(
                PerformanceDetailResponse.from(performance), performanceStatistics.getIncreasedScrapCount());
    }
}
