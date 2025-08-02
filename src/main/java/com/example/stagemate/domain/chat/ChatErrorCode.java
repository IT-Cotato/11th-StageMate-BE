package com.example.stagemate.domain.chat;

import com.example.stagemate.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ChatErrorCode implements ErrorCode {
    //not found
    CHAT_NOT_FOUND(HttpStatus.NOT_FOUND, "CHAT-001", "채팅을 찾을 수 없습니다."),
    //CHAT_REPORT_ALREADY_EXISTS
    CHAT_REPORT_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "CHAT-002", "이미 신고한 채팅입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
