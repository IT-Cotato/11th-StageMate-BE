package com.example.stagemate.service.user;

import com.example.stagemate.domain.user.User;
import com.example.stagemate.domain.user.entity.UserJpaEntity;
import com.example.stagemate.domain.user.model.ConsentType;
import com.example.stagemate.domain.user.model.UserConsent;
import com.example.stagemate.dto.request.RegisterUserRequestDTO;
import com.example.stagemate.global.exception.AppException;
import com.example.stagemate.global.exception.CommonErrorCode;
import com.example.stagemate.global.util.SignUpConsentTempStore;
import com.example.stagemate.repository.user.UserConsentRepository;
import com.example.stagemate.repository.user.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ConsentService {

    private final UserJpaRepository userJpaRepository;
    private final UserConsentRepository userConsentRepository;

    public void saveAll(User user, Map<ConsentType, Boolean> consents)
 {
        // userId로 UserJpaEntity를 조회
        UserJpaEntity userEntity = userJpaRepository.findByUserId(user.getUserId())
                .orElseThrow(() -> new AppException(CommonErrorCode.NOT_FOUND_USER));

        List<UserConsent> consentList = new ArrayList<>();

     for (Map.Entry<ConsentType, Boolean> entry : consents.entrySet()) {
         ConsentType type = entry.getKey();
         Boolean agreed = entry.getValue();

         UserConsent consent = UserConsent.builder()
                 .user(userEntity)
                 .consentType(type)
                 .agreed(agreed)
                 .build();

         consentList.add(consent);
     }

        userConsentRepository.saveAll(consentList);
    }
}
