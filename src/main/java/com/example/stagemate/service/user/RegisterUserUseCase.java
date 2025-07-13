package com.example.stagemate.service.user;

import com.example.stagemate.domain.user.User;
import com.example.stagemate.service.user.command.NormalAgreeCommand;
import com.example.stagemate.service.user.command.RegisterUserCommand;

public interface RegisterUserUseCase {
    void normalSignupInfo(RegisterUserCommand command);

    User normalAgreeAndRegister(NormalAgreeCommand command, String userId);
}
