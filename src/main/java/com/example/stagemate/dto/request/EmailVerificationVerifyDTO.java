package com.example.stagemate.dto.request;

import lombok.Getter;

@Getter
public class EmailVerificationVerifyDTO {
    private String email;
    private String code;
}
