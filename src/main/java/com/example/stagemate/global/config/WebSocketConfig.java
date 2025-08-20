package com.example.stagemate.global.config;


import com.example.stagemate.global.auth.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@RequiredArgsConstructor
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // ✅ 메시지 브로커 설정: SimpleBroker 사용
        // "/topic"으로 시작하는 주소(destination)를 구독하는 클라이언트에게 메시지를 전달합니다.
        // 클라이언트가 메시지를 구독할 때 사용할 prefix입니다. (예: /topic/chat/room/1)
        config.enableSimpleBroker("/topic");

        // ✅ 클라이언트 -> 서버 메시지 라우팅 설정
        // 클라이언트가 서버로 메시지를 보낼 때 사용할 prefix입니다.
        // @MessageMapping 어노테이션이 붙은 메서드로 메시지가 라우팅됩니다. (예: /app/chat/message)
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-stomp")
                .setAllowedOrigins(
                        "http://localhost:3000",
                        "http://localhost:5173",
                        "http://34.49.53.76",
                        "https://stagemate.co.kr"
                )
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(org.springframework.messaging.simp.config.ChannelRegistration registration) {
        registration.interceptors(new org.springframework.messaging.support.ChannelInterceptor() {
            @Override
            public org.springframework.messaging.Message<?> preSend(org.springframework.messaging.Message<?> message,
                                                                    org.springframework.messaging.MessageChannel channel) {
                var accessor = org.springframework.messaging.simp.stomp.StompHeaderAccessor.wrap(message);

                if (org.springframework.messaging.simp.stomp.StompCommand.CONNECT.equals(accessor.getCommand())) {
                    // 1) Authorization 헤더에서 토큰 추출
                    String authHeader = accessor.getFirstNativeHeader("Authorization"); // "Bearer xxx"
                    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                        throw new IllegalArgumentException("Missing Authorization header");
                    }
                    String token = authHeader.substring(7);

                    // 2) 토큰 검증
                    if (!jwtTokenProvider.validateToken(token)) {
                        throw new IllegalArgumentException("Invalid JWT");
                    }

                    // 3) 토큰에서 사용자 식별자 꺼내기
                    Long userId = jwtTokenProvider.getUserId(token);

                    // 4) Authentication 생성해서 Principal로 세팅
                    //    (권한이 필요하면 실제 UserDetails를 로드해서 넣으세요)
                    var authorities = org.springframework.security.core.authority.AuthorityUtils.NO_AUTHORITIES;
                    var authentication = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                            String.valueOf(userId), // principal (String 권장)
                            null,                   // credentials
                            authorities
                    );

                    accessor.setUser(authentication); // STOMP Principal
                    org.springframework.security.core.context.SecurityContextHolder.getContext()
                            .setAuthentication(authentication);
                }
                return message;
            }
        });
    }

}
