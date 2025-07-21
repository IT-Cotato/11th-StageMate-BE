package com.example.stagemate.controller;

import com.example.stagemate.domain.archive.Archive;
import com.example.stagemate.domain.user.entity.UserJpaEntity;
import com.example.stagemate.dto.request.ArchiveCreateRequest;
import com.example.stagemate.dto.request.ArchiveUpdateRequest;
import com.example.stagemate.dto.response.ArchiveDetailResponse;
import com.example.stagemate.dto.response.MagazinePagedResponse;
import com.example.stagemate.dto.response.community.CommunityPostPagedResponse;
import com.example.stagemate.global.dto.DataResponse;
import com.example.stagemate.global.dto.ErrorResponse;
import com.example.stagemate.global.reslover.CurrentUser;
import com.example.stagemate.service.archive.ArchiveService;
import com.example.stagemate.service.community.CommunityService;
import com.example.stagemate.service.magazine.MagazineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    private final MagazineService magazineService;
    private final CommunityService communityService;

    @Operation(summary = "아카이브 상세 정보", description = "아카이브 상세 정보를 가져옴")
    @ApiResponse(responseCode = "200", description = "아카이브 상세 정보를 가져옴")
    @GetMapping("/api/v1/archive/{archiveId}")
    public ResponseEntity<DataResponse<ArchiveDetailResponse>> getArchive(
            @PathVariable("archiveId") Long archiveId) {

        ArchiveDetailResponse archiveDetailResponse = archiveService.getArchive(archiveId);

        return ResponseEntity.ok(DataResponse.from(archiveDetailResponse));
    }

    //월별 공연 아카이빙
    @Operation(summary = "월별 공연 아카이브", description = "월별 공연 아카이브 목록을 가져옴")
    @ApiResponse(responseCode = "200", description = "월별 공연 아카이브 목록을 가져옴")
    @GetMapping("/api/v1/archives")
    public ResponseEntity<DataResponse<List<ArchiveDetailResponse>>> getArchives(
            @CurrentUser UserJpaEntity user,
            @RequestParam("year") Integer year,
            @RequestParam("month") Integer month) {

        List<ArchiveDetailResponse> archiveDetailResponses = archiveService.getArchives(user, year, month);
        return ResponseEntity.ok(DataResponse.from(archiveDetailResponses));
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

    // 내가 스크랩한 매거진
    // 매거진 목록 보기(기본값 6개씩 페이징), 페이지 1부터 시작
    @Operation(summary = "내가 스크랩한 매거진 목록 조회 (페이징)", description = "매거진을 페이지 단위로 조회합니다. 페이지는 1부터 시작합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "매거진 목록 페이지 단위 조회 성공"),
            @ApiResponse(responseCode = "404", description = "매거진을 찾을 수 없음(MAGAZINE-002)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "유저를 찾을 수 없음 (COMMON-008)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/api/v1/archive/magazines")
    public ResponseEntity<DataResponse<MagazinePagedResponse>> getMagazines(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "6") int size,
            @Parameter(hidden = true) @CurrentUser UserJpaEntity user
    ) {
        MagazinePagedResponse magazines = magazineService.getMyMagazineScrapList(page, size, user);
        return ResponseEntity.ok(DataResponse.from(magazines));
    }

    // 내가 스크랩한 커뮤니티
    @Operation(summary = "내가 스크랩한 커뮤니티 게시글 목록 조회 (페이징)", description = "커뮤니티 게시글을 페이지 단위로 조회합니다. 페이지는 1부터 시작합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "커뮤니티 게시글 목록 페이지 단위 조회 성공"),
            @ApiResponse(responseCode = "404", description = "커뮤니티 게시글을 찾을 수 없음(COMMUNITY-002)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "유저를 찾을 수 없음 (COMMON-008)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/api/v1/archive/communities")
    public ResponseEntity<DataResponse<CommunityPostPagedResponse>> getMyCommunityScrapList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "6") int size,
            @Parameter(hidden = true) @CurrentUser UserJpaEntity user
    ) {
        CommunityPostPagedResponse myCommunityScrapList = communityService.getMyCommunityScrapList(user, page, size);
        return ResponseEntity.ok(DataResponse.from(myCommunityScrapList));
    }
}
