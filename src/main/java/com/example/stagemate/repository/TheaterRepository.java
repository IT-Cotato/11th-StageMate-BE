package com.example.stagemate.repository;

import com.example.stagemate.domain.theater.Theater;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TheaterRepository extends JpaRepository<Theater, Long> {
    Optional<Theater> findByName(String name);
    List<Theater> findByRegion(String region);
}
