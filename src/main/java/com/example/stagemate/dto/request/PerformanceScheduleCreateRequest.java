package com.example.stagemate.dto.request;

import com.example.stagemate.domain.performanceSchedule.PerformanceScheduleReportCategoryType;
import com.example.stagemate.domain.theater.Theater;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class PerformanceScheduleCreateRequest {
    @NotNull(message = "title cannot be null")
    private String title;

    @NotNull(message = "content cannot be null")
    private String content;

    @NotNull(message = "url cannot be null")
    private String url;

    //관련 공연 id
    private Long performanceId;

    //관련 장소 id
    private Long theaterId;

    //일정 날짜
    private LocalDate scheduleDate;

    //일정 시작시간
    private LocalDateTime scheduleDateStartTime;

    //일정 종료시간
    private LocalDateTime scheduleDateEndTime;

    //공연일정 제보 일시
    private LocalDateTime reportDate;

    //공연일정제보 카테고리
    private List<PerformanceScheduleReportCategoryType> performanceScheduleCategoryTypes = new ArrayList<>();

}
