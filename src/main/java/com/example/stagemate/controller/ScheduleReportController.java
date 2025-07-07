package com.example.stagemate.controller;

import com.example.stagemate.domain.scheduleReport.ScheduleReport;
import com.example.stagemate.domain.scheduleReport.ScheduleReportStatus;
import com.example.stagemate.dto.request.ScheduleReportCreateRequest;
import com.example.stagemate.global.dto.DataResponse;
import com.example.stagemate.service.scheduleReport.ScheduleReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ScheduleReportController {
    private final ScheduleReportService scheduleReportService;

    @Tag(name = "ScheduleReport", description = "ScheduleReport API")
    @Operation(summary = "스케줄 리포트 상세 정보", description = "스케줄 리포트 상세 정보를 가져옴")
    @ApiResponse(responseCode = "200", description = "스케줄 리포트 상세 정보를 가져옴")
    @GetMapping("/api/v1/scheduleReport/{scheduleReportId}")
    public DataResponse<ScheduleReport> getScheduleReport(@PathVariable Long scheduleReportId) {
        return DataResponse.from(scheduleReportService.getScheduleReport(scheduleReportId));
    }

    @Tag(name = "ScheduleReport", description = "ScheduleReport API")
    @Operation(summary = "스케줄 생성", description = "스케줄 생성")
    @ApiResponse(responseCode = "200", description = "스케줄 생성")
    @PostMapping("/api/v1/scheduleReport")
    public DataResponse<Long> createScheduleReport(
            @RequestBody ScheduleReportCreateRequest scheduleReportCreateRequest) {
        return DataResponse.from(scheduleReportService.createScheduleReport(scheduleReportCreateRequest));
    }

    @Tag(name = "ScheduleReport", description = "ScheduleReport API")
    @Operation(summary = "스케줄 상태별 조회", description = "스케줄 상태별 조회")
    @ApiResponse(responseCode = "200", description = "스케줄 상태별 조회")
    @GetMapping("/api/v1/scheduleReport")
    public DataResponse<List<ScheduleReport>> getScheduleReports(@RequestParam ScheduleReportStatus scheduleReportStatus) {
        return DataResponse.from(scheduleReportService.getScheduleReports(scheduleReportStatus));
    }

    @Tag(name = "ScheduleReport", description = "ScheduleReport API")
    @Operation(summary = "스케줄 상태 변경", description = "스케줄 상태 변경")
    @ApiResponse(responseCode = "200", description = "스케줄 상태 변경")
    @PutMapping("/api/v1/scheduleReport/{scheduleReportId}")
    public DataResponse<ScheduleReport> changeScheduleReportStatus(
            @PathVariable Long scheduleReportId, @RequestParam ScheduleReportStatus scheduleReportStatus) {

        return DataResponse.from(
                scheduleReportService.changeScheduleReportStatus(scheduleReportId, scheduleReportStatus));
    }

}
