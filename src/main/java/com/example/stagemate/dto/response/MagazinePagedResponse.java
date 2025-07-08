package com.example.stagemate.dto.response;

import java.util.List;

public record MagazinePagedResponse(
        List<MagazineListResponse> list,
        int currentPage,
        int pageSize,
        long totalElements,
        int totalPages
) {
}
