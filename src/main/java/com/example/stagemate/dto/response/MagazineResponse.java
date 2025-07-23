package com.example.stagemate.dto.response;

import com.example.stagemate.domain.magazine.Magazine;

import java.time.LocalDateTime;
import java.util.List;

public record MagazineResponse(
        Long id,
        String title,
        String subTitle,
        String content,
        List<String> imageUrls,
        String category,
        LocalDateTime createdAt,
        int likeCount,
        int scrapCount,
        boolean isScraped,
        boolean isLiked
) {
    public static MagazineResponse from(Magazine magazine, boolean isScraped, boolean isLiked) {
        return new MagazineResponse(
                magazine.getId(),
                magazine.getTitle(),
                magazine.getSubTitle(),
                magazine.getContent(),
                magazine.getImages().isEmpty() ? List.of("basic") : magazine.getImages().stream().map(image -> image.getImage().getImageUrl()).toList(),
                magazine.getCategory().getDescription(),
                magazine.getCreatedAt(),
                magazine.getLikeCount(),
                magazine.getScrapCount(),
                isScraped,
                isLiked
            );
    }
}
