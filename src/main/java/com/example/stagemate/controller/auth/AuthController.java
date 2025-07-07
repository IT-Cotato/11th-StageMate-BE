package com.example.stagemate.controller.auth;

import com.example.stagemate.domain.user.User;
import com.example.stagemate.dto.request.ConsentRequestDTO;
import com.example.stagemate.dto.request.LoginRequestDTO;
import com.example.stagemate.dto.request.RegisterUserRequestDTO;
import com.example.stagemate.global.dto.DataResponse;
import com.example.stagemate.global.exception.AppException;
import com.example.stagemate.global.exception.CommonErrorCode;
import com.example.stagemate.global.security.session.SessionManager;
import com.example.stagemate.service.user.LoginUseCase;
import com.example.stagemate.service.user.RegisterUserUseCase;
import com.example.stagemate.service.user.command.LoginCommand;
import com.example.stagemate.service.user.command.NormalAgreeCommand;
import com.example.stagemate.service.user.command.RegisterUserCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth", description = "회원가입 및 로그인 API")
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final RegisterUserUseCase registerUserUseCase;
    private final SessionManager sessionManager;
    private final HttpSession httpSession;

    private static final String NORMAL_SIGNUP_USER_ID = "NORMAL_SIGNUP_USER_ID";

    @Operation(summary = "일반 회원가입 - 정보 입력", description = "아이디, 비밀번호 등 기본 정보를 입력받아 임시 저장합니다.")
    @PostMapping("/sign-up/info")
    public ResponseEntity<DataResponse<Void>> signUpInfo(@Valid @RequestBody RegisterUserRequestDTO request) {
        RegisterUserCommand command = RegisterUserCommand.from(request);
        registerUserUseCase.normalSignupInfo(command);
        httpSession.setAttribute(NORMAL_SIGNUP_USER_ID, command.userId());
        return ResponseEntity.ok(DataResponse.ok());
    }

    @Operation(summary = "일반 회원가입 - 약관 동의 및 최종 가입", description = "약관 동의를 받아 최종적으로 회원가입을 완료하고 로그인 처리합니다.")
    @PostMapping("/sign-up/agree")
    public ResponseEntity<DataResponse<Void>> signUpAgree(@Valid @RequestBody ConsentRequestDTO request) {
        String userId = (String) httpSession.getAttribute(NORMAL_SIGNUP_USER_ID);
        if (userId == null) {
            throw new AppException(CommonErrorCode.SESSION_EXPIRED, "세션 정보가 유효하지 않습니다.");
        }

        NormalAgreeCommand command = NormalAgreeCommand.from(request);
        User completedUser = registerUserUseCase.normalAgreeAndRegister(command, userId);

        sessionManager.login(completedUser.getUserId());
        httpSession.removeAttribute(NORMAL_SIGNUP_USER_ID);

        return ResponseEntity.ok(DataResponse.ok());
    }

    @PostMapping("/login")
    ResponseEntity<DataResponse<String>> login(final @Valid @RequestBody LoginRequestDTO request) {
        LoginCommand command = LoginCommand.from(request);
        String token = loginUseCase.login(command);

        sessionManager.login(command.userId());
        return ResponseEntity.ok(DataResponse.from(token));
    }


    @PostMapping("/logout")
    ResponseEntity<DataResponse<Void>> logout() {
        sessionManager.logout();
        return ResponseEntity.ok(DataResponse.ok());
    }
}