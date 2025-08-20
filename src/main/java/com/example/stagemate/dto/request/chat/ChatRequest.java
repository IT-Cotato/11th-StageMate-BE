package com.example.stagemate.dto.request.chat;

import com.example.stagemate.domain.chat.Chat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Setter
@Getter
@NoArgsConstructor
public class ChatRequest {
    private String chatId;
    private String content;
    private Long roomId;
    private Long senderId;
    private LocalDateTime createdAt;

    public Chat toEntity() {
        return Chat.builder()
                .id(chatId)
                .content(content)
                .roomId(roomId)
                .senderId(senderId)
                .createdAt(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                .build();
    }
}
