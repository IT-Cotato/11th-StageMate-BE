package com.example.stagemate.domain.search;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Document;

import java.time.LocalDate;

@Document(indexName = "search")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchDocument {
    @Id
    private String id;
    private String type; // "performance", "post", "chatroom"
    private String title;
    private String content;
    private String genre; // performance/chatroom 에만 존재
    private LocalDate date;
}

