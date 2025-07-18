package com.example.stagemate.service.community;

import com.example.stagemate.repository.community.CommunityLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CommunityLikeService {
    private final CommunityLikeRepository communityLikeRepository;

    public List<Long> getLikedPostIdsByUser(Long userId) {
        return communityLikeRepository.findPostIdsByUserId(userId);
    }

}
