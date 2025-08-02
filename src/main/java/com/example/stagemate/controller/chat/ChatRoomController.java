package com.example.stagemate.controller.chat;

import com.example.stagemate.dto.response.ChatRoomResponse;
import com.example.stagemate.global.dto.DataResponse;
import com.example.stagemate.service.chat.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    @GetMapping("/api/v1/chat-room")
    public ResponseEntity<DataResponse<Page<ChatRoomResponse>>> getChatRooms(int page, int size) {
        org.springframework.data.domain.Page<ChatRoomResponse> chatRooms = chatRoomService.getChatRooms(page, size);
        return ResponseEntity.ok(DataResponse.from(chatRooms));
    }
}
