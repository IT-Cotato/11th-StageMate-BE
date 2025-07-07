package com.example.stagemate.dto.response;

import com.example.stagemate.domain.performance.PerformanceType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class PerformanceDetailResponse {
    private final String performanceName;
    private final String url;
    private final String startDate;
    private final String endDate;
    private final String theaterName;
    private final String imageUrl;
    private final PerformanceType performanceType;

    public static PerformanceDetailResponse from(com.example.stagemate.domain.performance.Performance performance) {
        return new PerformanceDetailResponse(
                performance.getPerformanceName(),
                performance.getUrl(),
                performance.getStartDate().toString(),
                performance.getEndDate().toString(),
                performance.getTheaterName(),
                performance.getImageUrl(),
                performance.getPerformanceType()
        );
    }
}
