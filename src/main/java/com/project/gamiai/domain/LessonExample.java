package com.project.gamiai.domain;

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
@Table(name = "lesson_examples")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LessonExample {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer lessonId;
    @Column(columnDefinition = "text")
    private String code;
    @Column(columnDefinition = "text")
    private String executableCode;
    private String codeId;
    @Column(columnDefinition = "text")
    private String description;
    @Column(columnDefinition = "text")
    private String output;
    @Column(columnDefinition = "text")
    private String explaination;
}