package com.example.stagemate.global.auth.mail;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class EmailVerificationCodeGenerator {
    public String generateCode() {
        return String.format("%06d", new SecureRandom().nextInt(999999));
    }
}
