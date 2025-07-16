package com.example.stagemate.global.exception.community;

import com.example.stagemate.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommunityErrorCode implements ErrorCode {

    COMMUNITY_CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "커뮤니티 카테고리를 찾을 수 없습니다.", "COMMUNITY-001"),
    COMMUNITY_POST_NOT_FOUND(HttpStatus.NOT_FOUND, "커뮤니티 게시글을 찾을 수 없습니다.", "COMMUNITY-002"),
    TRADE_CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "나눔거래 카테고리를 찾을 수 없습니다.", "COMMUNITY-003"),
    COMMUNITY_POST_NOT_AUTHOR(HttpStatus.FORBIDDEN, "게시글 작성자가 아닙니다.", "COMMUNITY-004"),
    TRADE_METHOD_NOT_FOUND(HttpStatus.NOT_FOUND, "나눔거래 방법을 찾을 수 없습니다.", "COMMUNITY-005"),
    ;

    private final HttpStatus httpStatus;
    private final String message;
    private final String code;
}
