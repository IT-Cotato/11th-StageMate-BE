package com.example.stagemate.dto.auth;

import lombok.Builder;

import java.io.Serializable;

@Builder
public record GuestInfo(
        String userId,
        String name,
        String email,
        String picture
) implements Serializable {
}
