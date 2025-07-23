package com.example.stagemate.controller.auth;

import com.example.stagemate.dto.request.OAuth2SignupRequestDTO;
import com.example.stagemate.global.exception.AppException;
import com.example.stagemate.global.exception.auth.AuthErrorCode;
import com.example.stagemate.service.user.LoginUseCase;
import com.example.stagemate.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/oauth")
@RequiredArgsConstructor
@Tag(name = "OAuth", description = "소셜 로그인 관련 API")
public class OAuthController {

    private final UserService userService;
    private final LoginUseCase loginUseCase;


    @Operation(summary = "소셜 회원가입 - 정보 입력", description = "소셜 로그인 후, 닉네임, 생년월일 등 추가 정보를 입력받습니다.")
    @PostMapping("/sign-up")
    public ResponseEntity<Void> oAuthSignup(@RequestBody @Valid OAuth2SignupRequestDTO request, HttpServletRequest httpRequest) {

        //닉네임 중복 여부 검사 확인
        String verifiedNickname = (String) httpRequest.getSession().getAttribute("verified_nickname");

        if (verifiedNickname == null || !verifiedNickname.equals(request.getNickname())) {
            throw new AppException(AuthErrorCode.NICKNAME_NOT_VERIFIED);
        }

        //user 정보 처리
        userService.oauthSignupInfo(request, request.getGuestInfo());

        //세션에 SecurityContext 저장
        SecurityContext context = SecurityContextHolder.getContext();
        httpRequest.getSession().setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                context
        );

        return ResponseEntity.ok().build();


    }

}