package com.example.stagemate.global.exception.search;

import com.example.stagemate.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SearchErrorCode implements ErrorCode {
    REDIS_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Redis 작업 중 오류가 발생했습니다.", "SEARCH-001"),
    ELASTICSEARCH_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "엘라스틱서치 작업 중 오류가 발생했습니다.", "SEARCH-002"),
    ;

    private final HttpStatus httpStatus;
    private final String message;
    private final String code;
}
