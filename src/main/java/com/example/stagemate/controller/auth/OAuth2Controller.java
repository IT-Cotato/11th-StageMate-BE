package com.example.stagemate.controller.auth;

import com.example.stagemate.dto.auth.GuestInfo;
import com.example.stagemate.dto.request.OAuth2RegisterRequestDTO;
import com.example.stagemate.dto.request.OAuth2SignupRequestDTO;
import com.example.stagemate.global.exception.AppException;
import com.example.stagemate.global.exception.CommonErrorCode;
import com.example.stagemate.service.user.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor
public class OAuth2Controller {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<Void> oauthSignUp(
            @Valid @RequestBody OAuth2SignupRequestDTO request
    ) {
        // 프론트에서 guestInfo 정보 포함해서 요청 보내야 함
        userService.oauthSignupInfo(request, request.getGuestInfo());
        return ResponseEntity.ok().build();
    }
}
