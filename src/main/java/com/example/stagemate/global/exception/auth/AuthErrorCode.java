package com.example.stagemate.global.exception.auth;

import com.example.stagemate.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;


@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {

    TERMS_NOT_AGREED(HttpStatus.FORBIDDEN, "필수 약관에 동의하지 않았습니다.", "AUTH-001"),
    INVALID_ID_FORMAT(HttpStatus.BAD_REQUEST, "아이디 형식이 올바르지 않습니다.", "AUTH-002"),
    INVALID_PASSWORD_FORMAT(HttpStatus.BAD_REQUEST, "비밀번호 형식이 올바르지 않습니다.", "AUTH-003"),

    // RefreshToken 관련 오류들
    TOKEN_MISSING(HttpStatus.FORBIDDEN, "유효하지 않은 토큰입니다.", "AUTH-004"),
    TOKEN_NOT_FOUND(HttpStatus.FORBIDDEN, "유효하지 않은 토큰입니다.", "AUTH-005"),
    TOKEN_MISMATCH(HttpStatus.FORBIDDEN, "유효하지 않은 토큰입니다.", "AUTH-006"),
    TOKEN_EXPIRED(HttpStatus.FORBIDDEN, "유효하지 않은 토큰입니다.", "AUTH-007"),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다.", "AUTH-008");

    private final HttpStatus httpStatus;
    private final String message;
    private final String code;
}
