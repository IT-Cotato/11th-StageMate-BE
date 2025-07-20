package com.example.stagemate.controller;

import com.example.stagemate.domain.user.entity.UserJpaEntity;
import com.example.stagemate.dto.request.community.CommunityPostCreateRequest;
import com.example.stagemate.dto.request.community.CommunityPostUpdateRequest;
import com.example.stagemate.dto.response.community.CommunityPostPagedResponse;
import com.example.stagemate.dto.response.community.CommunityPostResponse;
import com.example.stagemate.dto.response.community.CommunityPostTradePagedResponse;
import com.example.stagemate.global.auth.CustomUserDetails;
import com.example.stagemate.global.dto.DataResponse;
import com.example.stagemate.global.dto.ErrorResponse;
import com.example.stagemate.global.reslover.CurrentUser;
import com.example.stagemate.service.community.CommunityService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/communities")
@RequiredArgsConstructor
@Tag(name = "CommunityController", description = "Community 관련 API")
public class CommunityController {
    private final CommunityService communityService;
    private final ObjectMapper objectMapper;

    @Operation(summary = "커뮤니티 게시글 작성", description = "JSON 문자열 + 이미지 리스트(Multipart)로 커뮤니티 게시글을 작성합니다.")
    @ApiResponse(responseCode = "200", description = "작성 성공")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DataResponse<CommunityPostResponse>> createCommunityPost(
            @RequestPart("request") String requestJson,
            @RequestPart(value = "images", required = false)
            @Parameter(
                    description = "업로드할 이미지 파일들",
                    content = @Content(array = @ArraySchema(schema = @Schema(type = "string", format = "binary")))
            )
            List<MultipartFile> images,
            @Parameter(hidden = true) @CurrentUser UserJpaEntity user
    ) throws JsonProcessingException {
        CommunityPostCreateRequest request = objectMapper.readValue(requestJson, CommunityPostCreateRequest.class);
        CommunityPostResponse response = communityService.createCommunityPost(user, request, images);
        return ResponseEntity.ok(DataResponse.from(response));
    }

    @Operation(summary = "HOT 게시글 목록 조회", description = "페이징 기반으로 HOT 게시글을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/hot")
    public ResponseEntity<DataResponse<CommunityPostPagedResponse>> getCommunityHotPosts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @Parameter(hidden = true) @CurrentUser UserJpaEntity user
    ) {
        CommunityPostPagedResponse response = communityService.getCommunityHotPosts(
                user, page, size
        );
        return ResponseEntity.ok(DataResponse.from(response));
    }


    @Operation(summary = "일상/꿀팁 게시글 목록 조회", description = "페이징 기반으로 일상/꿀팁 카테고리 게시글을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping
    public ResponseEntity<DataResponse<CommunityPostPagedResponse>> getCommunityPosts(
            @RequestParam String category,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @Parameter(hidden = true) @CurrentUser UserJpaEntity user
    ) {
        CommunityPostPagedResponse response = communityService.getCommunityPosts(
                user, category, page, size
        );
        return ResponseEntity.ok(DataResponse.from(response));
    }

    @Operation(summary = "나눔거래 게시글 목록 조회", description = "페이징 기반으로 나눔거래 카테고리 게시글을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/trade")
    public ResponseEntity<DataResponse<CommunityPostTradePagedResponse>> getCommunityTradePosts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @Parameter(hidden = true) @CurrentUser UserJpaEntity user
    ) {
        CommunityPostTradePagedResponse response = communityService.getCommunityTradePosts(
                user, page, size
        );
        return ResponseEntity.ok(DataResponse.from(response));
    }

    @Operation(summary = "커뮤니티 게시글 상세 조회", description = "게시글 ID로 상세 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "상세 조회 성공")
    @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/{postId}")
    public ResponseEntity<DataResponse<CommunityPostResponse>> getCommunityPostDetail(
            @PathVariable Long postId,
            @Parameter(hidden = true) @CurrentUser UserJpaEntity user
    ) {
        CommunityPostResponse response = communityService.getCommunityPostDetail(
                postId, user
        );
        return ResponseEntity.ok(DataResponse.from(response));
    }

    @Operation(summary = "커뮤니티 게시글 좋아요", description = "게시글 좋아요 기능 (좋아요/취소)")
    @ApiResponse(responseCode = "200", description = "성공")
    @PostMapping("/{postId}/likes")
    public ResponseEntity<DataResponse<Void>> toggleCommunityPostLike(
            @PathVariable Long postId,
            @Parameter(hidden = true) @CurrentUser UserJpaEntity user
    ) {
        communityService.toggleCommunityPostLike(postId, user);
        return ResponseEntity.ok(DataResponse.ok());
    }

    @Operation(summary = "커뮤니티 게시글 스크랩", description = "게시글 스크랩 토글 기능 (스크랩/취소)")
    @ApiResponse(responseCode = "200", description = "성공")
    @PostMapping("/{postId}/scraps")
    public ResponseEntity<DataResponse<Void>> toggleCommunityPostScrap(
            @PathVariable Long postId,
            @Parameter(hidden = true) @CurrentUser UserJpaEntity user
    ) {
        communityService.toggleCommunityPostScrap(postId, user);
        return ResponseEntity.ok(DataResponse.ok());
    }

    @Operation(summary = "커뮤니티 게시글 수정", description = "게시글 본문 및 이미지 정보를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "수정 성공")
    @PutMapping(value = "/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DataResponse<CommunityPostResponse>> updateCommunityPost(
            @PathVariable Long postId,
            @RequestPart("request") String requestJson,
            @RequestPart(value = "images", required = false)
            @Parameter(
                    description = "추가할 이미지 파일들",
                    content = @Content(array = @ArraySchema(schema = @Schema(type = "string", format = "binary")))
            )
            List<MultipartFile> newImages,
            @Parameter(hidden = true) @CurrentUser UserJpaEntity user
    ) throws JsonProcessingException {
        CommunityPostUpdateRequest request = objectMapper.readValue(requestJson, CommunityPostUpdateRequest.class);
        CommunityPostResponse response = communityService.updateCommunityPost(user, postId, request, newImages);
        return ResponseEntity.ok(DataResponse.from(response));
    }

    @Operation(summary = "커뮤니티 게시글 삭제", description = "게시글을 소프트 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "삭제 성공")
    @DeleteMapping("/{postId}")
    public ResponseEntity<DataResponse<Void>> deletePost(
            @PathVariable Long postId,
            @Parameter(hidden = true) @CurrentUser UserJpaEntity user
    ) {
        communityService.deleteCommunityPost(postId, user);
        return ResponseEntity.ok(DataResponse.ok());
    }



}
