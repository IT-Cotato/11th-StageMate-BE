package com.example.stagemate.controller;

import com.example.stagemate.domain.user.entity.UserJpaEntity;
import com.example.stagemate.dto.request.community.CommunityCommentRequest;
import com.example.stagemate.dto.request.community.CommunityCommentUpdateRequest;
import com.example.stagemate.dto.response.community.CommunityCommentResponse;
import com.example.stagemate.global.auth.CustomUserDetails;
import com.example.stagemate.global.dto.DataResponse;
import com.example.stagemate.global.reslover.CurrentUser;
import com.example.stagemate.service.community.CommunityCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/api/v1/communities-comments") // {postId}는 커뮤니티 게시글 ID
@RequiredArgsConstructor
@Tag(name = "CommunityCommentController", description = "Community 댓글 관련 API")
public class CommunityCommentController {
    private final CommunityCommentService communityCommentService;


    @Operation(summary = "댓글 등록", description = "해당 게시글에 댓글 또는 대댓글을 등록합니다. 댓글인 경우 parentId에 null을 넣고, 대댓글은 parentId에 댓글 Id를 넣습니다.")
    @PostMapping("/{postId}")
    public ResponseEntity<DataResponse<Void>> createComment(
            @PathVariable Long postId,
            @RequestBody CommunityCommentRequest request,
            @Parameter(hidden = true) @CurrentUser UserJpaEntity user
    ) {
        communityCommentService.addComment(user, postId, request);
        return ResponseEntity.ok(DataResponse.ok());
    }

    @Operation(summary = "댓글 수정", description = "댓글 내용을 수정합니다. 작성자 본인만 수정할 수 있습니다.")
    @ApiResponse(responseCode = "200", description = "수정 성공")
    @PutMapping("/{commentId}")
    public ResponseEntity<DataResponse<Void>> updateComment(
            @PathVariable Long commentId,
            @RequestBody CommunityCommentUpdateRequest request,
            @Parameter(hidden = true) @CurrentUser UserJpaEntity user
    ) {
        communityCommentService.updateComment(user, commentId, request);
        return ResponseEntity.ok(DataResponse.ok());
    }

    @Operation(summary = "댓글 삭제", description = "soft delete 방식으로 댓글을 삭제합니다. 작성자 본인만 삭제할 수 있습니다.")
    @ApiResponse(responseCode = "200", description = "삭제 성공")
    @DeleteMapping("/{commentId}")
    public ResponseEntity<DataResponse<Void>> deleteComment(
            @PathVariable Long commentId,
            @Parameter(hidden = true) @CurrentUser UserJpaEntity user

    ) {
        communityCommentService.deleteComment(user, commentId);
        return ResponseEntity.ok(DataResponse.ok());
    }


}
