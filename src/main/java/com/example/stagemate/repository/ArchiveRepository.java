package com.example.stagemate.repository;

import com.example.stagemate.domain.archive.Archive;
import com.example.stagemate.domain.user.entity.UserJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ArchiveRepository extends JpaRepository<Archive, Long> {

    // 월별 공연 평점 순위를 가져오는 쿼리
    @Query("SELECT a FROM Archive a WHERE a.viewingDate BETWEEN :startDate AND :endDate ORDER BY a.rating DESC")
    Page<Archive> findTopRatedArchives(@Param("startDate") LocalDate startDate,
                                       @Param("endDate") LocalDate endDate,
                                       Pageable pageable);


    @Query("SELECT a FROM Archive a WHERE a.viewingDate BETWEEN :startDate AND :endDate")
    List<Archive> findArchives(@Param("startDate") LocalDate startDate,
                               @Param("endDate") LocalDate endDate);


    List<Archive> findByUserAndViewingDateBetween(UserJpaEntity user, LocalDate startDate, LocalDate endDate);


}
