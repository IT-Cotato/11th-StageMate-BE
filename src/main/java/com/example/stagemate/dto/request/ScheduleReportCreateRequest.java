package com.example.stagemate.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class ScheduleReportCreateRequest {
    @NotNull(message = "content cannot be null")
    private String content;

    @NotNull(message = "url cannot be null")
    private String url;

    @NotNull(message = "performanceId cannot be null")
    private Long performanceId;
}
