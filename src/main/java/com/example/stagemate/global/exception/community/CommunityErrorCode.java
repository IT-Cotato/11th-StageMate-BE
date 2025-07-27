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
    COMMUNITY_COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "커뮤니티 댓글을 찾을 수 없습니다.", "COMMUNITY-006"),
    COMMUNITY_COMMENT_NOT_AUTHOR(HttpStatus.FORBIDDEN, "댓글 작성자가 아닙니다.", "COMMUNITY-007"),
    COMMUNITY_REPLY_NOT_ALLOWED(HttpStatus.FORBIDDEN, "대댓글에는 다시 대댓글을 달 수 없습니다.", "COMMUNITY-008"),
    REPORT_REASON_NOT_FOUND(HttpStatus.BAD_REQUEST, "신고 사유가 올바르지 않습니다.", "COMMUNITY-009"),
    COMMUNITY_REPORT_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 신고한 게시글입니다.", "COMMUNITY-010"),
    REPORT_TARGET_TYPE_INVALID(HttpStatus.BAD_REQUEST, "신고 대상 타입이 올바르지 않습니다.", "COMMUNITY-011"),
    ;

    private final HttpStatus httpStatus;
    private final String message;
    private final String code;
}
