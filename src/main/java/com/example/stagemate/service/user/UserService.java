package com.example.stagemate.service.user;

import com.example.stagemate.domain.user.User;
import com.example.stagemate.domain.user.Role;
import com.example.stagemate.domain.user.model.ConsentType;
import com.example.stagemate.domain.user.port.out.LoadUserPort;
import com.example.stagemate.domain.user.port.out.SaveUserPort;
import com.example.stagemate.global.exception.AppException;
import com.example.stagemate.global.exception.CommonErrorCode;
import com.example.stagemate.service.user.command.LoginCommand;
import com.example.stagemate.service.user.command.NormalAgreeCommand;
import com.example.stagemate.service.user.command.RegisterUserCommand;
import com.example.stagemate.dto.auth.GuestInfo;
import com.example.stagemate.dto.request.ConsentRequestDTO;
import com.example.stagemate.dto.request.OAuth2RegisterRequestDTO;
import com.example.stagemate.dto.request.OAuth2SignupRequestDTO;
import com.example.stagemate.dto.request.RegisterUserRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static com.example.stagemate.global.exception.CommonErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService implements LoginUseCase, RegisterUserUseCase {

    private final SaveUserPort saveUserPort;
    private final LoadUserPort loadUserPort;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public String normalSignupInfo(RegisterUserCommand command) {
        if (loadUserPort.existsByUserId(command.userId())) {
            throw new AppException(CommonErrorCode.RESOURCE_CONFLICT, "이미 사용 중인 아이디입니다.");
        }

        if (loadUserPort.existsByNickname(command.nickname())) {
            throw new AppException(CommonErrorCode.RESOURCE_CONFLICT, "이미 사용 중인 닉네임입니다.");
        }

        User guest = User.normalGuestSignUp(
                command.userId(),
                command.email(),
                passwordEncoder.encode(command.password()),
                command.name(),
                command.nickname(),
                command.birthdate()
        );

        saveUserPort.save(guest);
        return command.userId();
    }

    @Override
    @Transactional
    public User normalAgreeAndRegister(NormalAgreeCommand command, String userId) {
        validateConsents(command.consents());

        User guestUser = loadUserPort.findByUserId(userId)
                .orElseThrow(() -> new AppException(CommonErrorCode.NOT_FOUND_USER));

        User finalUser = guestUser.register(command.consents());

        return saveUserPort.save(finalUser);
    }

    @Transactional
    public void oauthSignupInfo(OAuth2SignupRequestDTO request, GuestInfo guestInfo) {
        if (loadUserPort.existsByNickname(request.getNickname())) {
            throw new AppException(CommonErrorCode.RESOURCE_CONFLICT, "이미 사용 중인 닉네임입니다.");
        }

        User guestUser = loadUserPort.findByEmail(guestInfo.email())
                .orElseThrow(() -> new AppException(CommonErrorCode.NOT_FOUND_USER));

        User updatedGuest = guestUser.updateGuestInfo(request.getNickname(), request.getBirthdate());

        saveUserPort.save(updatedGuest);
    }

    @Transactional
    public User oauthAgreeAndRegister(ConsentRequestDTO request, GuestInfo guestInfo) {
        validateConsents(request.getConsents());

        User guestUser = loadUserPort.findByEmail(guestInfo.email())
                .orElseThrow(() -> new AppException(CommonErrorCode.NOT_FOUND_USER));

        User finalUser = guestUser.register(request.getConsents());

        return saveUserPort.save(finalUser);
    }

    @Override
    public String login(LoginCommand command) {
        User user = loadUserPort.findByUserId(command.userId())
                .orElseThrow(() -> new AppException(CommonErrorCode.AUTHENTICATION_FAILED));

        if (!passwordEncoder.matches(command.password(), user.getPassword())) {
            throw new AppException(CommonErrorCode.AUTHENTICATION_FAILED);
        }

        return user.getId().toString();
    }

    public User findUserById(Long id) {
        return loadUserPort.findById(id)
                .orElseThrow(() -> new AppException(CommonErrorCode.NOT_FOUND));
    }

    public boolean checkUserIdExists(String userId) {
        return loadUserPort.existsByUserId(userId);
    }

    public boolean checkNicknameExists(String nickname) {
        return loadUserPort.existsByNickname(nickname);
    }

    private void validateConsents(List<ConsentType> consents) {
        boolean allRequiredConsentsPresent = consents.containsAll(ConsentType.getRequiredConsents());

        if (!allRequiredConsentsPresent) {
            throw new AppException(CommonErrorCode.BAD_REQUEST, "필수 이용 약관에 모두 동의해야 합니다.");
        }
    }
}