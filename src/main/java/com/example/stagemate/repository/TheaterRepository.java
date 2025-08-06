package com.example.stagemate.repository;

import com.example.stagemate.domain.theater.Theater;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TheaterRepository extends JpaRepository<Theater, Long> {
    Optional<Theater> findByName(String name);

    //        List<Theater> theaters = theaterRepository.findByRegion(region, pageable);
    Page<Theater> findByRegion(String region, Pageable pageable);
}
