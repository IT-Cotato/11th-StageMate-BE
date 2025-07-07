package com.example.stagemate.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ScheduleReportCreateRequest {
    private final String content;

    private String url;

    private Long performanceId;
}
