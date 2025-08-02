package com.example.stagemate.redis;

import com.example.stagemate.dto.request.chat.ChatRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisMessageSubscriber {
    private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate messagingTemplate;

    public void onMessage(String message, String channel) {
        log.info("Received message: {} from channel: {}", message, channel);
        try {
            ChatRequest chatRequest = objectMapper.readValue(message, ChatRequest.class);

            String destinationChannel = "chat";

            messagingTemplate.convertAndSend(destinationChannel, chatRequest);

            log.info("chatRequest: {}", chatRequest.toString());
        } catch (Exception e) {
            log.error("Failed to parse message: {}", message, e);
        }
    }
}
