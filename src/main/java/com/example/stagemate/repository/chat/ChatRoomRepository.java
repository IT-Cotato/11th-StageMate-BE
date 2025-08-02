package com.example.stagemate.repository.chat;

import com.example.stagemate.domain.chat.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    //endDate asc 정렬 쿼리
    @Query("SELECT c FROM ChatRoom c ORDER BY c.endDate ASC")
    Page<ChatRoom> findChatRoomsOrderByEndDateAsc(Pageable pageable);
}
