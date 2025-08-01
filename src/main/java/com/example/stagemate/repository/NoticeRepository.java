package com.example.stagemate.repository;

import com.example.stagemate.domain.mypage.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice,Long> {
}
