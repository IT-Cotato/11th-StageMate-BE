package com.example.stagemate.dto.response.chat;

public record ChatReportCountResponse(
        Long userId,
        Long reportCount
) {
    public static ChatReportCountResponse from(Long userId, Long reportCount) {
        return new ChatReportCountResponse(userId, reportCount);
    }
}
