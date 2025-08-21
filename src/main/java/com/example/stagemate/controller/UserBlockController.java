package com.example.stagemate.controller;

import com.example.stagemate.domain.user.entity.UserJpaEntity;
import com.example.stagemate.dto.request.community.UserBlockRequest;
import com.example.stagemate.dto.response.community.UserBlockListResponse;
import com.example.stagemate.dto.response.UserBlockStatusResponse;
import com.example.stagemate.global.dto.DataResponse;
import com.example.stagemate.global.dto.ErrorResponse;
import com.example.stagemate.global.dto.PagedResponse;
import com.example.stagemate.global.reslover.CurrentUser;
import com.example.stagemate.service.community.UserBlockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/blocks")
@Slf4j
public class UserBlockController {

    private final UserBlockService userBlockService;

    @Operation(summary = "사용자 차단", description = "게시글 또는 댓글을 기반으로 해당 작성자를 차단합니다." +
            "targetId는 신고할 게시글 또는 댓글의 ID, targetType은 POST 또는 COMMENT 중 하나여야 합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "차단 성공"),
            @ApiResponse(responseCode = "400", description = """
            - 잘못된 대상 타입 (COMMUNITY-011)
            - 자기 자신을 차단 (COMMUNITY-012)
            - 이미 차단한 사용자 (COMMUNITY-013)
        """),
            @ApiResponse(responseCode = "401", description = "인증 필요 (COMMON-009)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = """
            - 게시글이 존재하지 않음 (COMMUNITY-002)
            - 댓글이 존재하지 않음 (COMMUNITY-006)
            - 사용자를 찾을 수 없음 (COMMON-008)
        """)
    })
    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<DataResponse<Void>> blockUser(
            @RequestBody UserBlockRequest request,
            @Parameter(hidden = true) @CurrentUser UserJpaEntity user
    ) {
//        userBlockService.blockUser(user, request.getTargetId(), request.getTargetType());

        userBlockService.blockUser(user, request.getBlockedUserId());

        return ResponseEntity.ok(DataResponse.ok());
    }

    @Operation(summary = "사용자 차단 해제", description = "차단한 사용자를 차단 목록에서 해제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "차단 해제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요 (COMMON-009)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "차단당한 사용자가 존재하지 않음/ 사용자를 찾을 수 없음(COMMON-008)")
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{blockedUserId}")
    public ResponseEntity<DataResponse<Void>> unblockUser(
            @PathVariable Long blockedUserId,
            @Parameter(hidden = true) @CurrentUser UserJpaEntity user
    ) {
        userBlockService.unblockUser(user, blockedUserId);
        return ResponseEntity.ok(DataResponse.ok());
    }

    @Operation(summary = "차단한 사용자 목록 조회", description = "내가 차단한 사용자 목록을 페이지 단위로 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "차단 목록 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요 (COMMON-009)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public ResponseEntity<DataResponse<PagedResponse<UserBlockListResponse>>> getBlockedUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @Parameter(hidden = true) @CurrentUser UserJpaEntity user
    ) {
        PagedResponse<UserBlockListResponse> blockedUsers = userBlockService.getBlockedUsers(user, page, size);
        return ResponseEntity.ok(DataResponse.from(blockedUsers));
    }


    @Operation(summary = "사용자 차단 여부 확인", description = "사용자 차단 여부를 확인합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "차단 여부 확인 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요 (COMMON-009)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/check")
    public ResponseEntity<DataResponse<List<UserBlockStatusResponse>>> checkBlockedUser(
            @RequestParam("userIds") List<Long> userIds,
            @Parameter(hidden = true) @CurrentUser UserJpaEntity user
    ) {
        log.info("userIds: {}", userIds);
        List<UserBlockStatusResponse> response = userBlockService.checkBlockedUser(user.getId(), userIds);
        return ResponseEntity.ok(DataResponse.from(response));
    }
}

