package com.example.stagemate.dto.response.community;

import java.util.List;

public record CommunityPostTradePagedResponse(
        List<CommunityPostTradeListResponse> list,
        int currentPage,
        int pageSize,
        long totalElements,
        int totalPages
) {
}