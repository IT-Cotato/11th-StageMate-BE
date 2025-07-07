package com.example.stagemate.domain.user.port.out;

import com.example.stagemate.domain.user.User;

public interface SaveUserPort {
    User save(User user);
}