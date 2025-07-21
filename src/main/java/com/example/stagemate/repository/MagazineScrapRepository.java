package com.example.stagemate.repository;

import com.example.stagemate.domain.magazine.Magazine;
import com.example.stagemate.domain.magazine.MagazineScrap;
import com.example.stagemate.domain.user.entity.UserJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MagazineScrapRepository extends JpaRepository<MagazineScrap, Long> {
    boolean existsByUserIdAndMagazineId(Long userId, Long magazineId);
    void deleteByUserIdAndMagazineId(Long userId, Long magazineId);

    @Query("SELECT ms.magazine FROM MagazineScrap ms WHERE ms.user.id = :userId ORDER BY ms.id DESC")
    Page<Magazine> findScrappedMagazinesByUser(Long userId, Pageable pageable);

    @Query("SELECT ms.magazine.id FROM MagazineScrap ms WHERE ms.user.id = :userId ORDER BY ms.id DESC")
    List<Long> findMagazineIdsByUserId(Long userId);
}