package com.example.stagemate.domain.scheduleReport;

import com.example.stagemate.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ScheduleReportErrorCode implements ErrorCode {
    NOT_FOUND(HttpStatus.NOT_FOUND, "공식 스케줄 리포트 상세 정보를 찾을 수 없습니다.", "SCHEDULE-001");

    private final HttpStatus httpStatus;
    private final String message;
    private final String code;


}
