package com.example.stagemate.controller.auth;

import com.example.stagemate.dto.request.EmailVerificationRequestDTO;
import com.example.stagemate.dto.request.EmailVerificationVerifyDTO;
import com.example.stagemate.service.user.EmailVerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/email")
@RequiredArgsConstructor
public class MailController {

    private final EmailVerificationService emailVerificationService;

    @PostMapping("/send-code")
    public ResponseEntity<?> sendCode(@Valid @RequestBody EmailVerificationRequestDTO request) {
        emailVerificationService.sendCode(request.getEmail());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@RequestBody EmailVerificationVerifyDTO request) {
        emailVerificationService.verifyCode(request.getEmail(), request.getCode());
        return ResponseEntity.ok().build();
    }
}
