package com.example.stagemate.controller.auth;

import com.example.stagemate.domain.user.User;
import com.example.stagemate.dto.auth.GuestInfo;
import com.example.stagemate.dto.request.OAuth2SignupRequestDTO;
import com.example.stagemate.dto.request.ConsentRequestDTO;
import com.example.stagemate.global.auth.dto.SessionUser;
import com.example.stagemate.global.dto.DataResponse;
import com.example.stagemate.global.security.session.SessionManager;
import com.example.stagemate.service.user.LoginUseCase;
import com.example.stagemate.service.user.UserService;
import com.example.stagemate.service.user.command.LoginCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
    @PostMapping("/signup")
    public ResponseEntity<Void> oAuthSignup(@RequestBody @Valid OAuth2SignupRequestDTO request) {
        userService.oauthSignupInfo(request, request.getGuestInfo());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "소셜 회원가입 - 약관 동의 및 최종 가입", description = "약관 동의를 받아 최종적으로 회원가입을 완료하고 로그인 처리합니다.")
    @PostMapping("/agree")
    public ResponseEntity<DataResponse<String>> oAuthAgree(@RequestBody @Valid ConsentRequestDTO request) {
        User completedUser = userService.oauthAgreeAndRegister(request, request.getGuestInfo());

        String token = loginUseCase.login(LoginCommand.fromUser(completedUser));
        return ResponseEntity.ok(DataResponse.from(token));
    }
}