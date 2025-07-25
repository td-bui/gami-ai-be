package com.project.gamiai.dto.response;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CompletedLessonDto {
    private String title;
    private String difficulty;
    private LocalDateTime completedAt;
}
