package com.example.stagemate.dto.response.archive;

import com.example.stagemate.domain.archive.Archive;
import com.example.stagemate.domain.image.Image;

import java.util.Optional;

public record ArchiveRankingResponse(
        Long id,
        String title,
        int ranking,
        double rating,
        String imageUrl //optional
) {
    public static ArchiveRankingResponse from(Archive archive, int ranking) {
        return new ArchiveRankingResponse(archive.getId(), archive.getTitle(),
                ranking, archive.getRating(),
                Optional.ofNullable(archive.getImage()).map(Image::getImageUrl).orElse(null));
    }
}
