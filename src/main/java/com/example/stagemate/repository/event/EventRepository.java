package com.example.stagemate.repository.event;

import com.example.stagemate.domain.event.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    //findAllByIsUsed
    //    private boolean isUsed; //사용된 이벤트인지 확인
    List<Event> findAllByIsUsed(boolean isUsed);
}
