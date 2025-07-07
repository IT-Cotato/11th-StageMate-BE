package com.example.stagemate.service.user.command;

import com.example.stagemate.domain.user.model.ConsentType;
import com.example.stagemate.dto.request.ConsentRequestDTO;
import lombok.Builder;

import java.util.List;

@Builder
public record NormalAgreeCommand(
        List<ConsentType> consents
) {
    public static NormalAgreeCommand from(ConsentRequestDTO request) {
        return NormalAgreeCommand.builder()
                .consents(request.getConsents())
                .build();
    }
}
