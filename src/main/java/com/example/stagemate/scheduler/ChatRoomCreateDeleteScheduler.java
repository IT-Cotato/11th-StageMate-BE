package com.example.stagemate.scheduler;

import com.example.stagemate.service.chat.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ChatRoomCreateDeleteScheduler {
    private final ChatRoomService chatRoomService;

    @Scheduled(cron = "0 0 0 * * *")
    public void CreateOrDeleteChatRoom() {
        //채팅방 생성/채팅방 삭제
        //공연시작일자가 넘으면 채팅방 생성
        //공연종료일자가 넘었는데 채팅방이 존재하면 채팅방 삭제
        chatRoomService.createChatRoomOrDeleteChatRoomBasedOnCurrentDate();
    }


}
