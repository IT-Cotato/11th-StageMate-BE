package com.example.stagemate.service.user;

import com.example.stagemate.dto.response.TokenResponseDTO;
import com.example.stagemate.service.user.command.LoginCommand;

public interface LoginUseCase {
    TokenResponseDTO login(LoginCommand command);//리턴 타입 일치시킴
}
