package com.example.stagemate.global.exception.magazine;

import com.example.stagemate.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MagazineErrorCode implements ErrorCode {
    // 매거진 카테고리 없음
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "매거진 카테고리를 찾을 수 없습니다.", "MAGAZINE-001"),
    // 매거진 없음
    MAGAZINE_NOT_FOUND(HttpStatus.NOT_FOUND, "매거진을 찾을 수 없습니다.", "MAGAZINE-002"),
    ;

    private final HttpStatus httpStatus;
    private final String message;
    private final String code;
}
