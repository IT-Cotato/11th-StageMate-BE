package com.example.stagemate.service.user.command;

import com.example.stagemate.dto.request.LoginRequest;

public record LoginCommand(
        String userId,
        String password
) {

    public static LoginCommand from(LoginRequest request) {
        return new LoginCommand(
                request.userId(),
                request.password()
        );
    }


}