package com.example.stagemate.redis;

import com.example.stagemate.dto.request.chat.ChatRequest;
import com.fasterxml.jackson.databind.JsonNode;
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
            // roomId만 뽑아서 destination 구성
            JsonNode root = objectMapper.readTree(message);
            long roomId = root.get("roomId").asLong();
            String destination = "/topic/chat/room/" + roomId;

            // ✅ JSON을 재가공하지 말고 그대로 브로커로 전달 (chatId/createdAt 유지)
            messagingTemplate.convertAndSend(destination, message);

            log.info("Published to destination: {}", destination);
        } catch (Exception e) {
            log.error("Failed to forward message: {}", message, e);
        }
    }


//    public void onMessage(String message, String channel) {
//        log.info("Received message: {} from channel: {}", message, channel);
//        try {
//            ChatRequest chatRequest = objectMapper.readValue(message, ChatRequest.class);
//
//            String destinationChannel = "chat";
//
//            messagingTemplate.convertAndSend(destinationChannel, chatRequest);
//
//            log.info("chatRequest: {}", chatRequest.toString());
//        } catch (Exception e) {
//            log.error("Failed to parse message: {}", message, e);
//        }
//    }
}
