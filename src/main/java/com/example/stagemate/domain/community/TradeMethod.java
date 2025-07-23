package com.example.stagemate.domain.community;

import com.example.stagemate.global.exception.AppException;
import lombok.Getter;

import static com.example.stagemate.global.exception.community.CommunityErrorCode.TRADE_CATEGORY_NOT_FOUND;
import static com.example.stagemate.global.exception.community.CommunityErrorCode.TRADE_METHOD_NOT_FOUND;

@Getter
public enum TradeMethod {
    RAFFLE("추첨나눔"),
    FIRST_COME("선착나눔"),
    SALE("판매");

    private final String description;

    TradeMethod(String description) {
        this.description = description;
    }

    public static TradeMethod from(String method) {
        if (method == null) return null; // 나눔거래가 아닐 경우
        for (TradeMethod tradeMethod : TradeMethod.values()) {
            if (tradeMethod.getDescription().equals(method)) {
                return tradeMethod;
            }
        }
        throw new AppException(TRADE_METHOD_NOT_FOUND);
    }
}
