package com.project.gamiai.dto.response;

import com.fasterxml.jackson.databind.JsonNode;
import com.project.gamiai.domain.Submission;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubmissionResultDto {
    private Submission submission;
    private int totalTestCases;
    private int passedTestCases;
    private JsonNode failedTestCase;
    private int xpGained; // New field

    public SubmissionResultDto(Submission submission, int totalTestCases, int passedTestCases, JsonNode failedTestCase, int xpGained) {
        this.submission = submission;
        this.totalTestCases = totalTestCases;
        this.passedTestCases = passedTestCases;
        this.failedTestCase = failedTestCase;
        this.xpGained = xpGained; // Update constructor
    }
}