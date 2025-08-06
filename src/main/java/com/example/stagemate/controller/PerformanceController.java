package com.example.stagemate.controller;

import com.example.stagemate.domain.performance.PerformanceGenre;
import com.example.stagemate.domain.performance.PerformanceType;
import com.example.stagemate.domain.user.entity.UserJpaEntity;
import com.example.stagemate.dto.response.performance.PerformanceDetailResponse;
import com.example.stagemate.dto.response.performance.RecommendedPerformanceResponse;
import com.example.stagemate.global.dto.DataResponse;
import com.example.stagemate.global.dto.PagedResponse;
import com.example.stagemate.global.reslover.CurrentUser;
import com.example.stagemate.service.performance.PerformanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@RestController
@Tag(name = "Performance", description = "Performance API")
public class PerformanceController {
    private final PerformanceService performanceService;

    @Operation(summary = "공연 상세 정보", description = "공연 상세 정보를 가져옴")
    @ApiResponse(responseCode = "200", description = "공연 상세 정보를 가져옴")
    @GetMapping("/api/v1/performance/{performanceId}")
    public ResponseEntity<DataResponse<PerformanceDetailResponse>> getPerformance(@PathVariable Long performanceId) {
        PerformanceDetailResponse performanceDetailResponse = performanceService.getPerformance(performanceId);

        return ResponseEntity.ok(DataResponse.from(performanceDetailResponse));
    }

    //공연 목록
    @Operation(summary = "공연 목록", description = "공연 목록을 가져옴")
    @ApiResponse(responseCode = "200", description = "공연 목록을 가져옴")
    @GetMapping("/api/v1/performance")
    public ResponseEntity<DataResponse<PagedResponse<PerformanceDetailResponse>>> getPerformances(
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "performanceType", required = false) PerformanceType performanceType,
            @RequestParam(name = "performanceGenre", required = false) PerformanceGenre performanceGenre,
            @RequestParam(name = "region", required = false) List<String> region,
            @RequestParam(name = "date", required = false) LocalDate date
            ) {

        Pageable pageable = PageRequest.of(page - 1, size);

        PagedResponse<PerformanceDetailResponse> performanceDetailResponses =
                performanceService.findFillteredPerformances(performanceType, performanceGenre, region, date, pageable);

        return ResponseEntity.ok(DataResponse.from(performanceDetailResponses));
    }


    //performance 스크랩
    @Operation(summary = "공연 스크랩", description = "공연 스크랩 저장 또는 삭제")
    @ApiResponse(responseCode = "200", description = "공연 스크랩 저장 또는 삭제")
    @PostMapping("/api/v1/performance/{performanceId}")
    public ResponseEntity<DataResponse<?>> insertOrDeletePerformanceScrap(
            @Parameter(hidden = true) @CurrentUser UserJpaEntity user,
            @PathVariable("performanceId") Long performanceId) {

        performanceService.insertOrDeletePerformanceScrap(user, performanceId);

        return ResponseEntity.ok(DataResponse.ok());
    }


    //추천 performance
    //1시간이내 스크랩이 많이 오른 순으로 추천
    @Operation(summary = "추천 공연", description = "추천 공연 목록을 가져옴")
    @ApiResponse(responseCode = "200", description = "추천 공연 목록을 가져옴")
    @GetMapping("/api/v1/performance/recommend")
    public ResponseEntity<DataResponse<PagedResponse<RecommendedPerformanceResponse>>> getRecommendPerformance(
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "page", defaultValue = "1") int page) { // 기본 size = 10


        //size만큼 추천 공연 목록 가져오기
        PagedResponse<RecommendedPerformanceResponse> recommendedPerformanceResponses =
                performanceService.getRecommendPerformances(page, size);

        return ResponseEntity.ok(DataResponse.from(recommendedPerformanceResponses));

    }
}
