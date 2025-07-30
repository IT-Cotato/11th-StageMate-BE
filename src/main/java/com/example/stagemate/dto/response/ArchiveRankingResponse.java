package com.example.stagemate.dto.response;

import com.example.stagemate.domain.archive.Archive;

public record ArchiveRankingResponse(
        Long id,
        String title,
        int ranking,
        double rating,
        String imageUrl
) {
    public static ArchiveRankingResponse from(Archive archive, int ranking) {
        return new ArchiveRankingResponse(archive.getId(), archive.getTitle(),
                ranking, archive.getRating(), archive.getImage().getImageUrl());
    }
}
