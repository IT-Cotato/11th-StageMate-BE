package com.example.stagemate.dto.response.chat;

import com.example.stagemate.domain.chat.Chat;
import com.example.stagemate.global.util.DateFormat;

import java.time.LocalDateTime;

public record ChatResponse(
        String chatId,
        Long senderId,
        String content,
        String createdAt
) {
    public static ChatResponse from(Chat chat) {
        return new ChatResponse(
                chat.getId(),
                chat.getSenderId(),
                chat.getContent(),
                DateFormat.formatTimeIfTodayElseDateTime(chat.getCreatedAt()));
    }

}
