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
    PASSWORD_CONFIRM_NOT_MATCH(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다.", "AUTH-009"),
    INVALID_CODE(HttpStatus.UNAUTHORIZED, "유효하지 않은 인증번호 입니다.", "AUTH-010"),
    INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST,"이메일 형식이 올바르지 않습니다.", "AUTH-011"),
    EMAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR,"이메일 전송에 실패했습니다.", "AUTH-012"),
    EMAIL_NOT_VERIFIED(HttpStatus.UNAUTHORIZED, "이메일 인증이 완료되지 않았습니다.", "AUTH-013"),
    NICKNAME_NOT_VERIFIED(HttpStatus.UNAUTHORIZED, "닉네임 중복 검사가 완료되지 않았습니다.", "AUTH-014"),
    USERID_NOT_VERIFIED(HttpStatus.UNAUTHORIZED, "유저 아이디 중복 검사가 완료되지 않았습니다.", "AUTH-015"),
    DUPLICATE_USER_ID(HttpStatus.CONFLICT, "이미 사용 중인 유저 ID입니다.", "AUTH-016"),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다.", "AUTH-017"),




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
