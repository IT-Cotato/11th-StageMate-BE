package com.example.stagemate.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RedisMessagePublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final ChannelTopic channelTopic;

    public void publish(Object chat) {
        try {
            String json = objectMapper.writeValueAsString(chat); // ✅ 객체 → JSON 문자열
            redisTemplate.convertAndSend(channelTopic.getTopic(), json);            // ✅ Redis 채널로 퍼블리시
        } catch (Exception e) {
            throw new RuntimeException("Redis publish 실패", e);
        }
    }
}