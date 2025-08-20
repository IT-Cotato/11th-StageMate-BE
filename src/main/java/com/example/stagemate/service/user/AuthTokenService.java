package com.example.stagemate.service.user;


import com.example.stagemate.domain.user.entity.RefreshTokenEntity;
import com.example.stagemate.dto.response.TokenResponse;
import com.example.stagemate.global.auth.JwtTokenProvider;
import com.example.stagemate.repository.user.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class AuthTokenService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    public TokenResponse generateTokensAndSave(Long userId) {
        String accessToken = jwtTokenProvider.createToken(userId);
        String refreshToken = jwtTokenProvider.createRefreshToken(userId);

        refreshTokenRepository.save(
                RefreshTokenEntity.builder()
                        .userId(userId)
                        .token(refreshToken)
                        .expiresAt(LocalDateTime.now(ZoneId.of("Asia/Seoul")).plusDays(14))
                        .build()
        );

        return new TokenResponse(accessToken, refreshToken);
    }

}
