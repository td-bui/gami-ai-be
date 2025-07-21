package com.project.gamiai.dto.response;

import lombok.Data;

@Data
public class TestCaseDto {
    private Integer id;
    private String input;
    private String expectedOutput;
    private Boolean isSample;
}