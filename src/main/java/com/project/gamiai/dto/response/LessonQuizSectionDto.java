package com.project.gamiai.dto.response;

import java.util.List;

import lombok.Data;

@Data
public class LessonQuizSectionDto {
    private Boolean completed;
    private Boolean quizCompleted;
    private List<LessonQuizDto> quizzes;
}