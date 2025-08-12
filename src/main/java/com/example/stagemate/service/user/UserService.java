package com.example.stagemate.service.user;

import com.example.stagemate.domain.user.User;
import com.example.stagemate.domain.user.entity.RefreshTokenEntity;
import com.example.stagemate.domain.user.entity.UserJpaEntity;
import com.example.stagemate.domain.user.model.ConsentType;
import com.example.stagemate.domain.user.port.out.LoadUserPort;
import com.example.stagemate.domain.user.port.out.SaveUserPort;
import com.example.stagemate.dto.request.RegisterUserRequest;
import com.example.stagemate.dto.response.AccountInfoResponse;
import com.example.stagemate.dto.response.TokenResponse;
import com.example.stagemate.global.exception.AppException;
import com.example.stagemate.global.exception.CommonErrorCode;
import com.example.stagemate.global.exception.auth.AuthErrorCode;
import com.example.stagemate.repository.user.RefreshTokenRepository;
import com.example.stagemate.repository.user.UserJpaRepository;
import com.example.stagemate.service.user.command.LoginCommand;
import com.example.stagemate.service.user.command.NormalAgreeCommand;
import com.example.stagemate.service.user.command.RegisterUserCommand;
import com.example.stagemate.dto.auth.GuestInfo;
import com.example.stagemate.dto.request.OAuth2SignupRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.stagemate.global.auth.JwtTokenProvider;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService implements LoginUseCase, RegisterUserUseCase {

    private final SaveUserPort saveUserPort;
    private final LoadUserPort loadUserPort;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserJpaRepository userJpaRepository;


    @Override
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
    public User execute(RegisterUserRequest request) {
        // 비밀번호 일치 검증
        if (!request.password().equals(request.passwordConfirm())) {
            throw new AppException(AuthErrorCode.PASSWORD_CONFIRM_NOT_MATCH);
        }

        // 커맨드 변환
        RegisterUserCommand command = RegisterUserCommand.from(request);

        // userId 저장
        String userId = normalSignupInfo(command);

        // userId로 다시 조회해서 User 반환
        return loadUserPort.findByUserId(userId)
                .orElseThrow(() -> new AppException(CommonErrorCode.NOT_FOUND_USER));
    }

    @Override
    public User normalAgreeAndRegister(NormalAgreeCommand command, String userId) {
        validateConsents(command.consents());

        User guestUser = loadUserPort.findByUserId(userId)
                .orElseThrow(() -> new AppException(CommonErrorCode.NOT_FOUND_USER));

        User finalUser = guestUser.register(command.consents());

        return saveUserPort.save(finalUser);
    }

    public User oauthSignupInfo(OAuth2SignupRequest request, GuestInfo guestInfo) {
        if (loadUserPort.existsByNickname(request.getNickname())) {
            throw new AppException(CommonErrorCode.RESOURCE_CONFLICT, "이미 사용 중인 닉네임입니다.");
        }

        User guestUser = loadUserPort.findByEmail(guestInfo.email())
                .orElseThrow(() -> new AppException(CommonErrorCode.NOT_FOUND_USER));

        User updatedGuest = guestUser.updateGuestInfo(request.getNickname(), request.getBirthdate());

        saveUserPort.save(updatedGuest);

        return updatedGuest;
    }


    @Override
    public TokenResponse login(LoginCommand command) {
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
        return new TokenResponse(accessToken, refreshToken);
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

    // 프로필 이미지 업데이트
    public void updateProfileImage(Long userId, String imageUrl) {
        User user = loadUserPort.findById(userId)
                .orElseThrow(() -> new AppException(CommonErrorCode.NOT_FOUND_USER));
        user.updateProfileImage(imageUrl);
        log.info("Image URL: {}", imageUrl);

        saveUserPort.save(user);
    }


    // 계정 정보 조회
    public AccountInfoResponse getAccountInfo(Long userId) {
        UserJpaEntity user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new AppException(CommonErrorCode.NOT_FOUND_USER));
        return AccountInfoResponse.from(user);
    }


    // 비밀번호 변경
    public void changePassword(Long userId, String currentPassword, String newPassword, String newPasswordConfirm) {
        // 유저 조회
        UserJpaEntity user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new AppException(AuthErrorCode.USERID_NOT_VERIFIED));

        // 현재 비밀번호 일치 여부 확인
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new AppException(AuthErrorCode.INVALID_CURRENT_PASSWORD); // 🔹 새로 추가된 에러 코드
        }

        // 새 비밀번호 형식 검사 (선택적으로 정규식 검사 등 추가 가능)
        if (newPassword.length() < 8 || newPassword.length() > 20) {
            throw new AppException(AuthErrorCode.INVALID_PASSWORD_FORMAT);
        }

        // 새 비밀번호 확인 일치 여부 확인
        if (!newPassword.equals(newPasswordConfirm)) {
            throw new AppException(AuthErrorCode.PASSWORD_CONFIRM_NOT_MATCH);
        }

        // 기존 비밀번호와 동일한 경우 예외 처리
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new AppException(AuthErrorCode.SAME_AS_OLD_PASSWORD); // 🔹 새로 추가된 에러 코드
        }

        // 비밀번호 변경 및 저장
        user.changePassword(passwordEncoder.encode(newPassword));
        userJpaRepository.save(user);
    }


    // 회원 탈퇴
    public void withdraw(Long userId) {
        User user = loadUserPort.findById(userId)
                .orElseThrow(() -> new AppException(CommonErrorCode.NOT_FOUND_USER));

        userJpaRepository.deleteById(userId);
    }

}