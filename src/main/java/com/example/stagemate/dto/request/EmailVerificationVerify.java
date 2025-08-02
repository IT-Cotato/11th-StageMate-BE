package com.example.stagemate.dto.request;

import lombok.Getter;

@Getter
public class EmailVerificationVerify {
    private String email;
    private String code;
}
