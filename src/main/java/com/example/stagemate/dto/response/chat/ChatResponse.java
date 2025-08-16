package com.example.stagemate.dto.response.chat;

import com.example.stagemate.domain.chat.Chat;

import java.time.LocalDateTime;

public record ChatResponse(
        String chatId,
        Long senderId,
        String content,
        LocalDateTime createdAt
) {
    public static ChatResponse from(Chat chat) {
        return new ChatResponse(chat.getId(), chat.getSenderId(), chat.getContent(), chat.getCreatedAt());
    }

}
