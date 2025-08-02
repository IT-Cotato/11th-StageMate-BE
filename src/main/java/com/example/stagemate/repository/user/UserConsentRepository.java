package com.example.stagemate.repository.user;

import com.example.stagemate.domain.user.model.UserConsent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserConsentRepository extends JpaRepository<UserConsent, Long> {
}
