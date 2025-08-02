package com.example.stagemate.domain.community;

import com.example.stagemate.global.exception.AppException;
import static com.example.stagemate.global.exception.community.CommunityErrorCode.*;


public enum TargetType {
    POST,
    COMMENT;

    public static TargetType fromString(String targetTypeRaw) {
        try {
            return TargetType.valueOf(targetTypeRaw.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new AppException(REPORT_TARGET_TYPE_INVALID);
        }
    }
}
