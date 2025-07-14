package com.example.stagemate.dto.request;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

import static com.example.stagemate.global.util.ValidationConstants.*;

public record RegisterUserRequestDTO(
        @NotBlank
        @Size(min = USER_ID_MIN_LENGTH, max = USER_ID_MAX_LENGTH, message = USER_ID_LENGTH_MESSAGE)
        @Pattern(regexp = USER_ID_REGEX, message = USER_ID_PATTERN_MESSAGE)
        String userId,

        @NotBlank
        @Email(message = "유효한 이메일 주소를 입력해주세요.")
        String email,

        @NotBlank
        @Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH, message = PASSWORD_LENGTH_MESSAGE)
        @Pattern(regexp = PASSWORD_REGEX, message = PASSWORD_PATTERN_MESSAGE)
        String password,

        @NotBlank
        String name,

        @NotBlank
        String nickname,

        @NotNull
        @Past(message = "생년월일은 현재보다 과거여야 합니다.")
        LocalDate birthdate
) {
}