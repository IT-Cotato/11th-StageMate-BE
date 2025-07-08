package com.example.stagemate.repository;

import com.example.stagemate.domain.magazine.Magazine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface MagazineRepository extends JpaRepository<Magazine, Long> {
    List<Magazine> findAllByOrderByCreatedAtDesc();
    Page<Magazine> findAllByOrderByCreatedAtDesc(Pageable pageable);
    @Query("""
        SELECT m
        FROM Magazine m
        LEFT JOIN m.likes l
        LEFT JOIN m.scraps s
        GROUP BY m
        ORDER BY COUNT(l) + COUNT(s) DESC, m.createdAt DESC
    """)
    List<Magazine> findTop4ByLikesAndScrapsSum(Pageable pageable);

}
