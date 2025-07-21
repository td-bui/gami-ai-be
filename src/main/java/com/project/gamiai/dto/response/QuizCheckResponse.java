package com.project.gamiai.dto.response;

import java.util.List;

import lombok.Data;

@Data
public class QuizCheckResponse {
    private boolean allCorrect;
    private List<Integer> wrongQuizIds;
    private boolean isCompleted = false;
}