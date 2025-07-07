package com.example.stagemate.dto.response;

import com.example.stagemate.domain.performanceSchedule.PerformanceSchedule;
import com.example.stagemate.domain.performance.PerformanceType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class PerformanceScheduleResponse {
    private final String scheduleDate;
    private final PerformanceType performanceType;
    private final String content;
    private final String performanceName;
    private final PerformanceDetailResponse performanceDetailResponse;

    public static PerformanceScheduleResponse from(PerformanceSchedule performanceSchedule) {
        return new PerformanceScheduleResponse(
                performanceSchedule.getScheduleDate().toString(),
                performanceSchedule.getPerformance().getPerformanceType(),
                performanceSchedule.getContent(),
                performanceSchedule.getPerformance().getPerformanceName(),
                PerformanceDetailResponse.from(performanceSchedule.getPerformance())
        );
    }
}
