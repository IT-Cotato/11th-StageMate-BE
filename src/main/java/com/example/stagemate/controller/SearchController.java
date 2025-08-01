package com.example.stagemate.controller;

import com.example.stagemate.domain.user.entity.UserJpaEntity;
import com.example.stagemate.dto.response.community.CommunityPostListResponse;
import com.example.stagemate.global.dto.DataResponse;
import com.example.stagemate.global.reslover.CurrentUser;
import com.example.stagemate.service.search.SearchIndexInitializerService;
import com.example.stagemate.service.search.SearchService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/search")
public class SearchController {

    private final SearchService searchService;
    private final SearchIndexInitializerService indexService;


    @GetMapping("/community")
    public ResponseEntity<DataResponse<List<CommunityPostListResponse>>> searchCommunity(
            @Parameter(hidden = true) @CurrentUser UserJpaEntity user,
            @RequestParam String keyword) {
        return ResponseEntity.ok(DataResponse.from(searchService.searchCommunityPosts(keyword, user)));
    }




}

