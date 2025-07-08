package com.example.stagemate.dto.response;

import com.example.stagemate.domain.magazine.Magazine;

import java.util.List;

public record MagazineResponse(
        Long id,
        String title,
        String subTitle,
        String content,
        List<String> imageUrls,
        String category,
        String createdAt
) {
    public static MagazineResponse from(Magazine magazine) {
        return new MagazineResponse(
                magazine.getId(),
                magazine.getTitle(),
                magazine.getSubTitle(),
                magazine.getContent(),
                magazine.getImages().isEmpty() ? List.of("basic") : magazine.getImages().stream().map(image -> image.getImage().getImageUrl()).toList(),
                magazine.getCategory().name(),
                magazine.getCreatedAt().toString()
            );
    }
}
