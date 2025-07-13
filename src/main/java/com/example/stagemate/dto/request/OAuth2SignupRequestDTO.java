package com.example.stagemate.dto.request;

import com.example.stagemate.dto.auth.GuestInfo;
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

    private GuestInfo guestInfo;
}
