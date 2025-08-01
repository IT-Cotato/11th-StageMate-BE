package com.example.stagemate.controller;

import com.example.stagemate.domain.user.entity.UserJpaEntity;
import com.example.stagemate.dto.request.chat.ReportChatRequest;
import com.example.stagemate.dto.request.community.ReportCommunityRequest;
import com.example.stagemate.global.dto.DataResponse;
import com.example.stagemate.global.dto.ErrorResponse;
import com.example.stagemate.global.reslover.CurrentUser;
import com.example.stagemate.service.report.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Tag(name = "ReportController", description = "신고 관련 API")
public class ReportController {

    private final ReportService reportService;

    // 커뮤니티 게시글/댓글신고, 채팅신고
    @Operation(
            summary = "커뮤니티 게시글/댓글 신고",
            description = """
        게시글 또는 댓글 신고합니다.<br>
        targetId는 신고할 게시글, 댓글 targetType은 POST, COMMENT 중 하나여야 하며,<br>
        reason은 BAIT, LEAK_IMPERSONATION_FRAUD, COMMERCIAL_AD, ILLEGAL_CONTENT, OBSCENE, ABUSE 중 하나입니다.<br>
        """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "신고 성공"),
            @ApiResponse(responseCode = "400", description = """
                        - 잘못된 신고 사유 (COMMUNITY-009)
                        - 잘못된 대상 타입 (COMMUNITY-011)
                        - 이미 신고한 대상 (COMMUNITY-010)
                    """, content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 필요 (COMMON-009)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = """
                        - 게시글을 찾을 수 없음 (COMMUNITY-002)
                        - 댓글을 찾을 수 없음 (COMMUNITY-006)
                        - 사용자를 찾을 수 없음 (COMMON-008)
                    """, content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/community")
    public ResponseEntity<DataResponse<Void>> reportCommunityContent(
            @RequestBody ReportCommunityRequest request,
            @Parameter(hidden = true) @CurrentUser UserJpaEntity user
    ) {
        reportService.reportCommunityPost(user, request.getTargetId(), request.getTargetType(), request.getReason());
        return ResponseEntity.ok(DataResponse.ok());
    }



    @Operation(
            summary = "채팅 신고",
            description = """
        채팅을 신고합니다.<br>
        chatId는 신고할 채팅 중 하나여야 하며,<br>
        reason은 BAIT, LEAK_IMPERSONATION_FRAUD, COMMERCIAL_AD, ILLEGAL_CONTENT, OBSCENE, ABUSE 중 하나입니다.<br>
        """
    )
    @ApiResponses(
            @ApiResponse(responseCode = "200", description = "신고 성공")
    )
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/chat")
    public ResponseEntity<DataResponse<Void>> reportChat(
            @RequestBody ReportChatRequest request,
            @Parameter(hidden = true) @CurrentUser UserJpaEntity user
    ) {

        reportService.reportChat(user, request.chatId(), request.reason());
        return ResponseEntity.ok(DataResponse.ok());
    }


}
