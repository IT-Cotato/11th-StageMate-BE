package com.example.stagemate.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import static com.example.stagemate.global.util.ValidationConstants.*;

public record ChangePasswordRequest(
        @NotBlank(message = "현재 비밀번호를 입력해주세요.")
        String currentPassword,

        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH, message = "비밀번호는 8~20자 이내로 입력해주세요.")
        @Pattern(regexp = PASSWORD_REGEX, message = "비밀번호는 영문, 숫자, 특수문자 중 2가지 이상을 조합해주세요.")
        String newPassword,


        @NotBlank(message = "새 비밀번호 확인을 입력해주세요.")
        String newPasswordConfirm
) {}