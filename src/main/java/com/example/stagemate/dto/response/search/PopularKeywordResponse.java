package com.example.stagemate.dto.response.search;

import java.time.LocalDateTime;
import java.util.List;

public record PopularKeywordResponse(
        LocalDateTime time,
        List<String> keywords
) {
}
