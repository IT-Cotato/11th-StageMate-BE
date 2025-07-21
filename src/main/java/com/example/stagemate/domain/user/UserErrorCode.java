package com.example.stagemate.domain.user;

import com.example.stagemate.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {
    NOT_FOUND(HttpStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다.", "USER-001"),
    //권한이 없음
    NO_PERMISSION(HttpStatus.FORBIDDEN, "권한이 없음", "USER-002");

    private final HttpStatus httpStatus;
    private final String message;
    private final String code;
}
