package com.example.stagemate.global.auth;

import com.example.stagemate.domain.user.User;
import com.example.stagemate.domain.user.port.out.LoadUserPort;
import com.example.stagemate.domain.user.port.out.SaveUserPort;
import com.example.stagemate.global.auth.dto.OAuthAttributes;
import com.example.stagemate.global.auth.dto.SessionUser;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final LoadUserPort loadUserPort;
    private final SaveUserPort saveUserPort;
    private final HttpSession httpSession;


    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("▶️ OAuth2 로그인 시작");

        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);
        log.info("✅ 구글로부터 받은 사용자 정보: {}", oAuth2User.getAttributes());

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());
        User user = saveOrUpdate(attributes);

        if (user == null) {
            throw new IllegalStateException("❌ 저장된 유저가 null입니다.");
        }

        httpSession.setAttribute("user", new SessionUser(user));

        // ✅ 기존 DefaultOAuth2User → CustomUserDetails로 교체
        return new CustomUserDetails(user, attributes.getAttributes());
    }



    private User saveOrUpdate(OAuthAttributes attributes) {
        log.info("🛠️ saveOrUpdate 진입: email={}", attributes.getEmail());
        return loadUserPort.findByEmail(attributes.getEmail())
                .map(user -> saveUserPort.save(user.update(attributes.getUserId(), attributes.getPicture())))
                .orElseGet(() -> {
                    log.info("🆕 새로운 유저 생성: email={}", attributes.getEmail());
                    String newUserId = generateUniqueUserIdFromEmail(attributes.getEmail()); //수정된 방식
                    User newUser = User.googleGuestSignUp(
                            newUserId,
                            attributes.getUserId(),
                            attributes.getEmail(),
                            attributes.getPicture()
                    );
                    log.info("✅ 생성된 Guest 유저: {}", newUser);
                    return saveUserPort.save(newUser);
                });
    }


    private String generateUniqueRandomUserId() {
        String userId;
        do {
            userId = generateRandomUserId();
        } while (loadUserPort.existsByUserId(userId));
        return userId;
    }

    private String generateRandomUserId() {
        int length = 12;
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        java.security.SecureRandom random = new java.security.SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    //구글 계정 이메일 주소에서 UserId 추출
    private String generateUniqueUserIdFromEmail(String email) {
        String baseUserId = email.split("@")[0];
        String finalUserId = baseUserId;
        int suffix = 1;

        while (loadUserPort.existsByUserId(finalUserId)) {
            finalUserId = baseUserId + suffix;
            suffix++;
        }

        return finalUserId;
    }
}