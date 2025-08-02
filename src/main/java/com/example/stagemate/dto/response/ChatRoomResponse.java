package com.example.stagemate.dto.response;


import com.example.stagemate.domain.chat.ChatRoom;

import java.time.LocalDate;

public record ChatRoomResponse(
        Long chatRoomId,
        String title,
        LocalDate startDate,
        LocalDate endDate
) {
    public static ChatRoomResponse from(ChatRoom chatRoom) {
        return new ChatRoomResponse(chatRoom.getId(), chatRoom.getTitle(), chatRoom.getStartDate(), chatRoom.getEndDate());
    }

}
