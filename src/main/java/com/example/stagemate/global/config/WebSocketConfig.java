package com.example.stagemate.global.config;


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
                        "http://34.49.53.76"
                )
                .withSockJS();
    }

}
