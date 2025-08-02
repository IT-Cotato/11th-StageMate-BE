package com.example.stagemate.service.chat;

import com.example.stagemate.domain.chat.ChatRoom;
import com.example.stagemate.dto.response.ChatRoomResponse;
import com.example.stagemate.repository.chat.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;

    public Page<ChatRoomResponse> getChatRooms(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<ChatRoom> chatRooms = chatRoomRepository.findChatRoomsOrderByEndDateAsc(pageable);

        return chatRooms.map(ChatRoomResponse::from);
    }

}
