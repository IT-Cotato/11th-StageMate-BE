package com.example.stagemate.service.chat;

import com.example.stagemate.domain.chat.ChatRoom;
import com.example.stagemate.dto.response.chat.ChatRoomResponse;
import com.example.stagemate.global.dto.PagedResponse;
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

    public PagedResponse<ChatRoomResponse> getChatRooms(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<ChatRoom> chatRooms = chatRoomRepository.findChatRoomsOrderByEndDateAsc(pageable);

        return PagedResponse.from(chatRooms.getContent().stream().map(ChatRoomResponse::from).toList(), chatRooms);
    }

}
