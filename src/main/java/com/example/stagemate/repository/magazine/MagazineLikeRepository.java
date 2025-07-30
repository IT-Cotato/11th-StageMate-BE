package com.example.stagemate.repository.magazine;

import com.example.stagemate.domain.magazine.MagazineLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MagazineLikeRepository extends JpaRepository<MagazineLike, Long> {
    boolean existsByUserIdAndMagazineId(Long userId, Long magazineId);
    void deleteByUserIdAndMagazineId(Long userId, Long magazineId);
}