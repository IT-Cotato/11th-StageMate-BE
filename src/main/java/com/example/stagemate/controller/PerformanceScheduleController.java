package com.example.stagemate.controller;

import com.example.stagemate.domain.performanceSchedule.PerformanceSchedule;
import com.example.stagemate.domain.performanceSchedule.PerformanceScheduleReportStatus;
import com.example.stagemate.domain.user.entity.UserJpaEntity;
import com.example.stagemate.dto.request.PerformanceScheduleCreateRequest;
import com.example.stagemate.dto.response.PerformanceScheduleDetailResponse;
import com.example.stagemate.global.dto.DataResponse;
import com.example.stagemate.global.reslover.CurrentUser;
import com.example.stagemate.service.performance.PerformanceScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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
            @CurrentUser UserJpaEntity user,
            @PathVariable("performanceScheduleId") Long performanceScheduleId) {

        PerformanceSchedule performanceSchedule = performanceScheduleService.getPerformanceSchedule(performanceScheduleId);
        boolean isScraped = performanceScheduleService.findIsScraped(performanceScheduleId, user);

        return ResponseEntity.ok(DataResponse.from(PerformanceScheduleDetailResponse.from(performanceSchedule, isScraped)));
    }

    @Operation(summary = "공식스케줄 제보 생성", description = "공식스케줄 제보 생성")
    @ApiResponse(responseCode = "200", description = "공식스케줄 제보 생성")
    @PostMapping("/api/v1/performanceSchedule")
    public ResponseEntity<DataResponse<Long>> createPerformanceSchedule(
            @CurrentUser UserJpaEntity user,
            @Valid @RequestBody PerformanceScheduleCreateRequest performanceScheduleCreateRequest) {

        Long performanceScheduleId = performanceScheduleService.createPerformanceSchedule(user, performanceScheduleCreateRequest);
        return ResponseEntity.ok(DataResponse.from(performanceScheduleId));
    }

    @Operation(summary = "공식스케줄 상태 변경", description = "공식스케줄 상태 변경, 관리자가 PENDING 상태인 공식스케줄 제보를 APPROVED, REJECTED 상태로 바꾸는 용도")
    @ApiResponse(responseCode = "200", description = "공식스케줄리포트 상태 변경")
    @PatchMapping("/api/v1/performanceSchedule/{performanceScheduleId}")
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
            @CurrentUser UserJpaEntity user,
            @RequestParam("performanceScheduleReportStatus") List<PerformanceScheduleReportStatus> performanceScheduleReportStatus) {

        List<PerformanceSchedule> performanceSchedule =
                performanceScheduleService.getPerformanceSchedules(performanceScheduleReportStatus);

        List<Boolean> isScraped =
                performanceSchedule
                        .stream()
                        .map(
                                performanceSchedule1 ->
                                        performanceScheduleService.findIsScraped(performanceSchedule1.getId(), user))
                        .toList();

        return ResponseEntity.ok(
                DataResponse.from(
                        performanceSchedule.stream()
                                .map(
                                        performanceSchedule1 ->
                                                PerformanceScheduleDetailResponse.from(
                                                        performanceSchedule1, isScraped.get(performanceSchedule.indexOf(performanceSchedule1))))
                                .collect(Collectors.toList())));
    }

    //공연 스케줄 목록
    @Operation(summary = "공연 스케줄 목록", description = "공연 스케줄 목록을 가져옴")
    @ApiResponse(responseCode = "200", description = "공연 스케줄 목록을 가져옴")
    @GetMapping(value = "/api/v1/performanceSchedule", params = {"year", "month"})
    public ResponseEntity<DataResponse<List<PerformanceScheduleDetailResponse>>> getPerformanceSchedules(
            @CurrentUser UserJpaEntity user,
            @RequestParam("year") Integer year,
            @RequestParam("month") Integer month,
            @RequestParam(name = "day", required = false) Integer day) {

        List<PerformanceSchedule> performanceSchedules = day == null ?
                performanceScheduleService.getPerformanceSchedule(year, month) :
                performanceScheduleService.getPerformanceSchedule(year, month, day);

        List<Boolean> isScraped = performanceSchedules.stream()
                .map(performanceSchedule -> performanceScheduleService.findIsScraped(performanceSchedule.getId(), user))
                .toList();


        return ResponseEntity.ok(DataResponse.from(performanceSchedules
                .stream()
                .map(performanceSchedule -> PerformanceScheduleDetailResponse.from(performanceSchedule, isScraped.get(performanceSchedules.indexOf(performanceSchedule))))
                .collect(Collectors.toList())));
    }


    //공식 스케줄 스크랩하기
    @Operation(summary = "공식 스케줄 스크랩 저장 또는 삭제", description = "공식 스케줄 스크랩 저장 또는 삭제")
    @ApiResponse(responseCode = "200", description = "공식 스케줄 스크랩 저장 또는 삭제")
    @PostMapping("/api/v1/performanceSchedule/{performanceScheduleId}")
    public ResponseEntity<DataResponse<?>> insertOrDeletePerformanceScheduleScrap(
            @CurrentUser UserJpaEntity user, @PathVariable("performanceScheduleId") Long performanceScheduleId) {

        performanceScheduleService.insertOrDeletePerformanceScheduleScrap(performanceScheduleId, user);
        return ResponseEntity.ok().build();
    }


}
