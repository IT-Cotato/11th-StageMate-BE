package com.example.stagemate.service.user;

import com.example.stagemate.domain.user.port.out.LoadUserPort;
import com.example.stagemate.global.auth.mail.CustomMailSender;
import com.example.stagemate.global.exception.AppException;
import com.example.stagemate.global.exception.auth.AuthErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final CustomMailSender mailSender;
    private final LoadUserPort loadUserPort;

    public boolean checkEmailExists(String email) {
        return loadUserPort.existsByEmail(email);
    }

    public void sendVerificationCode(String email, String code) {
        String subject = "[StageMate] 이메일 인증번호입니다";
        String body = "인증번호: " + code + "\n5분 이내에 입력해주세요.";

        try {
            mailSender.send(email, subject, body);
        } catch (Exception e) {
            throw new AppException(AuthErrorCode.EMAIL_SEND_FAILED);
        }
    }
}
