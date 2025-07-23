package com.example.stagemate.global.exception.magazine;

import com.example.stagemate.global.exception.AppException;
import com.example.stagemate.global.exception.ErrorCode;

public class MagazineNotFoundException extends AppException {
    public MagazineNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
