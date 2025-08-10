package com.example.stagemate.global.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/login/oauth2/") || path.startsWith("/oauth2/") || path.startsWith("/login/oauth2/authorization/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 필요 시: /api/v1/auth/reissue 제외
        if (request.getRequestURI().contains("/api/v1/auth/reissue")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 방어용 try-catch 블록
        try {

            String token = jwtTokenProvider.extractToken(request);

            if (token != null && jwtTokenProvider.validateToken(token)) {
                Long userId = jwtTokenProvider.getUserId(token);
                CustomUserDetails userDetails = customUserDetailsService.loadUserById(userId);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }catch (Exception e) {
            // 토큰이 이상하거나 유효하지 않더라도 무시하고 다음 필터로 넘긴다.
            log.warn("JWT 필터에서 토큰 인증 중 예외 발생: {}", e.getMessage());
        }

            filterChain.doFilter(request, response);
    }
}
