package com.example.stagemate.dto.request.chat;

import com.example.stagemate.domain.community.ReportReason;

public record ReportChatRequest(
        String reason,
        String chatId
) {
}
