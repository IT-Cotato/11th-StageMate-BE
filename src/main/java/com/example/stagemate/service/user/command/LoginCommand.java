package com.example.stagemate.service.user.command;

import com.example.stagemate.dto.request.LoginRequestDTO;

public record LoginCommand(
        String userId,
        String password
) {

    public static LoginCommand from(LoginRequestDTO request) {
        return new LoginCommand(
                request.userId(),
                request.password()
        );
    }

}