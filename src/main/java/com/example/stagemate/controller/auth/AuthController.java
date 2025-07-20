package com.example.stagemate.controller.auth;

import com.example.stagemate.domain.user.User;
import com.example.stagemate.domain.user.entity.RefreshTokenEntity;
import com.example.stagemate.dto.request.LoginRequestDTO;
import com.example.stagemate.dto.request.RegisterUserRequestDTO;
import com.example.stagemate.dto.response.TokenResponseDTO;
import com.example.stagemate.global.auth.CustomUserDetails;
import com.example.stagemate.global.auth.JwtTokenProvider;
import com.example.stagemate.global.dto.DataResponse;
import com.example.stagemate.global.exception.AppException;
import com.example.stagemate.global.exception.auth.AuthErrorCode;
import com.example.stagemate.global.exception.CommonErrorCode;
import com.example.stagemate.repository.user.RefreshTokenRepository;
import com.example.stagemate.service.user.LoginUseCase;
import com.example.stagemate.service.user.RegisterUserUseCase;
import com.example.stagemate.service.user.UserService;
import com.example.stagemate.service.user.command.LoginCommand;
import com.example.stagemate.service.user.command.RegisterUserCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.regex.Pattern;

/**
 * AuthController
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth", description = "회원가입 및 로그인 API")
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final RegisterUserUseCase registerUserUseCase;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;

    @Operation(summary = "일반 회원가입 - 정보 입력", description = "아이디, 비밀번호 등 기본 정보를 입력받아 임시 저장합니다.")
    @PostMapping("/sign-up/info")
    public ResponseEntity<DataResponse<String>> signUpInfo(
            @Valid @RequestBody RegisterUserRequestDTO request,
            BindingResult bindingResult,
            HttpServletRequest httpRequest) {

        // DTO 검증 오류가 있는지 확인
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldErrors().stream()
                    .findFirst()
                    .map(org.springframework.validation.FieldError::getDefaultMessage)
                    .orElse("유효하지 않은 요청입니다.");

            // 아이디와 비밀번호 관련 오류인 경우 AUTH 에러 코드 사용
            if (bindingResult.hasFieldErrors("userId") || bindingResult.hasFieldErrors("password")) {
                if (bindingResult.getFieldError("userId") != null) {
                    throw new AppException(AuthErrorCode.INVALID_ID_FORMAT, errorMessage);
                } else if (bindingResult.getFieldError("password") != null) {
                    throw new AppException(AuthErrorCode.INVALID_PASSWORD_FORMAT, errorMessage);
                }
            }

            // 기타 필드 오류는 BAD_REQUEST로 처리
            throw new AppException(CommonErrorCode.BAD_REQUEST, errorMessage);
        }

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
    ResponseEntity<DataResponse<TokenResponseDTO>> login(final @Valid @RequestBody LoginRequestDTO request) {
        LoginCommand command = LoginCommand.from(request);
        TokenResponseDTO tokens = loginUseCase.login(command);

        return ResponseEntity.ok(DataResponse.from(tokens));
    }



    @PostMapping("/logout")
    @Transactional
    public ResponseEntity<DataResponse<Void>> logout(HttpServletRequest request) {
        //AccessToken 추출
        String token = jwtTokenProvider.extractToken(request);

        //AccessToken에서 userId 추출
        Long userId = jwtTokenProvider.getUserId(token);

        //RefreshToken 삭제
        refreshTokenRepository.deleteByUserId(userId);

        return ResponseEntity.ok(DataResponse.ok());
    }


    @Operation(summary = "AccessToken 재발급", description = "RefreshToken을 검증하여 새로운 AccessToken을 발급합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/reissue")
    public ResponseEntity<DataResponse<TokenResponseDTO>> reissue(HttpServletRequest request) {
        // 1. RefreshToken 추출
        String refreshToken = jwtTokenProvider.extractToken(request);
        if (refreshToken == null) {
            throw new AppException(AuthErrorCode.TOKEN_MISSING, "토큰이 존재하지 않습니다.");
        }

        // 2. userId 추출
        Long userId;
        try {
            userId = jwtTokenProvider.getUserId(refreshToken);
        } catch (Exception e) {
            throw new AppException(AuthErrorCode.TOKEN_INVALID, "유효하지 않은 토큰입니다.");
        }

        // 3. DB에서 저장된 RefreshToken과 비교
        RefreshTokenEntity savedToken = refreshTokenRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(AuthErrorCode.TOKEN_NOT_FOUND, "저장된 토큰을 찾을 수 없습니다."));

        // 4. 일치 여부 확인
        if (!savedToken.getToken().equals(refreshToken)) {
            throw new AppException(AuthErrorCode.TOKEN_MISMATCH, "토큰이 일치하지 않습니다.");
        }

        // 5. 만료 여부 확인
        if (savedToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.deleteByUserId(userId);
            throw new AppException(AuthErrorCode.TOKEN_EXPIRED, "토큰이 만료되었습니다. 다시 로그인해주세요.");
        }

        // 6. 유저 조회
        User user = userService.findUserById(userId);

        // 7. AccessToken 재발급
        String newAccessToken = jwtTokenProvider.createToken(user.getId());

        // 8. 응답 DTO 구성
        TokenResponseDTO response = new TokenResponseDTO(newAccessToken, refreshToken);

        return ResponseEntity.ok(DataResponse.from(response));
    }

}