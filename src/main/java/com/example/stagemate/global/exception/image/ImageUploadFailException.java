package com.example.stagemate.global.exception.image;

import com.example.stagemate.global.exception.AppException;
import com.example.stagemate.global.exception.ErrorCode;

public class ImageUploadFailException extends AppException {
    public ImageUploadFailException(ErrorCode errorCode) {
        super(errorCode);
    }
}
