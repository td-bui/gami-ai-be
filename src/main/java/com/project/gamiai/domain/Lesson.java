package com.project.gamiai.domain;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "lessons")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Lesson {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer moduleId;
    @Column(columnDefinition = "text")
    private String title;
    @Column(columnDefinition = "text")
    private String content;
    @ElementCollection
    private List<String> tags;
    private String difficulty;
    private LocalDateTime createdAt;
    private Integer createdById;
    private Integer lessonOrder;
    private Boolean isActive = true;
    private Boolean isSubLesson = false;
    private Integer parentLessonId; // For sub-lessons, this will reference the parent lesson's ID
    private Boolean onlyQuiz = true;
}