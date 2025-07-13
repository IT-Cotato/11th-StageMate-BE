package com.example.stagemate.global.auth;

import com.example.stagemate.domain.user.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(
                        (csrfConfig) -> csrfConfig.disable()
                )
                .headers(
                        (headerConfig) -> headerConfig.frameOptions(
                                frameOptionsConfig -> frameOptionsConfig.disable()
                        )
                )
                .authorizeHttpRequests((authorizeRequest) -> authorizeRequest
                        .requestMatchers(
                                "/", "/test/", "/css/**", "/images/**", "/js/**",
                                "/login", "/login/**",
                                "/logout", "/logout/**",
                                "/oauth2/**",
                                "/login/oauth2/authorization/**",
                                "/api/v1/auth/**",
                                "/swagger-ui/**", "/v3/api-docs/**",
                                "/posts/**", "/comments/**"
                        ).permitAll()
                        .requestMatchers("/posts/new", "/comments/save").hasRole(Role.USER.name())
                        .anyRequest().authenticated()
                )
                .logout( // 로그아웃 성공 시 / 주소로 이동
                        (logoutConfig) -> logoutConfig.logoutSuccessUrl("/")
                )
                // OAuth2 로그인 기능에 대한 여러 설정
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")  // 이 경로는 컨트롤러에서 뷰로 연결하거나 정적 페이지여도 무방
                        .authorizationEndpoint(authorization -> authorization
                                .baseUri("/login/oauth2/authorization")
                        )
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService))
                );

        return http.build();
    }
}
