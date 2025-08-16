package com.example.stagemate.dto.request.chat;

import com.example.stagemate.domain.chat.Chat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
public class ChatRequest {
    private String content;
    private Long roomId;
    private Long senderId;

    public Chat toEntity() {
        return Chat.builder()
                .content(content)
                .roomId(roomId)
                .senderId(senderId)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
