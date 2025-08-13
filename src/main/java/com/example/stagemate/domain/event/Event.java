package com.example.stagemate.domain.event;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    private Long targetId; //performanceId, communityId

    private String type; // performance, community

    private String title;

    private String content; //community에만 존재

    private String genre;

    private boolean isUsed; //사용된 이벤트인지 확인

    private LocalDate startDate; // performance 에만 존재

    private LocalDate endDate; // performance 에만 존재

    private LocalDate createdAt; // community 에만 존재

    private String eventType; //이벤트 타입은 생성, 삭제

    public void markEventIsUsed() {
        this.isUsed = true;
    }
}
