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
            @Valid @RequestBody OAuth2SignupRequestDTO request,
            HttpSession session
    ) {
        GuestInfo guestInfo = (GuestInfo) session.getAttribute("guestInfo");
        if (guestInfo == null) {
            throw new AppException(CommonErrorCode.SESSION_EXPIRED, "세션 정보가 유효하지 않습니다.");
        }

        userService.oauthSignupInfo(request, guestInfo);

        // 회원가입 성공 후 세션 무효화
        session.invalidate();

        return ResponseEntity.ok().build();
    }
}
