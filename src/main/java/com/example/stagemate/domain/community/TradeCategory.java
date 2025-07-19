package com.example.stagemate.domain.community;

import com.example.stagemate.global.exception.AppException;
import lombok.Getter;

import static com.example.stagemate.global.exception.community.CommunityErrorCode.TRADE_CATEGORY_NOT_FOUND;

@Getter
public enum TradeCategory {
    MUSICAL("뮤지컬"),
    PLAY("연극");

    private final String description;

    TradeCategory(String description) {
        this.description = description;
    }

    public static TradeCategory from(String category) {
        if (category == null) return null; // 나눔거래가 아닐 경우
        for (TradeCategory tradeCategory : TradeCategory.values()) {
            if (tradeCategory.getDescription().equals(category)) {
                return tradeCategory;
            }
        }
        throw new AppException(TRADE_CATEGORY_NOT_FOUND);
    }
}
