package com.example.stagemate.controller;

import com.example.stagemate.domain.performance.PerformanceGenre;
import com.example.stagemate.domain.user.entity.UserJpaEntity;
import com.example.stagemate.dto.response.community.CommunityPostListResponse;
import com.example.stagemate.dto.response.performance.PerformanceDetailResponse;
import com.example.stagemate.dto.response.search.PopularKeywordResponse;
import com.example.stagemate.global.dto.DataResponse;
import com.example.stagemate.global.reslover.CurrentUser;
import com.example.stagemate.service.search.KeywordService;
import com.example.stagemate.service.search.SearchService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/search")
public class SearchController {

    private final SearchService searchService;
    private final KeywordService keywordService;


    @GetMapping("/community")
    public ResponseEntity<DataResponse<List<CommunityPostListResponse>>> searchCommunity(
            @Parameter(hidden = true) @CurrentUser UserJpaEntity user,
            @RequestParam String keyword) {
        return ResponseEntity.ok(DataResponse.from(searchService.searchCommunityPosts(keyword, user)));
    }

    @GetMapping("/performances")
    public ResponseEntity<DataResponse<List<PerformanceDetailResponse>>> searchPerformances(
            @RequestParam String keyword,
            @RequestParam(name = "performanceGenre", required = false) PerformanceGenre performanceGenre,
            // 날짜
            @RequestParam(name = "date", required = false) LocalDate localDate
            ) {
        return ResponseEntity.ok(DataResponse.from(searchService.searchPerformances(keyword, performanceGenre, localDate)));
    }

    @GetMapping("/popular")
    public ResponseEntity<DataResponse<PopularKeywordResponse>> getPopular() {
        return ResponseEntity.ok(DataResponse.from(keywordService.getTop10()));
    }


}

