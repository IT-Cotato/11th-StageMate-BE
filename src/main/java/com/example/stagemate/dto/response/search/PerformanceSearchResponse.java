package com.example.stagemate.dto.response.search;

import com.example.stagemate.domain.performance.Performance;
import com.example.stagemate.domain.performance.PerformanceType;

public record PerformanceSearchResponse(String performanceName, String url, String startDate, String endDate,
                                        String theaterName, String region, String imageUrl, PerformanceType performanceType, Long chatRoomId) {
    public static PerformanceSearchResponse from(Performance performance, Long chatRoomId) {
        return new PerformanceSearchResponse(
                performance.getPerformanceName(),
                performance.getUrl(),
                performance.getStartDate().toString(),
                performance.getEndDate().toString(),
                performance.getTheater().getName(),
                performance.getTheater().getRegion(),
                performance.getImageUrl(),
                performance.getPerformanceType(),
                chatRoomId // null 가능
        );
    }
}

