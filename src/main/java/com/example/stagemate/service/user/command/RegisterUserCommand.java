package com.example.stagemate.service.user.command;

import com.example.stagemate.dto.request.RegisterUserRequestDTO;

import java.time.LocalDate;

/**
 * 일반 회원가입 정보 입력 단계에 사용될 커맨드 객체입니다.
 */
public record RegisterUserCommand(
        String userId,
        String email,
        String password,
        String name,
        String nickname,
        LocalDate birthdate
) {
    public static RegisterUserCommand from(RegisterUserRequestDTO request) {
        return new RegisterUserCommand(
                request.userId(),
                request.email(),
                request.password(),
                request.name(),
                request.nickname(),
                request.birthdate()
        );
    }
}
