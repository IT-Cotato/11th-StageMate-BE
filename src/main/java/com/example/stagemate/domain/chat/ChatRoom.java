package com.example.stagemate.domain.chat;

import com.example.stagemate.domain.performance.Performance;
import jakarta.persistence.*;
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
@Table(name = "chat_rooms")
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @jakarta.persistence.Column(name = "chat_room_id")
    private Long id;

    private String title;

    private LocalDate startDate;

    private LocalDate endDate;

    @OneToOne
    @JoinColumn(name = "performance_id")
    private Performance performance;

}
