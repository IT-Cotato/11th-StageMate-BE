package com.example.stagemate.service.community;

import com.example.stagemate.repository.community.CommunityScrapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CommunityScrapService {
    private final CommunityScrapRepository communityScrapRepository;

    public List<Long> getScrappedPostIdsByUser(Long userId) {
        return communityScrapRepository.findPostIdsByUserId(userId);
    }

}
