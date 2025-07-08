package com.example.stagemate.controller;

import com.example.stagemate.domain.magazine.MagazineCreateForm;
import com.example.stagemate.dto.request.MagazineCreateRequest;
import com.example.stagemate.dto.response.MagazineListResponse;
import com.example.stagemate.dto.response.MagazinePagedResponse;
import com.example.stagemate.dto.response.MagazineResponse;
import com.example.stagemate.global.dto.DataResponse;
import com.example.stagemate.global.dto.ErrorResponse;
import com.example.stagemate.service.magazine.MagazineService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("/api/v1/magazines")
@RequiredArgsConstructor
@Tag(name = "MagazineController", description = "Magazine 관련 API")
public class MagazineController {

    private final MagazineService magazineService;
    private final ObjectMapper objectMapper;

    @Operation(
            summary = "매거진 작성",
            description = """
        매거진 정보를 JSON 문자열로 입력하고, 여러 이미지를 함께 업로드합니다.

        🔽 예시 JSON (request 필드 입력값):

        ```json
        {
          "title": "프랑스 배경 연극 추천",
          "subTitle": "프랑스 중세시대 이야기",
          "content": "안녕하세요. 최근에 프랑스 배경으로 연극이 많이 나오고 있습니다. 오늘 추천할 프랑스 배경 연극 추천은요...",
          "category": "연극"
        }
        ```
        """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "매거진 작성 성공"),
            @ApiResponse(responseCode = "404", description = "카테고리를 찾을 수 없음 (MAGAZINE-001)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 에러 (COMMON-005)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DataResponse<MagazineResponse>> createMagazine(
            @Parameter(
                    description = "매거진 작성 요청 정보 (JSON 문자열)",
                    required = true
            )
            @RequestPart("request") String requestJson,
            @Parameter(
                    description = "업로드할 이미지 파일들 (여러 개 가능)",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE,
                            array = @ArraySchema(schema = @Schema(type = "string", format = "binary"))
                    )
            )
            @RequestPart("images") List<MultipartFile> images

    ) throws JsonProcessingException {

        MagazineCreateRequest request = objectMapper.readValue(requestJson, MagazineCreateRequest.class);
        MagazineResponse response = magazineService.createMagazine(request, images);
        return ResponseEntity.ok(DataResponse.from(response));
    }


    // 최신 순 4개 보여주기
    @Operation(summary = "최신 매거진 4개 조회", description = "작성일 기준 최신 매거진 4개를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "매거진 목록 최신순 4개 조회 성공"),
    })
    @GetMapping("/latest")
    public ResponseEntity<DataResponse<List<MagazineListResponse>>> getLatestMagazines() {
        List<MagazineListResponse> magazines = magazineService.getLatestMagazines();
        return ResponseEntity.ok(DataResponse.from(magazines));
    }

    // 매거진 상세 보기
    @Operation(summary = "매거진 상세 조회", description = "매거진 ID로 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "매거진을 찾을 수 없음 (MAGAZINE-002)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{magazineId}")
    public ResponseEntity<DataResponse<MagazineResponse>> getMagazineDetail(@PathVariable Long magazineId) {
        MagazineResponse magazine = magazineService.getMagazineDetail(magazineId);
        return ResponseEntity.ok(DataResponse.from(magazine));
    }

    // 매거진 목록 보기(6개씩 페이징), 페이지 1부터 시작
    @Operation(summary = "매거진 목록 조회 (페이징)", description = "매거진을 페이지 단위로 조회합니다. 페이지는 1부터 시작합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "매거진 목록 페이지 단위 조회 성공"),
    })
    @GetMapping
    public ResponseEntity<DataResponse<MagazinePagedResponse>> getMagazines(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "6") int size
    ) {
        MagazinePagedResponse magazines = magazineService.getMagazineList(page, size);
        return ResponseEntity.ok(DataResponse.from(magazines));
    }

    // 매거진 삭제하기
    @Operation(summary = "매거진 삭제", description = "매거진 ID로 매거진을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "매거진을 찾을 수 없음 (MAGAZINE-002)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{magazineId}")
    public ResponseEntity<DataResponse<Void>> deleteMagazine(@PathVariable Long magazineId) {
        magazineService.deleteMagazine(magazineId);
        return ResponseEntity.ok(DataResponse.ok());
    }



    // 매거진 좋아요

    // 매거진 스크랩

    // 좋아요 + 스크랩 많은 순 추천 매거진 4개 보여주기
}
