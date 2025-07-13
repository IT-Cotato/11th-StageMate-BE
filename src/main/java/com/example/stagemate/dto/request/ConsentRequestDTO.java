package com.example.stagemate.dto.request;

import com.example.stagemate.domain.user.model.ConsentType;
import com.example.stagemate.dto.auth.GuestInfo;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

import java.util.List;

@Getter
public class ConsentRequestDTO {

    @NotEmpty
    private List<ConsentType> consents;

    @NotEmpty
    private String userId;

    private GuestInfo guestInfo;
}
