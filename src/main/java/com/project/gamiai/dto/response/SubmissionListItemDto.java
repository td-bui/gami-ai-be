package com.project.gamiai.dto.response;

import lombok.Data;

@Data
public class SubmissionListItemDto {
    private Integer id;
    private String status;
    private String language;
    private Float runtime;
    private Float memory;

    // getters and setters
}