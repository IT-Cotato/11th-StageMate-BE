package com.example.stagemate.global.exception;

import com.example.stagemate.global.dto.ErrorResponse;
import com.example.stagemate.global.exception.archive.ArchiveErrorCode;
import com.example.stagemate.global.exception.auth.AuthErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.example.stagemate.global.exception.CommonErrorCode.INTERNAL_SERVER_ERROR;

@Slf4j
@RestControllerAdvice
public class ExceptionController {

    // 처리되지 않은 모든 예외를 잡는 핸들러
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllException(Exception e, HttpServletRequest request) {
        log.error("처리되지 않은 예외 발생: ", e);
        log.error("에러가 발생한 지점 {}, {}", request.getMethod(), request.getRequestURI());
        ErrorResponse errorResponse = ErrorResponse.of(
                INTERNAL_SERVER_ERROR,
                request
        );
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse> handleAppCustomException(AppException e, HttpServletRequest request) {
        log.error("AppException 발생: {}", e.getErrorCode().getMessage());
        log.error("에러가 발생한 지점 {}, {}", request.getMethod(), request.getRequestURI());
        ErrorResponse errorResponse = ErrorResponse.of(e.getErrorCode(), request);
        return ResponseEntity
                .status(e.getErrorCode().getHttpStatus())
                .body(errorResponse);
    }

    //spring validation 예외
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        log.error("MethodArgumentNotValidException 발생: {}", e.getMessage());
        log.error("에러가 발생한 지점 {}, {}", request.getMethod(), request.getRequestURI());

        // 필드 에러가 있는 경우
        if (e.getBindingResult().hasFieldErrors()) {
            FieldError fieldError = e.getBindingResult().getFieldError();
            String fieldName = fieldError != null ? fieldError.getField() : "";
            String errorMessage = fieldError != null && fieldError.getDefaultMessage() != null 
                ? fieldError.getDefaultMessage() 
                : "유효하지 않은 요청입니다.";

            // 아이디 또는 비밀번호 필드 에러인 경우
            if ("userId".equals(fieldName)) {
                AuthErrorCode errorCode = AuthErrorCode.INVALID_ID_FORMAT;
                return ResponseEntity
                    .status(errorCode.getHttpStatus())
                    .body(ErrorResponse.of(errorCode, request));
            } else if ("password".equals(fieldName)) {
                AuthErrorCode errorCode = AuthErrorCode.INVALID_PASSWORD_FORMAT;
                return ResponseEntity
                    .status(errorCode.getHttpStatus())
                    .body(ErrorResponse.of(errorCode, request));
            } else if ("rating".equals(fieldName)) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ErrorResponse.of(ArchiveErrorCode.INVALID_RATING, request));
            }
        }

        // 기타 필드 에러
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(CommonErrorCode.BAD_REQUEST, request));
    }
}