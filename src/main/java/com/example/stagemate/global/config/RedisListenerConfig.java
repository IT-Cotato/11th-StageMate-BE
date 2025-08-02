package com.example.stagemate.global.config;

import com.example.stagemate.redis.RedisMessageSubscriber;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
@RequiredArgsConstructor
public class RedisListenerConfig {
    private final RedisMessageSubscriber redisMessageSubscriber;

    @Bean
    public ChannelTopic channelTopic() {
        return new ChannelTopic("chat");
    }

    @Bean
    public MessageListenerAdapter messageListenerAdapter() {
        return new MessageListenerAdapter(redisMessageSubscriber, "onMessage");
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListener(
            RedisConnectionFactory redisConnectionFactory, // Redis 연결 정보
            MessageListenerAdapter listenerAdapterSendMessage, // 위에서 만든 어댑터
            ChannelTopic sendMessageTopic // 위에서 만든 토픽
            // ... (다른 어댑터와 토픽들도 인자로 받아서 등록)
    ) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);

        // "sendMessage" 토픽(채널)과 해당 메시지를 처리할 리스너(어댑터)를 연결 (구독!)
        container.addMessageListener(listenerAdapterSendMessage, sendMessageTopic);
        // ... (다른 토픽과 리스너 연결 추가)

        // 에러 핸들러 설정 등 추가 설정 가능
        // container.setErrorHandler(e -> log.error("Redis Listener Error", e));

        return container;
    }
}
