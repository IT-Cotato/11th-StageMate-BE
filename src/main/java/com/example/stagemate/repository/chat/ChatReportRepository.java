package com.example.stagemate.repository.chat;

import com.example.stagemate.domain.chat.ChatReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatReportRepository extends JpaRepository<ChatReport, Long> {
    //existsByReporterIdAndChatId
    boolean existsByReporterIdAndChatId(Long reporterId, String chatId);
}
