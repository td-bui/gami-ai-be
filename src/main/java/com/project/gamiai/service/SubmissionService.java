package com.project.gamiai.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.project.gamiai.domain.Submission;
import com.project.gamiai.dto.response.SubmissionDetailDto;
import com.project.gamiai.dto.response.SubmissionListItemDto;

@Service
public interface SubmissionService {
    void createPendingSubmission(String code, Integer problemId, Integer userId, String jobId);
    Submission updateSubmissionWithResult(String jobId, JsonNode resultBody, String authorizationHeader);
    List<SubmissionListItemDto> getSubmissionList(Integer userId, Integer problemId);
    SubmissionDetailDto getSubmissionDetail(Integer submissionId);
    boolean hasAcceptedSubmission(Integer userId, Integer problemId);
}
