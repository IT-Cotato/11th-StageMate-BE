package com.example.stagemate.controller.chat;


import com.example.stagemate.dto.request.chat.ChatRequest;
import com.example.stagemate.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;


@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatWriteController {
    private final ChatService chatService;


    @MessageMapping("api/v1/chat-room/chat")
    public void sendGroupChat(ChatRequest chatRequest) {
        log.info("chatRequest: {}", chatRequest.toString());
        chatService.sendGroupChat(chatRequest);
    }


}
