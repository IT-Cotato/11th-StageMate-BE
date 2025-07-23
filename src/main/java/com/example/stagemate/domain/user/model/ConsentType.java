package com.example.stagemate.domain.user.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
@Schema(description = "사용자 약관 동의 유형")
public enum ConsentType {

    @Schema(description = "서비스 이용 약관 동의", example = "SERVICE_TERMS")
    SERVICE_TERMS("서비스 이용 약관", true),

    @Schema(description = "개인정보 수집 이용 동의", example = "PRIVACY_POLICY")
    PRIVACY_POLICY("개인정보 수집 이용 동의", true),

    @Schema(description = "마케팅 정보 수신 및 활용 동의", example = "MARKETING")
    MARKETING("마케팅 정보 수신 및 활용 동의", false),

    @Schema(description = "SMS 수신 동의", example = "SMS_NOTIFICATION")
    SMS_NOTIFICATION("SMS 수신 동의", false),

    @Schema(description = "이메일 수신 동의", example = "EMAIL_NOTIFICATION")
    EMAIL_NOTIFICATION("이메일 수신 동의", false);

    private final String description;
    private final boolean isRequired;

    public static List<ConsentType> getRequiredConsents() {
        return Arrays.stream(values())
                .filter(ConsentType::isRequired)
                .collect(Collectors.toList());
    }
}
