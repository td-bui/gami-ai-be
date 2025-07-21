package com.project.gamiai.dto.response;

import java.util.List;

import lombok.Data;

@Data
public class LessonQuizDto {
    private Integer id;
    private String question;
    private List<String> options;
}