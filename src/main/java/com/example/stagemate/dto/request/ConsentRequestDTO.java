package com.example.stagemate.dto.request;

import com.example.stagemate.domain.user.model.ConsentType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

import java.util.Map;

@Getter
public class ConsentRequestDTO {

    @Schema(description = "회원가입 임시 식별자", example = "e1432a5f-89a0-4f1e-85a7-7a91b2be14e9")
    private String tempUserKey;

    @Schema(
            description = "동의한 약관 목록 (ConsentType: true/false)",
            example = "{ \"SERVICE_TERMS\" : true, \"PRIVACY_POLICY\" : true, \"MARKETING\" : false, \"SMS_NOTIFICATION\" : false, \"EMAIL_NOTIFICATION\" : false}"
        )
    @NotEmpty
    private Map<String, Boolean> consents;

}
