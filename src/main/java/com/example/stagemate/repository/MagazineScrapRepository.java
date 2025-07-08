package com.example.stagemate.repository;

import com.example.stagemate.domain.magazine.MagazineScrap;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MagazineScrapRepository extends JpaRepository<MagazineScrap, Long> {
    boolean existsByUserIdAndMagazineId(Long userId, Long magazineId);
    void deleteByUserIdAndMagazineId(Long userId, Long magazineId);
}