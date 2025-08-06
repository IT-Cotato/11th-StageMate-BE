package com.example.stagemate.controller.chat;

import com.example.stagemate.dto.response.chat.ChatRoomResponse;
import com.example.stagemate.global.dto.DataResponse;
import com.example.stagemate.global.dto.PagedResponse;
import com.example.stagemate.service.chat.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    @GetMapping("/api/v1/chat-room")
    public ResponseEntity<DataResponse<PagedResponse<ChatRoomResponse>>> getChatRooms(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        PagedResponse<ChatRoomResponse> chatRooms = chatRoomService.getChatRooms(page, size);
        return ResponseEntity.ok(DataResponse.from(chatRooms));
    }
}
