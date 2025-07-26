package com.example.stagemate.controller.auth;

import com.example.stagemate.domain.user.User;
import com.example.stagemate.domain.user.entity.RefreshTokenEntity;
import com.example.stagemate.domain.user.model.ConsentType;
import com.example.stagemate.dto.request.OAuth2SignupRequestDTO;
import com.example.stagemate.dto.response.TokenResponseDTO;
import com.example.stagemate.global.auth.JwtTokenProvider;
import com.example.stagemate.global.exception.AppException;
import com.example.stagemate.global.exception.CommonErrorCode;
import com.example.stagemate.global.exception.auth.AuthErrorCode;
import com.example.stagemate.global.util.SignUpConsentTempStore;
import com.example.stagemate.repository.user.RefreshTokenRepository;
import com.example.stagemate.service.user.ConsentService;
import com.example.stagemate.service.user.UserService;
import com.example.stagemate.dto.auth.GuestInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/api/v1/oauth")
@RequiredArgsConstructor
@Tag(name = "OAuth", description = "소셜 로그인 관련 API")
public class OAuthController {

    private final UserService userService;
    private final SignUpConsentTempStore signUpConsentTempStore;
    private final ConsentService consentService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;


    @Operation(summary = "소셜 회원가입 - 정보 입력", description = "소셜 로그인 후, 닉네임, 생년월일 등 추가 정보를 입력받습니다.")
    @PostMapping("/sign-up")
    public ResponseEntity<TokenResponseDTO> oAuthSignup(@RequestBody @Valid OAuth2SignupRequestDTO request, HttpServletRequest httpRequest) {

        //닉네임 중복 여부 검사 확인
        String verifiedNickname = (String) httpRequest.getSession().getAttribute("verified_nickname");

        if (verifiedNickname == null || !verifiedNickname.equals(request.getNickname())) {
            throw new AppException(AuthErrorCode.NICKNAME_NOT_VERIFIED);
        }

        // 세션 기반 userId
        GuestInfo guestInfo = (GuestInfo) httpRequest.getSession().getAttribute("guestInfo");
        if (guestInfo == null || guestInfo.userId() == null) {
            throw new AppException(CommonErrorCode.BAD_REQUEST, "세션에서 guestInfo 정보를 찾을 수 없습니다.");
        }

        //동의 정보 조회
        Map<String, Boolean> consentMap = signUpConsentTempStore.getForOAuth(guestInfo.userId());
        Map<ConsentType, Boolean> enumConsents = consentMap.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> ConsentType.valueOf(e.getKey()),
                        Map.Entry::getValue
                ));


        // 회원 등록
        User user = userService.oauthSignupInfo(request, request.getGuestInfo());

        // 동의 저장
        consentService.saveAll(user, enumConsents);

        // Redis 삭제
        signUpConsentTempStore.deleteForOAuth(request.getGuestInfo().userId());

        // JWT 발급
        String accessToken = jwtTokenProvider.createToken(user.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());

        refreshTokenRepository.save(
                RefreshTokenEntity.builder()
                        .userId(user.getId())
                        .token(refreshToken)
                        .expiresAt(LocalDateTime.now().plusDays(14))
                        .build()
        );

        return ResponseEntity.ok(new TokenResponseDTO(accessToken, refreshToken));

    }

}