package com.example.stagemate.service.user.command;

import com.example.stagemate.domain.user.User;
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

    public static LoginCommand fromUser(User user) {
        return new LoginCommand(
                user.getUserId(),
                user.getPassword() // ⚠️ 실제로 필요하면, 또는 null로 처리해도 가능
        );
    }


}