package com.example.stagemate.controller;

import com.example.stagemate.domain.user.User;
import com.example.stagemate.domain.user.entity.UserJpaEntity;
import com.example.stagemate.dto.auth.GuestInfo;
import com.example.stagemate.global.auth.CustomUserDetails;
import com.example.stagemate.repository.user.UserJpaRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dev-login")
@Profile("local")
@RequiredArgsConstructor
public class DevLoginController {
    private final UserJpaRepository userJpaRepository;

    @GetMapping
    public String loginForDev(HttpServletRequest request) {
        // 1. 고정 테스트 사용자 정보
        String email = "3f9Tm@example.com";

        // 2. 이미 존재하는 사용자 조회 or 새로 생성
        UserJpaEntity userJpaEntity = userJpaRepository.findByEmail(email)
                .orElseGet(() -> {
                    User fakeUser = User.googleGuestSignUp(
                            "fake-user-id",
                            "Test User",
                            email,
                            "https://google.com"
                    );
                    return userJpaRepository.save(UserJpaEntity.from(fakeUser));
                });

        // 3. 인증 등록을 위한 User 객체 생성
        User domainUser = userJpaEntity.toDomain();

        // GuestInfo 임시 생성 후 세션에 저장
        GuestInfo guestInfo = new GuestInfo(domainUser.getEmail(), domainUser.getName(), domainUser.getPicture());
        request.getSession().setAttribute("guestInfo", guestInfo);

        // 4. UserDetails → Authentication 생성
        CustomUserDetails customUserDetails = new CustomUserDetails(domainUser, Map.of());
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        customUserDetails,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                );

        // 5. 세션 강제 생성
        request.getSession(true);

        // 6. SecurityContextHolder에 등록
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 7. HttpSession에 인증정보 저장
        new HttpSessionSecurityContextRepository().saveContext(
                SecurityContextHolder.getContext(), request, null);

        return "redirect:/test6"; // 테스트 페이지
    }
}
