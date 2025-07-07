package com.example.stagemate.global.util;

public final class ValidationConstants {

    private ValidationConstants() {
    }

    // User ID Validation
    public static final int USER_ID_MIN_LENGTH = 4;
    public static final int USER_ID_MAX_LENGTH = 20;
    public static final String USER_ID_LENGTH_MESSAGE = "아이디는 4자 이상 20자 이하로 입력해주세요.";
    public static final String USER_ID_PATTERN_MESSAGE = "아이디는 영문 대소문자와 숫자로만 가능합니다.";
    public static final String USER_ID_REGEX = "^[a-zA-Z0-9]{4,20}$";


    // Password Validation
    public static final int PASSWORD_MIN_LENGTH = 8;
    public static final int PASSWORD_MAX_LENGTH = 20;
    public static final String PASSWORD_LENGTH_MESSAGE = "비밀번호는 8자 이상 20자 이하로 입력해주세요.";
    public static final String PASSWORD_PATTERN_MESSAGE = "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다.";
    public static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,20}$";
}