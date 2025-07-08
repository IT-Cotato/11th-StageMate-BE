package com.example.stagemate.repository;

import com.example.stagemate.domain.magazine.Magazine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface MagazineRepository extends JpaRepository<Magazine, Long> {
    List<Magazine> findAllByOrderByCreatedAtDesc();
    Page<Magazine> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
