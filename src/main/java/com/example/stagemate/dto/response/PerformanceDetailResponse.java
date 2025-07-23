package com.example.stagemate.dto.response;

import com.example.stagemate.domain.performance.PerformanceType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


public record PerformanceDetailResponse(String performanceName, String url, String startDate, String endDate,
                                        String theaterName, String region, String imageUrl, PerformanceType performanceType) {
    public static PerformanceDetailResponse from(com.example.stagemate.domain.performance.Performance performance) {
        return new PerformanceDetailResponse(
                performance.getPerformanceName(),
                performance.getUrl(),
                performance.getStartDate().toString(),
                performance.getEndDate().toString(),
                performance.getTheater().getName(),
                performance.getTheater().getRegion(),
                performance.getImageUrl(),
                performance.getPerformanceType()
        );
    }
}
