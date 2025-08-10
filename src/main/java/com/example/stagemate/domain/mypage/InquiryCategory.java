package com.example.stagemate.domain.mypage;

public enum InquiryCategory {
    PERFORMANCE_INFO_UPDATE("공연 정보 수정 요청"),
    COMMUNITY_USAGE("커뮤니티 이용 관련"),
    REPORT_BLOCK("신고 및 차단 요청"),
    ACCOUNT_LOGIN("계정/로그인 문제"),
    PARTNERSHIP_AD("제휴 및 광고 문의"),
    OTHER("기타");

    private final String displayName;

    InquiryCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

}
