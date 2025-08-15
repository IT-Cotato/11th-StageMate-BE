package com.example.stagemate.global.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@Profile("!local")
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 프론트 도메인 허용 (https 프로토콜 포함)
        configuration.addAllowedOrigin("https://stagemate.co.kr");
        configuration.addAllowedOrigin("https://www.stagemate.co.kr");
        configuration.addAllowedOrigin("https://api.stagemate.co.kr");


        configuration.addAllowedOrigin("http://localhost:8080");
        configuration.addAllowedOrigin("http://localhost:3000");
        configuration.addAllowedOrigin("http://localhost:5173");      // ✅ 추가
        configuration.addAllowedOrigin("http://34.49.53.76");         // ✅ 추가

        configuration.setAllowCredentials(true);                      // ✅ true이면 origin은 * 사용 금지
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*"); // 또는 GET, POST, PUT, DELETE, OPTIONS

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .cors(Customizer.withDefaults()) // swagger 설정하다가 추가
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) //stateLess로 다시 변경
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/login", "/api/v1/auth/**","/login/oauth2/**", "/oauth2/**", "/swagger-ui/**", "/v3/api-docs/**","/api/v1/check/**", "/api/v1/email/*"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/mypage/notices",
                                "/api/v1/mypage/notices/*",
                                "api/v1/mypage/policy/**"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/auth/sign-up/tempUserKey")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/check/userId/*",
                                "/api/v1/check/nickname/*")
                        .permitAll()
                        .requestMatchers(HttpMethod.POST,
                                "/api/v1/auth/login",
                                  "/api/v1/auth/sign-up/agree",
                                  "/api/v1/auth/sign-up/info")
                        .permitAll()
                        .requestMatchers(HttpMethod.POST,
                                "/api/v1/email/send-code")
                        .permitAll()
                        .requestMatchers("/actuator/**")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/communities/hot",
                                "/api/v1/communities",
                                "/api/v1/communities/trade",
                                "/api/v1/communities/*"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/magazines/latest",
                                "/api/v1/magazines/recommend",
                                "/api/v1/magazines",
                                "/api/v1/magazines/*"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/performance/recommend",
                                "/api/v1/performance",
                                "/api/v1/performance/*"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/performanceSchedule",
                                "/api/v1/performanceSchedule/*"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/theaters"
                        ).permitAll()
                        .requestMatchers(HttpMethod.POST,
                                "/api/v1/email/*"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/check/**"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/chat-room"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/search",
                                "/api/v1/search/popular"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/mypage/notices",
                                "/api/v1/mypage/notices/*",
                                "/api/v1/mypage/policy/terms",
                                "/api/v1/mypage/policy/privacy")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET,
                                 "/api/v1/event/all-performance",
                                   "/api/v1/event/all-post",
                                   "/api/v1/event")
                        .permitAll()
                        .requestMatchers(HttpMethod.POST,
                                "/api/v1/event")
                        .permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(oAuth2LoginSuccessHandler)
                        .failureHandler(oAuth2LoginFailureHandler)
                        .authorizationEndpoint(authorizationEndpointConfig -> authorizationEndpointConfig
                                .baseUri("/login/oauth2/authorization"))
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(customAuthenticationEntryPoint) // 인증 실패 시 처리할 EntryPoint
                )
                .cors(Customizer.withDefaults())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


}
