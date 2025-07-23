package com.example.stagemate.service.user;

import com.example.stagemate.global.auth.mail.EmailVerificationCodeGenerator;
import com.example.stagemate.global.exception.AppException;
import com.example.stagemate.global.exception.auth.AuthErrorCode;
import com.example.stagemate.repository.user.EmailVerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final EmailVerificationCodeGenerator codeGenerator;
    private final EmailVerificationRepository repository;
    private final MailService mailService;

    public void sendCode(String email) {
        String code = codeGenerator.generateCode();
        repository.saveCode(email, code, Duration.ofMinutes(5));

        mailService.sendVerificationCode(email, code);

    }

    public void verifyCode(String email, String code) {
        if (!repository.verifyCode(email, code)) {
            throw new AppException(AuthErrorCode.INVALID_CODE);
        }
    }

    public boolean isVerified(String email) {
        return repository.isVerified(email);  // Redis에서 "email:verified:{email}" → true인지 확인
    }
}