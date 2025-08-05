package com.example.stagemate.controller;

import com.example.stagemate.domain.performance.PerformanceGenre;
import com.example.stagemate.domain.user.entity.UserJpaEntity;
import com.example.stagemate.dto.response.search.PopularKeywordResponse;
import com.example.stagemate.dto.response.search.SearchResultResponse;
import com.example.stagemate.global.dto.DataResponse;
import com.example.stagemate.global.dto.ErrorResponse;
import com.example.stagemate.global.reslover.CurrentUser;
import com.example.stagemate.service.search.KeywordService;
import com.example.stagemate.service.search.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/search")
public class SearchController {

    private final SearchService searchService;
    private final KeywordService keywordService;

    @Operation(summary = "실시간 인기 검색어 확인", description = "10분 단위 기준으로 실시간 인기 검색어를 확인합니다. 최대 10개를 보여줍니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "실시간 인기 검색어 확인 성공"),
            @ApiResponse(responseCode = "500", description = "Redis 작업 중 오류가 발생했습니다. (REDIS-001)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/popular")
    public ResponseEntity<DataResponse<PopularKeywordResponse>> getPopular() {
        return ResponseEntity.ok(DataResponse.from(keywordService.getTop10()));
    }


    @Operation(
            summary = "검색하기",
            description = "키워드, 장르, 날짜로 공연(채팅방 포함)/커뮤니티 글을 검색할 수 있습니다. 최대 30개를 반환합니다.\n " +
                    "커뮤니티 글은 키워드 기반으로만 검색됩니다. 키워드를 쓰지 않은 경우 커뮤니티 글은 null 반환합니다.\n" +
                    "날짜를 입력하지 않으면 현재 상영중/상영예정인 공연을 반환합니다. 날짜를 입력하면 그 기간 내에 상영 중인 것을 보여줍니다.\n" +
                    "채팅방이 만들어지지 않으면 null을 반환하고 존재하면 채팅방 Id 를 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "검색 성공"),
            @ApiResponse(responseCode = "500", description = "Redis 작업 중 오류가 발생했습니다. (SEARCH-001) / 엘라스틱서치 작업 중 오류가 발생했습니다. (SEARCH-002) ",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<DataResponse<SearchResultResponse>> search(
            @Parameter(hidden = true) @CurrentUser UserJpaEntity user,
            @RequestParam(required = false) String keyword,
            @RequestParam(name = "performanceGenre", required = false) PerformanceGenre performanceGenre,
            @RequestParam(name = "date", required = false) LocalDate date
    ) {
        SearchResultResponse result = searchService.searchAll(keyword, performanceGenre, date, user);
        return ResponseEntity.ok(DataResponse.from(result));
    }


}

