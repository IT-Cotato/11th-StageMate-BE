package com.example.stagemate.repository.chat;

import com.example.stagemate.domain.chat.Chat;
import com.example.stagemate.domain.chat.ChatReport;
import com.example.stagemate.dto.response.chat.ChatReportCountResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatReportRepository extends JpaRepository<ChatReport, Long> {
    //existsByReporterIdAndChatId
    boolean existsByReporterIdAndChatId(Long reporterId, String chatId);

    //유저별 채팅 신고 횟수 userIds를 파라미터로
    @Query("""
    SELECT new com.example.stagemate.dto.response.chat.ChatReportCountResponse(
        cr.reporter.id, COUNT(cr)
    )
    FROM ChatReport cr
    WHERE cr.reporter.id IN :userIds
    GROUP BY cr.reporter.id
    """)
    List<ChatReportCountResponse> getChatReportCount(@Param("userIds") List<Long> userIds);

}
