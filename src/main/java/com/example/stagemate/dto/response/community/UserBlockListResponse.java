package com.example.stagemate.dto.response.community;

import com.example.stagemate.domain.community.UserBlock;
import com.example.stagemate.domain.user.entity.UserJpaEntity;


public record UserBlockListResponse(
        Long id,
        String nickname
) {
    public static UserBlockListResponse from(UserBlock userBlock) {
        UserJpaEntity blocked = userBlock.getBlocked();
        return new UserBlockListResponse(
                blocked.getId(),
                blocked.getNickname()
        );
    }
}
