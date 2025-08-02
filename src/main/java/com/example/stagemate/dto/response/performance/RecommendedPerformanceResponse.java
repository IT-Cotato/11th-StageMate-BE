package com.example.stagemate.dto.response.performance;

import com.example.stagemate.domain.performance.Performance;
import com.example.stagemate.domain.performance.PerformanceGenre;
import com.example.stagemate.domain.performance.PerformanceStatistics;

public record RecommendedPerformanceResponse(
        PerformanceDetailResponse performanceDetailResponse,
        PerformanceGenre performanceGenre,
        Long increasedScrapCount
) {
    public static RecommendedPerformanceResponse from(Performance performance, PerformanceStatistics performanceStatistics) {
        return new RecommendedPerformanceResponse(
                PerformanceDetailResponse.from(performance),
                performance.getPerformanceGenre(),
                performanceStatistics.getIncreasedScrapCount());
    }
}
