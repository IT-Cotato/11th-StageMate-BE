package com.example.stagemate.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class OAuth2SignupRequestDTO {

    @NotBlank
    private String nickname;

    @NotNull
    private LocalDate birthdate;
}
