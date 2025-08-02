package com.example.stagemate.service.chat;

import com.example.stagemate.domain.chat.Chat;
import com.example.stagemate.domain.chat.ChatErrorCode;
import com.example.stagemate.domain.community.ReportReason;
import com.example.stagemate.domain.community.TargetType;
import com.example.stagemate.domain.user.entity.UserJpaEntity;
import com.example.stagemate.dto.request.chat.ChatRequest;
import com.example.stagemate.dto.response.ChatResponse;
import com.example.stagemate.global.exception.AppException;
import com.example.stagemate.redis.RedisMessagePublisher;
import com.example.stagemate.repository.chat.ChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

import static com.example.stagemate.global.exception.community.CommunityErrorCode.COMMUNITY_REPORT_ALREADY_EXISTS;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ChatService {
    private final RedisMessagePublisher redisMessagePublisher;
    private final ChatRepository chatRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String MESSAGE_CHANNEL = "chat";

    public void sendGroupChat(ChatRequest chatRequest) {

        //몽고DB에 저장
        chatRepository.save(chatRequest.toEntity());

        //메세지 구독자에게 publish
        redisMessagePublisher.publish(chatRequest);
    }

    public List<ChatResponse> getPreviousMessages(
            Long roomId, String cursorId, int limit) {

        Pageable pageable = PageRequest.of(0, limit);
        List<Chat> chats;

        log.info("cursorId: {}", cursorId);

        if (cursorId == null) {
            // 처음 진입: 최신 메시지부터
            chats = chatRepository.findTopByRoomIdOrderByCreatedAtDesc(roomId, pageable);
            log.info("chats: {}", chats);
        } else {
            Chat cursorMessage = chatRepository.findById(cursorId)
                    .orElseThrow(() -> new RuntimeException("Cursor 메시지를 찾을 수 없습니다."));
            chats = chatRepository.findByRoomIdAndCreatedAtBeforeOrderByCreatedAtDesc(
                    roomId, cursorMessage.getCreatedAt(), pageable);
        }

        // 최신순으로 가져왔으니 → 오래된 순으로 reverse
        return chats.stream()
                .sorted(Comparator.comparing(Chat::getCreatedAt))
                .map(ChatResponse::from)
                .toList();
    }





}
