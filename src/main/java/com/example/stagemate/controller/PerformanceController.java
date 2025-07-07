package com.example.stagemate.controller;

import com.example.stagemate.domain.performanceSchedule.PerformanceSchedule;
import com.example.stagemate.domain.performance.Performance;
import com.example.stagemate.dto.response.PerformanceScheduleResponse;
import com.example.stagemate.global.dto.DataResponse;
import com.example.stagemate.service.performance.PerformanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
public class PerformanceController {
    private final PerformanceService performanceService;

    @Tag(name = "Performance", description = "Performance API")
    @Operation(summary = "공연 상세 정보", description = "공연 상세 정보를 가져옴")
    @ApiResponse(responseCode = "200", description = "공연 상세 정보를 가져옴")
    @GetMapping("/api/v1/performance/{performanceId}")
    public ResponseEntity<DataResponse<Performance>> getPerformance(@PathVariable Long performanceId) {
        Performance performance = performanceService.getPerformance(performanceId);
        return ResponseEntity.ok(DataResponse.from(performance));
    }


    //공연 스케줄 목록
    @Tag(name = "PerformanceSchedule", description = "PerformanceSchedule API")
    @Operation(summary = "공연 스케줄 목록", description = "공연 스케줄 목록을 가져옴")
    @ApiResponse(responseCode = "200", description = "공연 스케줄 목록을 가져옴")
    @GetMapping("/api/v1/performanceSchedule")
    public ResponseEntity<DataResponse<List<PerformanceScheduleResponse>>> getPerformanceSchedule(
            @RequestParam Integer year, @RequestParam Integer month,
            @RequestParam(required = false) Integer day) {

        List<PerformanceSchedule> performanceSchedules = day == null ?
                performanceService.getPerformanceSchedule(year, month) :
                performanceService.getPerformanceSchedule(year, month, day);

        List<PerformanceScheduleResponse> performanceScheduleResponses = performanceSchedules.stream()
                .map(PerformanceScheduleResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(DataResponse.from(performanceScheduleResponses));
    }

}
