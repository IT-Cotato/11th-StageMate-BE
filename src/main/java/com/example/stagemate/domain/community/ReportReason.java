package com.example.stagemate.domain.community;

import com.example.stagemate.global.exception.AppException;

import static com.example.stagemate.global.exception.community.CommunityErrorCode.REPORT_REASON_NOT_FOUND;

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

    public static ReportReason fromString(String name) {
        try {
            return ReportReason.valueOf(name);
        } catch (IllegalArgumentException e) {
            throw new AppException(REPORT_REASON_NOT_FOUND);
        }
    }
}
