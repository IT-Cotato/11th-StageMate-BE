package com.example.stagemate.controller.auth;

import com.example.stagemate.domain.user.User;
import com.example.stagemate.dto.request.LoginRequestDTO;
import com.example.stagemate.dto.request.RegisterUserRequestDTO;
import com.example.stagemate.global.auth.CustomUserDetails;
import com.example.stagemate.global.dto.DataResponse;
import com.example.stagemate.service.user.LoginUseCase;
import com.example.stagemate.service.user.RegisterUserUseCase;
import com.example.stagemate.service.user.command.LoginCommand;
import com.example.stagemate.service.user.command.RegisterUserCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Collections;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth", description = "회원가입 및 로그인 API")
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final RegisterUserUseCase registerUserUseCase;


    @Operation(summary = "일반 회원가입 - 정보 입력", description = "아이디, 비밀번호 등 기본 정보를 입력받아 임시 저장합니다.")
    @PostMapping("/sign-up/info")
    public ResponseEntity<DataResponse<String>> signUpInfo(@Valid @RequestBody RegisterUserRequestDTO request, HttpServletRequest httpRequest) {
        RegisterUserCommand command = RegisterUserCommand.from(request);
        //회원가입 처리
        String userId = registerUserUseCase.normalSignupInfo(command);

        // 세션 인증용 CustomUserDetails 생성
        User user = User.normalGuestSignUp(
                request.userId(),
                request.email(),
                request.password(),
                request.name(),
                request.nickname(),
                request.birthdate()
        );

        CustomUserDetails userDetails = new CustomUserDetails(user, Collections.emptyMap());

        // Authentication 객체 생성
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );

        // SecurityContext 생성 및 세션 저장
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);

        httpRequest.getSession().setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                context
        );

        return ResponseEntity.ok(DataResponse.from(userId));
    }

    @PostMapping("/login")
    ResponseEntity<DataResponse<String>> login(final @Valid @RequestBody LoginRequestDTO request) {
        LoginCommand command = LoginCommand.from(request);
        String token = loginUseCase.login(command);

        return ResponseEntity.ok(DataResponse.from(token));
    }


    @PostMapping("/logout")
    ResponseEntity<DataResponse<Void>> logout() {
        return ResponseEntity.ok(DataResponse.ok());
    }
}