package com.example.stagemate.domain.community;

import lombok.Getter;

public enum ReportReason {
    BAIT("낚시 / 놀림 / 도배"),
    LEAK_IMPERSONATION_FRAUD("유출 / 사칭 / 사기"),
    COMMERCIAL_AD("상업적 광고 및 판매"),
    ILLEGAL_CONTENT("불법촬영물 등의 유통"),
    OBSCENE("음란물 / 불건전한 대화"),
    ABUSE("욕설 / 비하");

    private final String description;

    ReportReason(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
