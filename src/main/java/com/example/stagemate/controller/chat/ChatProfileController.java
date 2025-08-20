package com.example.stagemate.controller.chat;

import com.example.stagemate.domain.user.entity.UserJpaEntity;
import com.example.stagemate.dto.response.chat.ChatProfileResponse;
import com.example.stagemate.global.dto.DataResponse;
import com.example.stagemate.global.reslover.CurrentUser;
import com.example.stagemate.service.chat.ChatProfileService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatProfileController {
    private final ChatProfileService chatProfileService;

    @GetMapping("api/v1/chat/profile")
    public ResponseEntity<DataResponse<List<ChatProfileResponse>>> getChatProfile(
            @Parameter(hidden = true) @CurrentUser UserJpaEntity user,
            @RequestParam("senderIds") List<Long> senderIds
    ) {
        List<ChatProfileResponse> chatProfileResponse = chatProfileService.getChatProfile(user.getId(), senderIds);
        return ResponseEntity.ok(DataResponse.from(chatProfileResponse));
    }
}
