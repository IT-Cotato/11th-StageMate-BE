package com.example.stagemate.domain.user.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum ConsentType {
    SERVICE_TERMS("서비스 이용 약관", true),
    PRIVACY_POLICY("개인정보 수집 이용 동의", true),
    MARKETING("마케팅 정보 수신 및 활용 동의", false),
    SMS_NOTIFICATION("SMS 수신 동의", false),
    EMAIL_NOTIFICATION("이메일 수신 동의", false);

    private final String description;
    private final boolean isRequired;

    public static List<ConsentType> getRequiredConsents() {
        return Arrays.stream(values())
                .filter(ConsentType::isRequired)
                .collect(Collectors.toList());
    }
}
