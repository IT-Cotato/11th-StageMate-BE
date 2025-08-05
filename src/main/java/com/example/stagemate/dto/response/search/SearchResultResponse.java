package com.example.stagemate.dto.response.search;

import com.example.stagemate.dto.response.community.CommunityPostListResponse;

import java.util.List;

public record SearchResultResponse(
        List<PerformanceSearchResponse> performances,
        List<CommunityPostListResponse> community
) {
    public static SearchResultResponse of(List<PerformanceSearchResponse> performances, List<CommunityPostListResponse> community) {
        return new SearchResultResponse(performances, community);
    }
}
