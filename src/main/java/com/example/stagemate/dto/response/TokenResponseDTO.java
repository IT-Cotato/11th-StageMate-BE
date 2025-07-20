package com.example.stagemate.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TokenResponseDTO(
        String accessToken,
        String refreshToken
) {
}
