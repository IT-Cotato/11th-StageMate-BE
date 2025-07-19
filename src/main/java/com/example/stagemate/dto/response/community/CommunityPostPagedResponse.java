package com.example.stagemate.dto.response.community;

import java.util.List;

public record CommunityPostPagedResponse (
        List<CommunityPostListResponse> list,
        int currentPage,
        int pageSize,
        long totalElements,
        int totalPages
) {
}