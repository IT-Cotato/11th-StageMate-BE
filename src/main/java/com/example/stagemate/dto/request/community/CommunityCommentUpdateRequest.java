package com.example.stagemate.dto.request.community;

import com.example.stagemate.global.exception.AppException;

import static com.example.stagemate.global.exception.community.CommunityErrorCode.COMMUNITY_COMMENT_CREATE_INVALID_INPUT;

public record CommunityCommentUpdateRequest(
        String content
) {
    public void validate() {
        if (isBlank(content)) {
            throw new AppException(COMMUNITY_COMMENT_CREATE_INVALID_INPUT);
        }
    }

    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
}