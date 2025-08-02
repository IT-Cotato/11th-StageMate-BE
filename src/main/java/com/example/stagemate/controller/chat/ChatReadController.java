package com.example.stagemate.controller.chat;

import com.example.stagemate.dto.response.ChatResponse;
import com.example.stagemate.global.dto.DataResponse;
import com.example.stagemate.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatReadController {
    private final ChatService chatService;

    @GetMapping("api/v1/room/{roomId}/chat")
    public ResponseEntity<DataResponse<List<ChatResponse>>> getChatMessages(
            @PathVariable Long roomId,
            @RequestParam(required = false) String cursorId,
            @RequestParam(defaultValue = "20") int limit) {

        List<ChatResponse> chatResponses = chatService.getPreviousMessages(roomId, cursorId, limit);

        return ResponseEntity.ok(DataResponse.from(chatResponses));

    }
}