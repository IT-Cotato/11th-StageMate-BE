package com.example.stagemate.dto.response;

import com.example.stagemate.domain.performance.Performance;
import com.example.stagemate.domain.performance.PerformanceStatistics;

public record RecommendedPerformanceResponse(
        Performance performance,
        Long increasedScrapCount
) {
    public static RecommendedPerformanceResponse from(Performance performance, PerformanceStatistics performanceStatistics) {
        return new RecommendedPerformanceResponse(performance, performanceStatistics.getIncreasedScrapCount());
    }
}
