package com.example.stagemate.redis;

import com.example.stagemate.domain.chat.Chat;
import com.example.stagemate.global.util.DateFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Slf4j
public class RedisMessagePublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final ChannelTopic channelTopic;

    public void publish(Chat chat) {
        try {
            // createdAt 포맷팅
            String formatted = DateFormat.formatTimeIfTodayElseDateTime(chat.getCreatedAt());

            log.info("Redis publish. chatId={}, roomId={}, senderId={}, content={}, createdAt={}", chat.getId(), chat.getRoomId(), chat.getSenderId(), chat.getContent(), formatted);
            // 전송 payload 구성
            Map<String, Object> payload = new HashMap<>();
            payload.put("chatId", chat.getId());
            payload.put("roomId", chat.getRoomId());
            payload.put("senderId", chat.getSenderId());
            payload.put("content", chat.getContent());
            payload.put("createdAt", formatted);

            String json = objectMapper.writeValueAsString(payload);
            redisTemplate.convertAndSend(channelTopic.getTopic(), json);
        } catch (Exception e) {
            throw new RuntimeException("Redis publish 실패", e);
        }
    }
}
