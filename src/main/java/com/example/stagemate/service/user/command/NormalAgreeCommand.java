package com.example.stagemate.service.user.command;

import com.example.stagemate.domain.user.model.ConsentType;
import com.example.stagemate.dto.request.ConsentRequestDTO;
import com.example.stagemate.global.exception.AppException;
import com.example.stagemate.global.exception.CommonErrorCode;
import lombok.Builder;

import java.util.Map;

@Builder
public record NormalAgreeCommand(
        Map<ConsentType, Boolean> consents
) {
    public static NormalAgreeCommand from(ConsentRequestDTO request) {

        //DTO -> Command 변환 시점에서 필수 입력값 검증
        Map<ConsentType, Boolean> consents = request.getConsents();

        for (ConsentType required : ConsentType.getRequiredConsents()) {
            if (!Boolean.TRUE.equals(consents.get(required))) {
                throw new AppException(
                        CommonErrorCode.INVALID_PARAMETER,
                        required.getDescription() + "은(는) 필수 동의 항목입니다."
                );
            }
        }


        return NormalAgreeCommand.builder()
                .consents(request.getConsents())
                .build();
    }
}
