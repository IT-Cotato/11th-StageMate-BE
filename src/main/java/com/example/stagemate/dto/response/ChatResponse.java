package com.example.stagemate.dto.response;

import com.example.stagemate.domain.chat.Chat;

import java.time.LocalDateTime;

public record ChatResponse(
        Long senderId,
        String senderNickname,
        String content,
        LocalDateTime createdAt
) {
    public static ChatResponse from(Chat chat) {
        return new ChatResponse(chat.getSenderId(), chat.getSenderNickname(), chat.getContent(), chat.getCreatedAt());
    }

}
