package com.example.stagemate.controller;

import com.example.stagemate.domain.performanceSchedule.PerformanceScheduleReportStatus;
import com.example.stagemate.domain.user.entity.UserJpaEntity;
import com.example.stagemate.dto.request.PerformanceScheduleCreateRequest;
import com.example.stagemate.dto.response.PerformanceScheduleDetailResponse;
import com.example.stagemate.global.dto.DataResponse;
import com.example.stagemate.global.reslover.CurrentUser;
import com.example.stagemate.service.performance.PerformanceScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "PerformanceSchedule", description = "PerformanceSchedule API")
public class PerformanceScheduleController {
    private final PerformanceScheduleService performanceScheduleService;

    @Operation(summary = "공식스케줄 상세조회",
            description = "공식스케줄 상세조회 (유저에게는 APPROVED 상태인 공식일정만 보여줍니다)")
    @ApiResponse(responseCode = "200", description = "공식일정리포트 상세 정보를 가져옴")
    @GetMapping("/api/v1/performanceSchedule/{performanceScheduleId}")
    public ResponseEntity<DataResponse<PerformanceScheduleDetailResponse>> getPerformanceSchedule(
            @Parameter(hidden = true) @CurrentUser UserJpaEntity user,
            @PathVariable("performanceScheduleId") Long performanceScheduleId) {

        PerformanceScheduleDetailResponse performanceScheduleDetailResponse =
                performanceScheduleService.getPerformanceSchedule(user, performanceScheduleId);

        return ResponseEntity.ok(DataResponse.from(performanceScheduleDetailResponse));
    }

    @Operation(summary = "공식스케줄 제보 생성", description = "공식스케줄 제보 생성")
    @ApiResponse(responseCode = "200", description = "공식스케줄 제보 생성")
    @PostMapping("/api/v1/performanceSchedule")
    public ResponseEntity<DataResponse<Long>> createPerformanceSchedule(
            @Parameter(hidden = true) @CurrentUser UserJpaEntity user,
            @Valid @RequestBody PerformanceScheduleCreateRequest performanceScheduleCreateRequest) {

        Long performanceScheduleId = performanceScheduleService.createPerformanceSchedule(user, performanceScheduleCreateRequest);
        return ResponseEntity.ok(DataResponse.from(performanceScheduleId));
    }

    @Operation(summary = "공식스케줄 상태 변경", description = "공식스케줄 상태 변경, 관리자가 PENDING 상태인 공식스케줄 제보를 APPROVED, REJECTED 상태로 바꾸는 용도")
    @ApiResponse(responseCode = "200", description = "공식스케줄리포트 상태 변경")
    @PutMapping("/api/v1/performanceSchedule/{performanceScheduleId}")
    public ResponseEntity<DataResponse<?>> changePerformanceScheduleStatus(
            @PathVariable("performanceScheduleId") Long performanceScheduleId,
            @RequestParam("performanceScheduleReportStatus") PerformanceScheduleReportStatus performanceScheduleReportStatus) {

        performanceScheduleService.changePerformanceScheduleStatus(performanceScheduleId, performanceScheduleReportStatus);
        return ResponseEntity.ok().build();
    }


    @Operation(summary = "공식스케줄 상태별 조회",
            description = "공식스케줄리포트 상태별 조회, 주로 관리자가 PENDING 상태인 공식스케줄리포트를 확인하는 용도")
    @ApiResponse(responseCode = "200", description = "공식스케줄리포트 상태별 조회")
    @GetMapping(value = "/api/v1/performanceSchedule", params = "performanceScheduleReportStatus")
    public ResponseEntity<DataResponse<List<PerformanceScheduleDetailResponse>>> getPerformanceSchedules(
            @RequestParam("performanceScheduleReportStatus") List<PerformanceScheduleReportStatus> performanceScheduleReportStatus) {

        List<PerformanceScheduleDetailResponse> performanceScheduleDetailResponses = performanceScheduleService.getPerformanceSchedules(performanceScheduleReportStatus);
        return ResponseEntity.ok(DataResponse.from(performanceScheduleDetailResponses));
    }

    //공연 스케줄 목록
    @Operation(summary = "공연 스케줄 목록", description = "공연 스케줄 목록을 가져옴")
    @ApiResponse(responseCode = "200", description = "공연 스케줄 목록을 가져옴")
    @GetMapping(value = "/api/v1/performanceSchedule", params = {"year", "month"})
    public ResponseEntity<DataResponse<List<PerformanceScheduleDetailResponse>>> getPerformanceSchedules(
            @Parameter(hidden = true) @CurrentUser UserJpaEntity user,
            @RequestParam("year") Integer year,
            @RequestParam("month") Integer month,
            @RequestParam(name = "day", required = false) Integer day) {

        //유저에게 승인된 공연 스케줄만 return
        List<PerformanceScheduleDetailResponse> performanceScheduleDetailResponses = day == null ?
                performanceScheduleService.getPerformanceSchedule(user, year, month, PerformanceScheduleReportStatus.APPROVED) :
                performanceScheduleService.getPerformanceSchedule(user, year, month, day, PerformanceScheduleReportStatus.APPROVED);

        return ResponseEntity.ok(DataResponse.from(performanceScheduleDetailResponses));
    }


    //공식 스케줄 스크랩하기
    @Operation(summary = "공식 스케줄 스크랩 저장 또는 삭제", description = "공식 스케줄 스크랩 저장 또는 삭제")
    @ApiResponse(responseCode = "200", description = "공식 스케줄 스크랩 저장 또는 삭제")
    @PostMapping("/api/v1/performanceSchedule/{performanceScheduleId}/scrap")
    public ResponseEntity<DataResponse<?>> insertOrDeletePerformanceScheduleScrap(
            @Parameter(hidden = true) @CurrentUser UserJpaEntity user,
            @PathVariable("performanceScheduleId") Long performanceScheduleId) {

        performanceScheduleService.insertOrDeletePerformanceScheduleScrap(performanceScheduleId, user);
        return ResponseEntity.ok().build();
    }


}
