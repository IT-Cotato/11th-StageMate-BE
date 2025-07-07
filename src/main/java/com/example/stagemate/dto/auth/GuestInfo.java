package com.example.stagemate.dto.auth;

import java.io.Serializable;

public record GuestInfo(
        String name,
        String email,
        String picture
) implements Serializable {
}
