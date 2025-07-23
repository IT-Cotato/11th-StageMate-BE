package com.example.stagemate.domain.performance;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PerformanceType {
    MUSICAL("뮤지컬"),
    PLAY("연극"),
    ETC("기타");

    private final String description;

    public static PerformanceType fromDescription(String description) {
        for (PerformanceType type : PerformanceType.values()) {
            if (type.getDescription().equals(description)) {
                return type;
            }
        }
        return null; //exception 처리 필요
    }
}
