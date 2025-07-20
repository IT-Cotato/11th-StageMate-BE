package com.example.stagemate.service.user;

import com.example.stagemate.domain.user.User;
import com.example.stagemate.domain.user.entity.RefreshTokenEntity;
import com.example.stagemate.domain.user.model.ConsentType;
import com.example.stagemate.domain.user.port.out.LoadUserPort;
import com.example.stagemate.domain.user.port.out.SaveUserPort;
import com.example.stagemate.dto.response.TokenResponseDTO;
import com.example.stagemate.global.exception.AppException;
import com.example.stagemate.global.exception.CommonErrorCode;
import com.example.stagemate.repository.user.RefreshTokenRepository;
import com.example.stagemate.service.user.command.LoginCommand;
import com.example.stagemate.service.user.command.NormalAgreeCommand;
import com.example.stagemate.service.user.command.RegisterUserCommand;
import com.example.stagemate.dto.auth.GuestInfo;
import com.example.stagemate.dto.request.OAuth2SignupRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.stagemate.global.auth.JwtTokenProvider;

import java.time.LocalDateTime;
import java.util.Map;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService implements LoginUseCase, RegisterUserUseCase {

    private final SaveUserPort saveUserPort;
    private final LoadUserPort loadUserPort;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

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


    @Override
    public TokenResponseDTO login(LoginCommand command) {
        User user = loadUserPort.findByUserId(command.userId())
                .orElseThrow(() -> new AppException(CommonErrorCode.AUTHENTICATION_FAILED));

        if (!passwordEncoder.matches(command.password(), user.getPassword())) {
            throw new AppException(CommonErrorCode.AUTHENTICATION_FAILED);
        }

        //AccessToken, RefreshToken 발급
        String accessToken = jwtTokenProvider.createToken(user.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());

        // RefreshToken 저장
        refreshTokenRepository.save(
                RefreshTokenEntity.builder()
                        .userId(user.getId())
                        .token(refreshToken)
                        .expiresAt(LocalDateTime.now().plusDays(14))
                        .build()
        );

        //응답 DTO로 반환
        return new TokenResponseDTO(accessToken, refreshToken);
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


    //비즈니스 로직 단계에서 필수 동의 항목 검증
    private void validateConsents(Map<ConsentType, Boolean> consents) {
        for (ConsentType required : ConsentType.getRequiredConsents()) {
            if (!Boolean.TRUE.equals(consents.get(required))) {
                throw new AppException(CommonErrorCode.BAD_REQUEST, required.getDescription() + "은(는) 필수 동의 항목입니다.");
            }
        }
    }
}