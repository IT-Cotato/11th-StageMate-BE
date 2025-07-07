package com.example.stagemate.global.auth;

import com.example.stagemate.domain.user.Role;
import com.example.stagemate.dto.auth.GuestInfo;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        boolean isGuest = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(Role.GUEST.getKey()));

        if (isGuest) {
            String name = oAuth2User.getAttribute("name");
            String email = oAuth2User.getAttribute("email");
            String picture = oAuth2User.getAttribute("picture");

            GuestInfo guestInfo = new GuestInfo(name, email, picture);

            HttpSession session = request.getSession();
            session.setAttribute("guestInfo", guestInfo);

            // 추가 정보 입력 페이지로 리다이렉트
            getRedirectStrategy().sendRedirect(request, response, "/oauth/register");
        } else {
            // 기존 회원은 메인 페이지로 리다이렉트
            getRedirectStrategy().sendRedirect(request, response, "/");
        }
    }
}
