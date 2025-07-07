package com.example.stagemate.dto.request;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

import static com.example.stagemate.global.util.ValidationConstants.*;

public record OAuth2RegisterRequestDTO(
        @NotBlank
        @Size(min = USER_ID_MIN_LENGTH, max = USER_ID_MAX_LENGTH, message = USER_ID_LENGTH_MESSAGE)
        @Pattern(regexp = USER_ID_REGEX, message = USER_ID_PATTERN_MESSAGE)
        String userId,

        @NotBlank
        String nickname,

        @NotNull
        @Past(message = "생년월일은 현재보다 과거여야 합니다.")
        LocalDate birthdate,

        @AssertTrue(message = "이용 약관에 동의해야 합니다.")
        @NotNull
        Boolean termsAgreed,

        @AssertTrue(message = "개인정보 수집 및 이용에 동의해야 합니다.")
        @NotNull
        Boolean privacyPolicyAgreed
) {
}
