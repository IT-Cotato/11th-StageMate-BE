package com.example.stagemate.global.exception.archive;

import com.example.stagemate.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ArchiveErrorCode implements ErrorCode {
    NOT_FOUND(HttpStatus.NOT_FOUND, "아카이브 상세 정보를 찾을 수 없습니다.", "ARCHIVE-001");

    private final HttpStatus httpStatus;
    private final String message;
    private final String code;


}
