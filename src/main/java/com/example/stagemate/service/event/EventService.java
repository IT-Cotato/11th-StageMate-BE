package com.example.stagemate.service.event;

import com.example.stagemate.domain.event.Event;
import com.example.stagemate.dto.response.event.EventResponse;
import com.example.stagemate.repository.event.EventRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EventService {
    private final EventRepository eventRepository;

    //사용하지않은 이벤트 리턴
    public List<EventResponse> getNotUsedEvents() {
        return eventRepository.findAllByIsUsed(false).stream().map(EventResponse::from).toList();
    }

    //사용한 이벤트 used 처리
    public void markEventIsUsed(List<Long> eventIds) {
        List<Event> events = eventRepository.findAllById(eventIds);
        events.forEach(Event::markEventIsUsed);
    }

    //새로운 이벤트 저장
    public void saveEvent(Event event) {
        eventRepository.save(event);
    }
}
