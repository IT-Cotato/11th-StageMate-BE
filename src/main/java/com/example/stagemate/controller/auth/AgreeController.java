package com.example.stagemate.controller.auth;

import com.example.stagemate.domain.user.User;
import com.example.stagemate.domain.user.entity.RefreshTokenEntity;
import com.example.stagemate.domain.user.model.ConsentType;
import com.example.stagemate.dto.request.ConsentRequestDTO;
import com.example.stagemate.dto.response.TokenResponseDTO;
import com.example.stagemate.global.auth.JwtTokenProvider;
import com.example.stagemate.global.dto.DataResponse;
import com.example.stagemate.global.exception.AppException;
import com.example.stagemate.global.exception.CommonErrorCode;
import com.example.stagemate.global.exception.auth.AuthErrorCode;
import com.example.stagemate.repository.user.RefreshTokenRepository;
import com.example.stagemate.service.user.LoginUseCase;
import com.example.stagemate.service.user.UserService;
import com.example.stagemate.service.user.command.NormalAgreeCommand;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AgreeController {

    private final UserService userService;
    private final LoginUseCase loginUseCase;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @PostMapping("/agree")
    public ResponseEntity<DataResponse<TokenResponseDTO>> agreeToTerms(
            @RequestBody @Valid ConsentRequestDTO request,
            HttpServletRequest httpRequest
    ) {
        if (request.getUserId() == null || request.getUserId().isBlank()) {
            throw new AppException(CommonErrorCode.BAD_REQUEST, "userId는 필수입니다.");
        }
        Map<String, Boolean> consents = request.getConsents();

        if (!Boolean.TRUE.equals(consents.get("SERVICE_TERMS")) ||
                !Boolean.TRUE.equals(consents.get("PRIVACY_POLICY"))) {
            throw new AppException(AuthErrorCode.TERMS_NOT_AGREED);
        }

        Map<ConsentType, Boolean> enumConsents = consents.entrySet().stream()
                .filter(e -> e.getKey() != null)
                .collect(Collectors.toMap(
                        e -> ConsentType.valueOf(e.getKey()),
                        Map.Entry::getValue
                ));

        // 동의 처리 및 유저 최종 등록
        User completedUser = userService.normalAgreeAndRegister(
                new NormalAgreeCommand(enumConsents),
                request.getUserId()
        );

        // 최종 Role 업데이트 후 JWT 발급
        String accessToken = jwtTokenProvider.createToken(completedUser.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(completedUser.getId());

        // DB에 저장
        refreshTokenRepository.save(
                RefreshTokenEntity.builder()
                        .userId(completedUser.getId())
                        .token(refreshToken)
                        .expiresAt(LocalDateTime.now().plusDays(14))
                        .build()
        );

        TokenResponseDTO response = new TokenResponseDTO(accessToken,refreshToken);
        return ResponseEntity.ok(DataResponse.from(response));
    }

}
