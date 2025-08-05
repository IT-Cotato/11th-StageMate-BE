package com.example.stagemate.controller;

import com.example.stagemate.domain.user.entity.UserJpaEntity;
import com.example.stagemate.dto.request.community.CommunityPostCreateRequest;
import com.example.stagemate.dto.request.community.CommunityPostUpdateRequest;
import com.example.stagemate.dto.response.community.CommunityPostListResponse;
import com.example.stagemate.dto.response.community.CommunityPostResponse;
import com.example.stagemate.dto.response.community.CommunityPostTradeListResponse;
import com.example.stagemate.global.dto.DataResponse;
import com.example.stagemate.global.dto.ErrorResponse;
import com.example.stagemate.global.dto.PagedResponse;
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
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(
            summary = "커뮤니티 게시글 작성",
            description = """
        JSON 문자열 + 이미지 리스트(Multipart)로 커뮤니티 게시글을 작성합니다.<br>
        작성시 category는 일상/꿀팁/나눔거래, tradeCategory는 뮤지컬/연극, tradeMethod는 추첨나눔/선착나눔/판매입니다.(category가 나눔거래가 아닐 경우 tradeCategory,tradeMethod에는 null 작성)<br>
        JSON 문자열 작성 예시는 길어서 노션에 적어놓았습니다.
        """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "작성 성공"),
            @ApiResponse(responseCode = "400", description = "요청 형식 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 필요 (COMMON-009)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = """
                    - 사용자를 찾을 수 없음 (COMMON-008)
                    - 커뮤니티 카테고리를 찾을 수 없음 (COMMUNITY-001)
                    - 나눔거래 카테고리를 찾을 수 없음 (COMMUNITY-003)
                    - 나눔거래 방법을 찾을 수 없음 (COMMUNITY-005)
                    """,content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "이미지 업로드에 실패했습니다. (IMAGE-001)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
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
        request.validate();
        CommunityPostResponse response = communityService.createCommunityPost(user, request, images);
        return ResponseEntity.ok(DataResponse.from(response));
    }

    @Operation(summary = "HOT 게시글 목록 조회", description = "페이징 기반으로 HOT 게시글을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/hot")
    public ResponseEntity<DataResponse<PagedResponse<CommunityPostListResponse>>> getCommunityHotPosts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @Parameter(hidden = true) @CurrentUser UserJpaEntity user
    ) {
        PagedResponse<CommunityPostListResponse> communityHotPosts = communityService.getCommunityHotPosts(
                user, page, size
        );
        return ResponseEntity.ok(DataResponse.from(communityHotPosts));
    }


    @Operation(summary = "일상/꿀팁 게시글 목록 조회", description = "페이징 기반으로 일상/꿀팁 카테고리 게시글을 조회합니다."+
    " 카테고리는 '일상' 또는 '꿀팁' 중 하나여야 합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping
    public ResponseEntity<DataResponse<PagedResponse<CommunityPostListResponse>>> getCommunityPosts(
            @RequestParam String category,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @Parameter(hidden = true) @CurrentUser UserJpaEntity user
    ) {
        PagedResponse<CommunityPostListResponse> response = communityService.getCommunityPosts(
                user, category, page, size
        );
        return ResponseEntity.ok(DataResponse.from(response));
    }

    @Operation(summary = "나눔거래 게시글 목록 조회", description = "페이징 기반으로 나눔거래 카테고리 게시글을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/trade")
    public ResponseEntity<DataResponse<PagedResponse<CommunityPostTradeListResponse>>> getCommunityTradePosts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @Parameter(hidden = true) @CurrentUser UserJpaEntity user
    ) {
        PagedResponse<CommunityPostTradeListResponse> communityTradePosts = communityService.getCommunityTradePosts(
                user, page, size
        );
        return ResponseEntity.ok(DataResponse.from(communityTradePosts));
    }

    @Operation(summary = "커뮤니티 게시글 상세 조회", description = "게시글 ID로 상세 정보를 조회합니다. 댓글과 대댓글도 함께 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "상세 조회 성공"),
            @ApiResponse(responseCode = "401", description = """
                    - 인증 필요 (COMMON-009)
                    - 회원 전용 게시글입니다 (COMMUNITY-017)
                    """,content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "차단한 사용자의 게시글입니다 (COMMUNITY-014)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음 (COMMUNITY-002)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{postId}")
    public ResponseEntity<DataResponse<CommunityPostResponse>> getCommunityPostDetail(
            @PathVariable Long postId,
            @Parameter(hidden = true) @CurrentUser UserJpaEntity user
    ) throws JsonProcessingException {
        CommunityPostResponse response = communityService.getCommunityPostDetail(
                postId, user
        );
        return ResponseEntity.ok(DataResponse.from(response));
    }

    @Operation(summary = "커뮤니티 게시글 좋아요", description = "게시글 좋아요 기능 (좋아요/취소)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요 (COMMON-009)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음 (COMMUNITY-002)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/{postId}/likes")
    public ResponseEntity<DataResponse<Void>> toggleCommunityPostLike(
            @PathVariable Long postId,
            @Parameter(hidden = true) @CurrentUser UserJpaEntity user
    ) {
        communityService.toggleCommunityPostLike(postId, user);
        return ResponseEntity.ok(DataResponse.ok());
    }

    @Operation(summary = "커뮤니티 게시글 스크랩", description = "게시글 스크랩 토글 기능 (스크랩/취소)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요 (COMMON-009)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음 (COMMUNITY-002)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/{postId}/scraps")
    public ResponseEntity<DataResponse<Void>> toggleCommunityPostScrap(
            @PathVariable Long postId,
            @Parameter(hidden = true) @CurrentUser UserJpaEntity user
    ) {
        communityService.toggleCommunityPostScrap(postId, user);
        return ResponseEntity.ok(DataResponse.ok());
    }

    @Operation(
            summary = "커뮤니티 게시글 수정",
            description = """
        게시글 본문 및 이미지 정보를 수정합니다.<br>
        작성시 category는 일상/꿀팁/나눔거래, tradeCategory는 뮤지컬/연극, tradeMethod는 추첨나눔/선착나눔/판매입니다.(category가 나눔거래가 아닐 경우 tradeCategory,tradeMethod에는 null 작성)<br>
        이미지 리스트는 게시글 조회 시 받은 이미지 id 중 사용자가 원래 있던 이미지 중 수정하면서 없애지 않은 이미지 id를 넣으면 됩니다. 추가하는 이미지는 따로 업로드 받습니다.<br>
        JSON 문자열 작성 예시는 길어서 노션에 적어놓았습니다.
        """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "요청 형식 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 필요 (COMMON-009)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "게시글 작성자가 아님 (COMMUNITY-004)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음 (COMMUNITY-002)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "이미지 업로드에 실패했습니다. (IMAGE-001)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
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
        request.validate();
        CommunityPostResponse response = communityService.updateCommunityPost(user, postId, request, newImages);
        return ResponseEntity.ok(DataResponse.from(response));
    }

    @Operation(summary = "커뮤니티 게시글 삭제", description = "게시글을 소프트 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요 (COMMON-009)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "게시글 작성자가 아님 (COMMUNITY-004)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음 (COMMUNITY-002)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{postId}")
    public ResponseEntity<DataResponse<Void>> deletePost(
            @PathVariable Long postId,
            @Parameter(hidden = true) @CurrentUser UserJpaEntity user
    ) {
        communityService.deleteCommunityPost(postId, user);
        return ResponseEntity.ok(DataResponse.ok());
    }

}
