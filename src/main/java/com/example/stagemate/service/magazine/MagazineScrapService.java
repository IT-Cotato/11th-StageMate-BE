package com.example.stagemate.service.magazine;

import com.example.stagemate.repository.magazine.MagazineScrapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MagazineScrapService {
    private final MagazineScrapRepository magazineScrapRepository;

    public List<Long> getScrappedMagazineIdsByUser(Long userId) {
        return magazineScrapRepository.findMagazineIdsByUserId(userId);
    }
}
