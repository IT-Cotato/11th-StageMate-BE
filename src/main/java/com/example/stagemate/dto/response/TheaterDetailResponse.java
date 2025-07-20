package com.example.stagemate.dto.response;

public record TheaterDetailResponse(
        String theaterName,
        String region
) {
    public static TheaterDetailResponse from(com.example.stagemate.domain.theater.Theater theater) {
        return new TheaterDetailResponse(theater.getName(), theater.getRegion());
    }
}
