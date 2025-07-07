package com.example.stagemate.domain.performance;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PerformanceGenre  {
    ORIGINAL_AND_TOURING_MUSICAL("오리지널/내한"),
    LICENSED_MUSICAL("라이선스"),
    CREATIVE_MUSICAL("창작뮤지컬"),
    NONVERBAL_PERFORMANCE("넌버벌퍼포먼스"),
    FAMILY_MUSICAL("아동/가족 뮤지컬"),

    LIMITED_RUN("리미티드런"),
    STEADY_SELLER("스테디셀러"),
    FAMILY_PLAY("아동/가족 연극");

    private final String description;

    public static PerformanceGenre fromDescription(String description) {
        for (PerformanceGenre genre : PerformanceGenre.values()) {
            if (genre.getDescription().equals(description)) {
                return genre;
            }
        }
        return null; //exception 처리 필요
    }

    public static boolean contains(String description) {
        for (PerformanceGenre genre : PerformanceGenre.values()) {
            if (genre.getDescription().equals(description)) {
                return true;
            }
        }
        return false;
    }


}
