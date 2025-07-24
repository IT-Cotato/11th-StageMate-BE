package com.example.stagemate.global.auth;

import com.example.stagemate.domain.user.Role;
import com.example.stagemate.domain.user.User;
import com.example.stagemate.dto.auth.GuestInfo;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        log.info("✅ OAuth2 Login 성공!");
        CustomUserDetails oAuth2User = (CustomUserDetails) authentication.getPrincipal();
        User user = oAuth2User.getUser();

        if (user.getRole() == Role.USER) {

            // 회원가입이 완료된 유저 → JWT 발급 + 프론트로 이동
            String accessToken = jwtTokenProvider.createToken(user.getId());

            String targetUrl = UriComponentsBuilder
                    .fromUriString("http://localhost:3000/login-success")
                    .queryParam("accessToken", accessToken)
                    .build()
                    .encode(StandardCharsets.UTF_8)
                    .toUriString();

            getRedirectStrategy().sendRedirect(request, response, targetUrl);
        } else {
            // 가입되지 않은 유저(GUEST) -> 세션에 GuestInfo 저장하고 약관 동의 페이지로 리다이렉트
            GuestInfo guestInfo = new GuestInfo(
                    user.getUserId(),
                    user.getName(),
                    user.getEmail(),
                    user.getPicture()
            );
            request.getSession().setAttribute("guestInfo", guestInfo);

            String targetUrl = "http://localhost:3000/oauth-agree"; // 프론트 약관 동의 페이지 주소
            getRedirectStrategy().sendRedirect(request, response, targetUrl);
        }
    }

}
