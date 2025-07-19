package com.example.stagemate.domain.theater;

import com.example.stagemate.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TheaterErrorCode implements ErrorCode {
    THEATER_NOT_FOUND(HttpStatus.NOT_FOUND, "영화장 정보를 찾을 수 없습니다.", "THEATER-001");

    private final HttpStatus httpStatus;
    private final String message;
    private final String code;

}
