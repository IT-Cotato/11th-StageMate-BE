package com.example.stagemate.domain.search;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDate;

@Document(indexName = "search")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchDocument {
    @Id
    private String id;
    private String type; // "performance", "community", "chatroom"
    private String title;
    private String content; // community 에만 존재
    private String genre; // performance/chatroom 에만 존재

    @Field(type = FieldType.Date, format = DateFormat.date)
    private LocalDate startDate; // performance 에만 존재
    @Field(type = FieldType.Date, format = DateFormat.date)
    private LocalDate endDate; // performance 에만 존재
}

