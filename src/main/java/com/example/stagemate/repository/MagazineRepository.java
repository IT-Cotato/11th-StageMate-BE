package com.example.stagemate.repository;

import com.example.stagemate.domain.magazine.Magazine;
import com.example.stagemate.domain.user.entity.UserJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface MagazineRepository extends JpaRepository<Magazine, Long> {
    List<Magazine> findAllByOrderByCreatedAtDesc();
    Page<Magazine> findAllByOrderByCreatedAtDesc(Pageable pageable);
    @Query("""
    SELECT m
    FROM Magazine m
    ORDER BY (m.likeCount + m.scrapCount) DESC, m.createdAt DESC
""")
    List<Magazine> findTop4ByLikesAndScrapsSum(Pageable pageable);

}
