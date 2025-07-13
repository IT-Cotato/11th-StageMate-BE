package com.example.stagemate.dto.request;

import com.example.stagemate.domain.user.model.ConsentType;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.List;

public record OAuth2RegisterRequestDTO(
        @NotBlank
        String nickname,

        @NotNull
        @Past(message = "생년월일은 현재보다 과거여야 합니다.")
        LocalDate birthdate,

        @NotNull
        List<ConsentType> consents
) {
}
