package com.example.stagemate.dto.response.search;

import com.example.stagemate.dto.response.community.CommunityPostListResponse;

import java.util.List;

public record SearchResultIdsResponse(
        List<Long> performanceIds,
        List<Long> communityIds
) {
    public static SearchResultIdsResponse of(List<Long> performanceIds, List<Long> communityIds) {
        return new SearchResultIdsResponse(performanceIds, communityIds);
    }
}