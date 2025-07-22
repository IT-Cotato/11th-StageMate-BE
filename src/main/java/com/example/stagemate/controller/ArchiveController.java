package com.example.stagemate.controller;

import com.example.stagemate.domain.user.entity.UserJpaEntity;
import com.example.stagemate.dto.request.ArchiveCreateRequest;
import com.example.stagemate.dto.request.ArchiveUpdateRequest;
import com.example.stagemate.dto.response.ArchiveDetailResponse;
import com.example.stagemate.global.dto.DataResponse;
import com.example.stagemate.global.dto.ErrorResponse;
import com.example.stagemate.global.reslover.CurrentUser;
import com.example.stagemate.service.archive.ArchiveService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Tag(name = "Archive", description = "Archive API")
public class ArchiveController {
    private final ArchiveService archiveService;
    private final ObjectMapper objectMapper;

    @Operation(summary = "아카이브 상세 정보", description = "아카이브 상세 정보를 가져옴")
    @ApiResponse(responseCode = "200", description = "아카이브 상세 정보를 가져옴")
    @GetMapping("/api/v1/archive/{archiveId}")
    public ResponseEntity<DataResponse<ArchiveDetailResponse>> getArchive(
            @Parameter(hidden = true) @CurrentUser UserJpaEntity user,
            @PathVariable("archiveId") Long archiveId) {

        ArchiveDetailResponse archiveDetailResponse = archiveService.getArchive(user, archiveId);

        return ResponseEntity.ok(DataResponse.from(archiveDetailResponse));
    }

    //월별 공연 아카이빙
    @Operation(summary = "월별 공연 아카이브", description = "월별 공연 아카이브 목록을 가져옴")
    @ApiResponse(responseCode = "200", description = "월별 공연 아카이브 목록을 가져옴")
    @GetMapping("/api/v1/archives")
    public ResponseEntity<DataResponse<List<ArchiveDetailResponse>>> getArchives(
            @Parameter(hidden = true) @CurrentUser UserJpaEntity user,
            @RequestParam("year") Integer year,
            @RequestParam("month") Integer month) {

        List<ArchiveDetailResponse> archiveDetailResponses = archiveService.getArchives(user, year, month);
        return ResponseEntity.ok(DataResponse.from(archiveDetailResponses));
    }

    @Operation(
            summary = "아카이브 생성",
            description = """
        아카이브 정보를 JSON 문자열로 입력하고, 이미지 파일을 함께 업로드합니다.

        🔽 예시 JSON (request 필드 입력값):

        ```json
        {
          "viewingDate": "2025-07-21",
          "casting": "홍길동, 김연아",
          "review": "배우들의 연기가 인상 깊었습니다.",
          "theaterName": "블루스퀘어 신한카드홀",
          "rating": 4.5,
          "memo": "연출도 훌륭했고, 다시 보고 싶어요."
        }
        ```

        - `viewingDate`: 관람한 날짜 (`yyyy-MM-dd`)
        - `casting`: 출연 배우 목록 (쉼표 구분 문자열)
        - `review`: 공연에 대한 리뷰 내용
        - `theaterName`: 공연장 이름
        - `rating`: 평점 (0.0 ~ 5.0, 0.5 단위)
        - `memo`: 자유 메모
        """
    )
    @ApiResponse(responseCode = "200", description = "아카이브 생성")
    @PostMapping(value = "/api/v1/archive", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DataResponse<Long>> createArchive(
            @Parameter(hidden = true) @CurrentUser UserJpaEntity user,
            @RequestPart("request") String requestJson,
            @Parameter(
                    description = "업로드할 이미지 파일",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE,
                            schema = @Schema(type = "string", format = "binary")))
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws JsonProcessingException {

        ArchiveCreateRequest archiveCreateRequest = objectMapper.readValue(requestJson, ArchiveCreateRequest.class);
        archiveCreateRequest.validate();
        Long archiveId = archiveService.createArchive(user, archiveCreateRequest, image);
        return ResponseEntity.ok(DataResponse.from(archiveId));
    }


    @Operation(summary = "아카이브 삭제", description = "아카이브 삭제")
    @ApiResponse(responseCode = "200", description = "아카이브 삭제")
    @DeleteMapping("/api/v1/archive/{archiveId}")
    public ResponseEntity<DataResponse<Void>> deleteArchive(
            @Parameter(hidden = true) @CurrentUser UserJpaEntity user,
            @PathVariable Long archiveId) {

        archiveService.deleteArchive(user, archiveId);

        return ResponseEntity.ok(DataResponse.ok());
    }

//    archive Update Reqeust 필요
@Operation(
        summary = "아카이브 수정",
        description = """
        아카이브 정보를 JSON 문자열로 입력하고, 이미지 파일을 함께 업로드합니다.

        🔽 예시 JSON (request 필드 입력값):

        ```json
        {
          "viewingDate": "2025-07-21",
          "casting": "홍길동, 김연아",
          "review": "배우들의 연기가 인상 깊었습니다.",
          "theaterName": "블루스퀘어 신한카드홀",
          "rating": 4.5,
          "memo": "연출도 훌륭했고, 다시 보고 싶어요."
        }
        ```

        - `viewingDate`: 관람한 날짜 (`yyyy-MM-dd`)
        - `casting`: 출연 배우 목록 (쉼표 구분 문자열)
        - `review`: 공연에 대한 리뷰 내용
        - `theaterName`: 공연장 이름
        - `rating`: 평점 (0.0 ~ 5.0, 0.5 단위)
        - `memo`: 자유 메모
        """
)
    @ApiResponse(responseCode = "200", description = "아카이브 변경")
    @PatchMapping(value = "/api/v1/archive/{archiveId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DataResponse<?>> updateArchive(
            @Parameter(hidden = true) @CurrentUser UserJpaEntity user,
            @PathVariable Long archiveId,
            @RequestPart String reqeustJson,
            @Parameter(
                    description = "업로드할 이미지 파일",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE,
                            schema = @Schema(type = "string", format = "binary")))
            @RequestPart(value = "image", required = false) MultipartFile updatedImage
) throws JsonProcessingException {

        ArchiveUpdateRequest archiveUpdateRequest = objectMapper.readValue(reqeustJson, ArchiveUpdateRequest.class);
        archiveUpdateRequest.validate();

        archiveService.updateArchive(user,archiveId, archiveUpdateRequest,updatedImage);
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
