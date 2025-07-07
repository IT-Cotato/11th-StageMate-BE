package com.example.stagemate.controller;

import com.example.stagemate.dto.request.LoginRequestDTO;
import com.example.stagemate.dto.request.RegisterUserRequestDTO;
import com.example.stagemate.global.dto.DataResponse;
import com.example.stagemate.global.security.session.SessionManager;
import com.example.stagemate.service.user.LoginUseCase;
import com.example.stagemate.service.user.RegisterUserUseCase;
import com.example.stagemate.service.user.command.LoginCommand;
import com.example.stagemate.service.user.command.RegisterUserCommand;
import io.swagger.v3.oas.annotations.tags.Tag;
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

    @PostMapping("/sign-up")
    ResponseEntity<DataResponse<Void>> signUp(final @Valid @RequestBody RegisterUserRequestDTO request) {
        RegisterUserCommand command = RegisterUserCommand.from(request);
        registerUserUseCase.signUp(command);
        return ResponseEntity.ok(DataResponse.ok());
    }


    @PostMapping("/login")
    ResponseEntity<DataResponse<Long>> login(final @Valid @RequestBody LoginRequestDTO request) {
        LoginCommand command = LoginCommand.from(request);
        long userId =  loginUseCase.login(command);

        sessionManager.login(command.userId());
        return ResponseEntity.ok(DataResponse.from(userId));
    }


    @PostMapping("/logout")
    ResponseEntity<DataResponse<Void>> logout() {
        sessionManager.logout();
        return ResponseEntity.ok(DataResponse.ok());
    }


}