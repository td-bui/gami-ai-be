package com.project.gamiai.dto.response;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class SolvedProblemDto {
    private String title;
    private String difficulty;
    private String status;
    private Float runtime;
    private LocalDateTime submittedAt;
}