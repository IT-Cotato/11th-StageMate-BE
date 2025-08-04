package com.example.stagemate.dto.response.naverImage;

import java.util.List;

public record NaverImageSearchResponse(
        String lastBuildDate,
        int total,
        int start,
        int display,
        List<NaverImageItem> items
) {
}
