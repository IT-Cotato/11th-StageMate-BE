package com.example.stagemate.repository.chat;

import com.example.stagemate.domain.chat.Chat;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatRepository extends MongoRepository<Chat, String> {
    // 최신 메시지 N개 조회
    List<Chat> findTopByRoomIdOrderByCreatedAtDesc(Long roomId, Pageable pageable);

    // cursor 이전 메시지 조회
    List<Chat> findByRoomIdAndCreatedAtBeforeOrderByCreatedAtDesc(
            Long roomId, LocalDateTime before, Pageable pageable);


}
