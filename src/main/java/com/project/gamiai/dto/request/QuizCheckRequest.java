package com.project.gamiai.dto.request;

import java.util.Map;

import lombok.Data;

@Data
public class QuizCheckRequest {
    private Integer lessonId;
    private Integer userId;
    private Map<Integer, String> answers; // quizId -> answer
}