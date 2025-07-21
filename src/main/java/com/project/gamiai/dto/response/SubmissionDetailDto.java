package com.project.gamiai.dto.response;

import lombok.Data;

@Data
public class SubmissionDetailDto {
    private Integer id;
    private String code;
    private String language;
    private Float runtime;
    private Float memory;
    private String status;
    private String submittedAt;
    private String feedback; // feedback from the AI or system
    // add more fields as needed

    // getters and setters
}