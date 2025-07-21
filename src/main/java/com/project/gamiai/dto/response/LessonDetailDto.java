package com.project.gamiai.dto.response;

import java.util.List;

import lombok.Data;

@Data
public class LessonDetailDto {
    private Integer id;
    private String title;
    private String content;
    private String difficulty;
    private Integer moduleId;
    private List<LessonExampleDto> examples;
}