package com.example.stagemate.global.exception.magazine;

import com.example.stagemate.global.exception.AppException;
import com.example.stagemate.global.exception.ErrorCode;

public class MagazineCategoryNotFoundException extends AppException {
    public MagazineCategoryNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
