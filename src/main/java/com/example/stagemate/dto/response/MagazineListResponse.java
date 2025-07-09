package com.example.stagemate.dto.response;

import com.example.stagemate.domain.magazine.Magazine;
import com.example.stagemate.domain.magazine.MagazineStatistics;

import java.util.List;

public record MagazineListResponse(
        Long id,
        String title,
        String subTitle,
        String imageUrl, // 맨 처음 이미지만 보이게
        String category
) {
    public static MagazineListResponse from(Magazine magazine) {
        return new MagazineListResponse(
                magazine.getId(),
                magazine.getTitle(),
                magazine.getSubTitle(),
                magazine.getImages().isEmpty() ? "basic" : magazine.getImages().get(0).getImage().getImageUrl(),
                magazine.getCategory().name()
        );
    }
}
