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
    private String senderNickname;

    public Chat toEntity() {
        return Chat.builder()
                .content(content)
                .roomId(roomId)
                .senderId(senderId)
                .senderNickname(senderNickname)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
