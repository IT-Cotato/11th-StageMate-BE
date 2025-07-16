package com.example.stagemate.domain.community;

import com.example.stagemate.global.exception.AppException;
import lombok.Getter;

import static com.example.stagemate.global.exception.community.CommunityErrorCode.COMMUNITY_CATEGORY_NOT_FOUND;


@Getter
public enum CommunityCategory {
    DAILY("일상"),
    TRADE("나눔거래"),
    TIP("꿀팁");

    private String description;

    CommunityCategory(String description) {
        this.description = description;
    }

    // request의 카테고리를 Community enum으로 변환(한국어 -> 영어)
    public static CommunityCategory from(String category) {
        for (CommunityCategory communityCategory : CommunityCategory.values()) {
            if (communityCategory.getDescription().equals(category)) {
                return communityCategory;
            }
        }
        throw new AppException(COMMUNITY_CATEGORY_NOT_FOUND);
    }
}
