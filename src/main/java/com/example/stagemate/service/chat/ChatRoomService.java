package com.example.stagemate.service.chat;

import com.example.stagemate.domain.chat.ChatRoom;
import com.example.stagemate.domain.performance.Performance;
import com.example.stagemate.dto.response.chat.ChatRoomResponse;
import com.example.stagemate.global.dto.PagedResponse;
import com.example.stagemate.repository.chat.ChatRoomRepository;
import com.example.stagemate.repository.performance.PerformanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final PerformanceRepository performanceRepository;

    public PagedResponse<ChatRoomResponse> getChatRooms(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<ChatRoom> chatRooms = chatRoomRepository.findChatRoomsOrderByEndDateAsc(pageable);

        return PagedResponse.from(chatRooms.getContent().stream().map(ChatRoomResponse::from).toList(), chatRooms);
    }

    public void createChatRoomOrDeleteChatRoomBasedOnCurrentDate() {
        List<Performance> performances = performanceRepository.findAllByStartDateAfter(LocalDate.now(ZoneId.of("Asia/Seoul")));
        for (Performance performance : performances) {
            //공연 시작일자가 넘었는데도 채팅방이 존재하지않으면 생성
            createChatRoomIfNotExist(performance.getId());
        }

        List<ChatRoom> chatRooms = chatRoomRepository.findAll();
        for (ChatRoom chatRoom : chatRooms) {
            //채팅방의 완료일자가 넘었는데도 채팅방이 존재하면 삭제
            deleteChatRoomIfEndDateAfter(chatRoom.getId());
        }

    }

    private void createChatRoomIfNotExist(Long performanceId) {
        boolean isExist = chatRoomRepository.existsByPerformanceId(performanceId);
        if (!isExist) {
            Performance performance = performanceRepository.findById(performanceId).get();

            ChatRoom chatRoom = ChatRoom.builder()
                    .title(performance.getPerformanceName() + " 채팅방")
                    .startDate(performance.getStartDate())
                    .endDate(performance.getEndDate())
                    .performance(performance)
                    .build();
            saveChatRoom(chatRoom);
        }
    }

    private void deleteChatRoomIfEndDateAfter(Long chatRoomId) {
        Optional<ChatRoom> chatRoom = chatRoomRepository.findById(chatRoomId);

        if (chatRoom.isPresent() && chatRoom.get().getEndDate().isAfter(LocalDate.now(ZoneId.of("Asia/Seoul")))) {
            deleteChatRoom(chatRoomId);
        }
    }

    private void saveChatRoom(ChatRoom chatRoom) {
        chatRoomRepository.save(chatRoom);
    }

    private void deleteChatRoom(Long chatRoomId) {
        chatRoomRepository.deleteById(chatRoomId);
    }

}
