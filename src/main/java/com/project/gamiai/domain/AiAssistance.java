package com.project.gamiai.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ai_assistance")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiAssistance {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer userId;
    private Integer problemId;
    private Integer lessonId;
    private String sessionId; 
    private String suggestionType;
    @Column(columnDefinition = "text")
    private String aiResponse;
    @Column(columnDefinition = "text")
    private String userQuery;
    @Column(columnDefinition = "text")
    private String userCode;
    private LocalDateTime dateTime;
}