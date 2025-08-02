package com.example.stagemate.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import static com.example.stagemate.global.util.ValidationConstants.*;

public record LoginRequest(

        @NotBlank
        @Size(min = USER_ID_MIN_LENGTH, max = USER_ID_MAX_LENGTH, message = USER_ID_LENGTH_MESSAGE)
        @Pattern(regexp = USER_ID_REGEX, message = USER_ID_PATTERN_MESSAGE)
        String userId,

        @NotBlank
        @Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH, message = PASSWORD_LENGTH_MESSAGE)
        @Pattern(regexp = PASSWORD_REGEX, message = PASSWORD_PATTERN_MESSAGE)
        String password

) {

}
