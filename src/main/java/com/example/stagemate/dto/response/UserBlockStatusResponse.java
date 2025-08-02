package com.example.stagemate.dto.response;

public record UserBlockStatusResponse(Long userId, boolean isBlocked) {
    public static UserBlockStatusResponse from(Long userId, boolean isBlocked) {
        return new UserBlockStatusResponse(userId, isBlocked);
    }
}
