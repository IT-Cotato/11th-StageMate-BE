package com.example.stagemate.service.user;

import com.example.stagemate.dto.response.TokenResponse;
import com.example.stagemate.service.user.command.LoginCommand;

public interface LoginUseCase {
    TokenResponse login(LoginCommand command);//리턴 타입 일치시킴
}
