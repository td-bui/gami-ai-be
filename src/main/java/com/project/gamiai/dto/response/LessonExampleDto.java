package com.project.gamiai.dto.response;

import lombok.Data;

@Data
public class LessonExampleDto {
    private Integer id;
    private String codeId;
    private String code;
    private String description;
    private String explaination;
    private String output;
}