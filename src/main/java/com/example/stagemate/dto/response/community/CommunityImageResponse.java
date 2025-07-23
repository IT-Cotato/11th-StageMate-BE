package com.example.stagemate.dto.response.community;

import com.example.stagemate.domain.community.CommunityImage;

public record CommunityImageResponse(
        Long id,         // 이미지 id
        String url       // 이미지 URL
) {
    public static CommunityImageResponse from(CommunityImage communityImage) {
        return new CommunityImageResponse(communityImage.getImage().getImageId(), communityImage.getImage().getImageUrl());
    }
}