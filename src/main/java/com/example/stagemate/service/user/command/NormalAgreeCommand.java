package com.example.stagemate.service.user.command;

import com.example.stagemate.domain.user.model.ConsentType;
import com.example.stagemate.dto.request.ConsentRequest;
import com.example.stagemate.global.exception.AppException;
import com.example.stagemate.global.exception.CommonErrorCode;
import lombok.Builder;

import java.util.Map;
import java.util.stream.Collectors;

@Builder
public record NormalAgreeCommand(
        Map<ConsentType, Boolean> consents
) {
    public static NormalAgreeCommand from(ConsentRequest request) {

        // Map<String, Boolean> → Map<ConsentType, Boolean> 수동 변환
        Map<ConsentType, Boolean> consents = request.getConsents().entrySet().stream()
                .filter(e -> e.getKey() != null)
                .collect(Collectors.toMap(
                        e -> {
                            try {
                                return ConsentType.valueOf(e.getKey()); // 문자열 → enum
                            } catch (IllegalArgumentException ex) {
                                throw new AppException(CommonErrorCode.INVALID_PARAMETER,
                                        "알 수 없는 약관 항목입니다: " + e.getKey());
                            }
                        },
                        Map.Entry::getValue
                ));

        //DTO -> Command 변환 시점에서 필수 입력값 검증
        for (ConsentType required : ConsentType.getRequiredConsents()) {
            if (!Boolean.TRUE.equals(consents.get(required))) {
                throw new AppException(
                        CommonErrorCode.INVALID_PARAMETER,
                        required.getDescription() + "은(는) 필수 동의 항목입니다."
                );
            }
        }


        return NormalAgreeCommand.builder()
                .consents(consents)
                .build();
    }
}
