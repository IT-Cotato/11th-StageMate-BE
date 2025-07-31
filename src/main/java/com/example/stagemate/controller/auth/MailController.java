package com.example.stagemate.controller.auth;

import com.example.stagemate.dto.request.EmailVerificationRequestDTO;
import com.example.stagemate.dto.request.EmailVerificationVerifyDTO;
import com.example.stagemate.global.exception.AppException;
import com.example.stagemate.global.exception.CommonErrorCode;
import com.example.stagemate.service.user.EmailVerificationService;
import com.example.stagemate.service.user.MailService;
import com.example.stagemate.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSender;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/email")
@RequiredArgsConstructor
public class MailController {

    private final EmailVerificationService emailVerificationService;
    private final MailService mailService;

    @PostMapping("/send-code")
    public ResponseEntity<?> sendCode(@Valid @RequestBody EmailVerificationRequestDTO request) {

        // 이메일 중복 체크
        if (mailService.checkEmailExists(request.getEmail())) {
            throw new AppException(CommonErrorCode.RESOURCE_CONFLICT, "이미 가입된 이메일입니다.");
        }

        // 인증 코드 전송
        emailVerificationService.sendCode(request.getEmail());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@RequestBody EmailVerificationVerifyDTO request) {
        emailVerificationService.verifyCode(request.getEmail(), request.getCode());
        return ResponseEntity.ok().build();
    }
}
