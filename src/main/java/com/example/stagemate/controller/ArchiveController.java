package com.example.stagemate.controller;

import com.example.stagemate.domain.archive.Archive;
import com.example.stagemate.domain.user.entity.UserJpaEntity;
import com.example.stagemate.dto.request.ArchiveCreateRequest;
import com.example.stagemate.dto.request.ArchiveUpdateRequest;
import com.example.stagemate.dto.response.ArchiveDetailResponse;
import com.example.stagemate.global.dto.DataResponse;
import com.example.stagemate.global.reslover.CurrentUser;
import com.example.stagemate.service.archive.ArchiveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@Tag(name = "Archive", description = "Archive API")
public class ArchiveController {
    private final ArchiveService archiveService;

    @Operation(summary = "아카이브 상세 정보", description = "아카이브 상세 정보를 가져옴")
    @ApiResponse(responseCode = "200", description = "아카이브 상세 정보를 가져옴")
    @GetMapping("/api/v1/archive/{archiveId}")
    public ResponseEntity<DataResponse<ArchiveDetailResponse>> getArchive(
            @PathVariable("archiveId") Long archiveId) {

        Archive archive = archiveService.getArchive(archiveId);

        return ResponseEntity.ok(DataResponse.from(ArchiveDetailResponse.from(archive)));
    }

    //월별 공연 아카이빙
    @Operation(summary = "월별 공연 아카이브", description = "월별 공연 아카이브 목록을 가져옴")
    @ApiResponse(responseCode = "200", description = "월별 공연 아카이브 목록을 가져옴")
    @GetMapping("/api/v1/archives")
    public ResponseEntity<DataResponse<List<ArchiveDetailResponse>>> getArchives(
            @CurrentUser UserJpaEntity user,
            @RequestParam("year") Integer year,
            @RequestParam("month") Integer month) {

        List<Archive> archives = archiveService.getArchives(user, year, month);
        return ResponseEntity.ok(DataResponse.from(archives
                .stream()
                .map(ArchiveDetailResponse::from)
                .collect(Collectors.toList())));
    }

    @Operation(summary = "아카이브 생성", description = "아카이브 생성")
    @ApiResponse(responseCode = "200", description = "아카이브 생성")
    @PostMapping("/api/v1/archive")
    public ResponseEntity<DataResponse<Long>> createArchive(
            @CurrentUser UserJpaEntity user,
            @Valid @RequestBody ArchiveCreateRequest archiveCreateRequest) {

        Long archiveId = archiveService.createArchive(archiveCreateRequest, user);
        return ResponseEntity.ok(DataResponse.from(archiveId));
    }

    @Operation(summary = "아카이브 삭제", description = "아카이브 삭제")
    @ApiResponse(responseCode = "200", description = "아카이브 삭제")
    @DeleteMapping("/api/v1/archive/{archiveId}")
    public ResponseEntity<DataResponse<Void>> deleteArchive(
            @CurrentUser UserJpaEntity user,
            @PathVariable Long archiveId) {

        archiveService.deleteArchive(user, archiveId);

        return ResponseEntity.ok(DataResponse.ok());
    }

//    archive Update Reqeust 필요
    @Operation(summary = "아카이브 변경", description = "아카이브 변경")
    @ApiResponse(responseCode = "200", description = "아카이브 변경")
    @PatchMapping("/api/v1/archive/{archiveId}")
    public ResponseEntity<DataResponse<?>> updateArchive(
            @CurrentUser UserJpaEntity user,
            @PathVariable Long archiveId,
            @Valid @RequestBody ArchiveUpdateRequest archiveUpdateRequest) {
        archiveService.updateArchive(user,archiveId, archiveUpdateRequest);
        return ResponseEntity.ok(DataResponse.ok());
    }

//    //월별 공연 평점 TOP
//    @Operation(summary = "월별 공연 평점 TOP", description = "월별 공연 평점 TOP 목록을 가져옴")
//    @ApiResponse(responseCode = "200", description = "월별 공연 평점 TOP 목록을 가져옴")
//    @GetMapping("/api/v1/archives")
//    public ResponseEntity<DataResponse<Page<Archive>>> getTopRatingArchives(
//            @RequestParam Integer year,
//            @RequestParam Integer month,
//            @RequestParam Integer size) {
//        Pageable pageable = PageRequest.of(0, size);
//
//
//        Page<Archive> archives = archiveService.getTopRatingArchives(year, month, pageable);
//
//        return ResponseEntity.ok(DataResponse.from(archives));
//    }



}
