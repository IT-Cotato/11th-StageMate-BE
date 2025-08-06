package com.example.stagemate.dto.response.performance;

import com.example.stagemate.domain.performance.Performance;
import com.example.stagemate.domain.performance.PerformanceGenre;
import com.example.stagemate.domain.performance.PerformanceStatistics;
import com.example.stagemate.domain.performance.PerformanceType;

public record RecommendedPerformanceResponse(
        String performanceName, String url, String startDate, String endDate,
        String theaterName, String region, String imageUrl, PerformanceType performanceType,
        Long increasedScrapCount,
        PerformanceGenre performanceGenre
) {

    public static RecommendedPerformanceResponse from(PerformanceStatistics performanceStatistics) {
        Performance performance = performanceStatistics.getPerformance();

        return new RecommendedPerformanceResponse(
                performance.getPerformanceName(),
                performance.getUrl(),
                performance.getStartDate().toString(),
                performance.getEndDate().toString(),
                performance.getTheater().getName(),
                performance.getTheater().getRegion(),
                performance.getImageUrl(),
                performance.getPerformanceType(),
                performanceStatistics.getIncreasedScrapCount(),
                performance.getPerformanceGenre()
        );
    }

}
