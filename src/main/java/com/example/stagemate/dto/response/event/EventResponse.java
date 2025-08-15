package com.example.stagemate.dto.response.event;

import com.example.stagemate.domain.event.Event;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public record EventResponse(
         Long id,
         String type,
         String title,
         String content,
         String genre,
         LocalDate startDate,
         LocalDate endDate,
         LocalDate createdAt,
         boolean isUsed,
         String eventType,
         Long targetId //performanceId, communityPostId
) {
    public static EventResponse from(Event event) {
        if (event == null) return null;
        return new EventResponse(
                event.getId(),
                event.getType(),
                event.getTitle(),
                event.getContent(),
                event.getGenre(),
                event.getStartDate(),
                event.getEndDate(),
                event.getCreatedAt(),
                event.isUsed(),
                event.getEventType(),
                event.getTargetId()
        );
    }



}
