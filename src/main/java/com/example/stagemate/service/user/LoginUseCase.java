package com.example.stagemate.service.user;

import com.example.stagemate.service.user.command.LoginCommand;

public interface LoginUseCase {
    String login(LoginCommand command);
}
