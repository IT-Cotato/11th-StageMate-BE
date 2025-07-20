package com.example.stagemate.dto.request;

import com.example.stagemate.domain.user.model.ConsentType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

import java.util.Map;

@Getter
public class ConsentRequestDTO {

    @Schema(description = "일반 가입자의 경우 userId 필수, OAuth 가입자의 경우 생략")
    private String userId;

    @Schema(
            description = "동의한 약관 목록 (ConsentType: true/false)",
            example = "{ \"SERVICE_TERMS\" : true, \"PRIVACY_POLICY\" : true, \"MARKETING\" : false, \"SMS_NOTIFICATION\" : false, \"EMAIL_NOTIFICATION\" : false}"
        )
    @NotEmpty
    private Map<String, Boolean> consents;

}
