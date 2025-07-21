package com.project.gamiai.dto.response;

import java.util.List;

import lombok.Data;

@Data
public class ProblemDetailDto {
    private Integer id;
    private String title;
    private String description;
    private String difficulty;
    private String constraints;
    private String examples;
    private String starterCode;
    private Integer numberOfAccepted;
    private Integer numberOfAttempts;
    private String createdAt;
    private Integer createdById;
    private List<String> tags;
    private List<TestCaseDto> testCases;
    private Boolean isHtml;
}