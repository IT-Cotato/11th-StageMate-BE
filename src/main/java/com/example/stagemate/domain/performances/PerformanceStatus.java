package com.example.stagemate.domain.performances;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PerformanceStatus {
    UPCOMING("상영예정"),
    ONGOING("상영중"),
    ENDED("상영종료");

    private final String description;

    public static PerformanceStatus fromDescription(String description) {
        for (PerformanceStatus status : PerformanceStatus.values()) {
            if (status.getDescription().equals(description)) {
                return status;
            }
        }
        return null; //exception 처리 필요
    }



}
