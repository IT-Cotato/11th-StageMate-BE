package com.example.stagemate.dto.response.performance;

import com.example.stagemate.domain.performance.PerformanceType;
import com.example.stagemate.global.util.DateFormat;


public record PerformanceDetailResponse(String performanceName, String url, String startDate, String endDate,
                                        String theaterName, String region, String imageUrl, PerformanceType performanceType) {
    public static PerformanceDetailResponse from(com.example.stagemate.domain.performance.Performance performance) {
        return new PerformanceDetailResponse(
                performance.getPerformanceName(),
                performance.getUrl(),
                DateFormat.formateOnlyDate(performance.getStartDate()),
                DateFormat.formateOnlyDate(performance.getEndDate()),
                performance.getTheater().getName(),
                performance.getTheater().getRegion(),
                performance.getImageUrl(),
                performance.getPerformanceType()
        );
    }
}
