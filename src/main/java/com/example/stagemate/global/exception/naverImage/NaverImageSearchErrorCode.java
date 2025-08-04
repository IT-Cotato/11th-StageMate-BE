package com.example.stagemate.global.exception.naverImage;

import com.example.stagemate.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
@Slf4j
public enum NaverImageSearchErrorCode implements ErrorCode {
    SE01(HttpStatus.BAD_REQUEST, "잘못된 쿼리요청입니다.", "NAVER-IMG-SE01"),
    SE02(HttpStatus.BAD_REQUEST, "부적절한 display 값입니다.", "NAVER-IMG-SE02"),
    SE03(HttpStatus.BAD_REQUEST, "부적절한 start 값입니다.", "NAVER-IMG-SE03"),
    SE04(HttpStatus.BAD_REQUEST, "부적절한 sort 값입니다.", "NAVER-IMG-SE04"),
    SE06(HttpStatus.BAD_REQUEST, "잘못된 형식의 인코딩입니다.", "NAVER-IMG-SE06"),
    SE05(HttpStatus.NOT_FOUND, "존재하지 않는 검색 api 입니다.", "NAVER-IMG-SE05"),
    SE98(HttpStatus.INTERNAL_SERVER_ERROR, "응답 파싱 중 오류가 발생했습니다.", "NAVER-IMG-SE98"),
    SE99(HttpStatus.INTERNAL_SERVER_ERROR, "시스템 에러입니다.", "NAVER-IMG-SE99");

    private final HttpStatus httpStatus;
    private final String message;
    private final String code;

    public static NaverImageSearchErrorCode from(String code) {
        if (code == null) {
            return SE99;
        }
        try {
            String trimmed = code.trim();
            for (NaverImageSearchErrorCode value : values()) {
                if (value.name().equalsIgnoreCase(trimmed)) {
                    return value;
                }
            }
            return SE99;
        } catch (Exception e) {
            return SE98;
        }
    }





}
