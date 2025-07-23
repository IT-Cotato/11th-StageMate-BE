package com.example.stagemate.controller;

import com.example.stagemate.domain.user.entity.UserJpaEntity;
import com.example.stagemate.dto.response.MagazinePagedResponse;
import com.example.stagemate.dto.response.community.CommunityPostPagedResponse;
import com.example.stagemate.global.dto.DataResponse;
import com.example.stagemate.global.dto.ErrorResponse;
import com.example.stagemate.global.reslover.CurrentUser;
import com.example.stagemate.service.community.CommunityService;
import com.example.stagemate.service.magazine.MagazineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "UserController", description = "User 관련 API")
public class UserController {

    private final CommunityService communityService;
    private final MagazineService magazineService;

    // 내가 스크랩한 매거진
    // 매거진 목록 보기(기본값 6개씩 페이징), 페이지 1부터 시작
    @Operation(summary = "내가 스크랩한 매거진 목록 조회 (페이징)", description = "매거진을 페이지 단위로 조회합니다. 페이지는 1부터 시작합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "매거진 목록 페이지 단위 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요 (COMMON-009)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/magazines")
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
            @ApiResponse(responseCode = "401", description = "인증 필요 (COMMON-009)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/communities")
    public ResponseEntity<DataResponse<CommunityPostPagedResponse>> getMyCommunityScrapList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "6") int size,
            @Parameter(hidden = true) @CurrentUser UserJpaEntity user
    ) {
        CommunityPostPagedResponse myCommunityScrapList = communityService.getMyCommunityScrapList(user, page, size);
        return ResponseEntity.ok(DataResponse.from(myCommunityScrapList));
    }
}
