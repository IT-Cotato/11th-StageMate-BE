package com.example.stagemate.dto.response.chat;

public record ChatProfileResponse(
        Long senderId,
        String profileImageUrl,
        String nickname,
        Integer reportedCount,
        Boolean isBlocked
) {
    public static ChatProfileResponse from(
            Long senderId, String profileImageUrl, String nickname, Integer reportedCount, Boolean isBlocked) {
        return new ChatProfileResponse(senderId, profileImageUrl, nickname, reportedCount, isBlocked);
    }
}
