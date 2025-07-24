package com.example.stagemate.service.user;

import com.example.stagemate.domain.user.User;
import com.example.stagemate.dto.request.RegisterUserRequestDTO;
import com.example.stagemate.service.user.command.NormalAgreeCommand;
import com.example.stagemate.service.user.command.RegisterUserCommand;

public interface RegisterUserUseCase {
    String normalSignupInfo(RegisterUserCommand command);

    // 동의 정보까지 포함한 최종 회원가입 (OAuth 등에서 사용)
    User normalAgreeAndRegister(NormalAgreeCommand command, String userId);

    // 일반 회원가입 (정보입력 + 유저 생성)
    User execute(RegisterUserRequestDTO request); // 리턴 타입 변경
}
