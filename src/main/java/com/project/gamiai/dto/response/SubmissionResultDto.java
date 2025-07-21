package com.project.gamiai.dto.response;

import com.fasterxml.jackson.databind.JsonNode;
import com.project.gamiai.domain.Submission;

import lombok.Data;
@Data
public class SubmissionResultDto {
    private Submission submission;
    private int totalTestCases;
    private int passedTestCases;
    private JsonNode failedTestCase; // null if accepted

    public SubmissionResultDto(Submission submission, int totalTestCases, int passedTestCases, JsonNode failedTestCase) {
        this.submission = submission;
        this.totalTestCases = totalTestCases;
        this.passedTestCases = passedTestCases;
        this.failedTestCase = failedTestCase;
    }
}