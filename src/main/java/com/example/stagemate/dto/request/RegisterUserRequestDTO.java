package com.example.stagemate.dto.request;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

import static com.example.stagemate.global.util.ValidationConstants.*;

public record RegisterUserRequestDTO(
        @NotBlank(message = "아이디를 입력해주세요.")
        @Size(min = USER_ID_MIN_LENGTH, max = USER_ID_MAX_LENGTH, message = "아이디는 4~16자 이내로 입력해주세요.")
        @Pattern(regexp = USER_ID_REGEX, message = "아이디는 영문과 숫자만 사용할 수 있습니다.")
        String userId,

        @NotBlank(message = "이메일을 입력해주세요.")
        @Email(message = "유효한 이메일 주소를 입력해주세요.")
        String email,

        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH, message = "비밀번호는 8~20자 이내로 입력해주세요.")
        @Pattern(regexp = PASSWORD_REGEX, message = "비밀번호는 영문, 숫자, 특수문자 중 2가지 이상을 조합해주세요.")
        String password,

        @NotBlank(message = "이름을 입력해주세요.")
        String name,

        @NotBlank(message = "닉네임을 입력해주세요.")
        String nickname,

        @NotNull(message = "생년월일을 입력해주세요.")
        @Past(message = "생년월일은 현재보다 과거여야 합니다.")
        LocalDate birthdate
) {
}