package com.example.stagemate.dto.response.performance;

import com.example.stagemate.domain.performanceSchedule.PerformanceSchedule;
import com.example.stagemate.domain.performanceSchedule.PerformanceScheduleReportCategory;
import com.example.stagemate.domain.performanceSchedule.PerformanceScheduleReportCategoryType;
import com.example.stagemate.domain.performanceSchedule.PerformanceScheduleReportStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record PerformanceScheduleDetailResponse(
        Long performanceScheduleId,
        PerformanceDetailResponse performanceDetailResponse,
        String title,
        String content,
        String url,
        LocalDate scheduleDate,
        LocalDateTime scheduleStartTime,
        LocalDateTime scheduleEndTime,
        PerformanceScheduleReportStatus performanceScheduleReportStatus,
        boolean isScraped,
        List<PerformanceScheduleReportCategoryType> performanceScheduleReportCategoryTypes
) {
    public static PerformanceScheduleDetailResponse from(PerformanceSchedule performanceSchedule, boolean isScraped) {
        return new PerformanceScheduleDetailResponse(
                performanceSchedule.getId(),
                //공연 정보
                PerformanceDetailResponse.from(performanceSchedule.getPerformance()),
                performanceSchedule.getTitle(),
                performanceSchedule.getContent(),
                performanceSchedule.getUrl(),
                performanceSchedule.getScheduleDate(), performanceSchedule.getScheduleStartTime(),
                performanceSchedule.getScheduleEndTime(),
                performanceSchedule.getPerformanceScheduleReportStatus(),
                isScraped,
                performanceSchedule.getPerformanceScheduleReportCategories().stream().map(PerformanceScheduleReportCategory::getPerformanceScheduleReportCategoryType).toList());
    }
}
