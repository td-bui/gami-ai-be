package com.project.gamiai.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.fasterxml.jackson.databind.JsonNode;
import com.project.gamiai.dto.request.RunUserCodeRequest;
import com.project.gamiai.dto.response.ProblemDetailDto;
import com.project.gamiai.dto.response.ProblemSearchDto;
import com.project.gamiai.dto.response.SubmissionResultDto;

public interface ProblemService {
    ProblemDetailDto findProblemById(Integer problemId);
    ProblemDetailDto findProblemByLessonId(Integer lessonId);
    String enqueueRunUserCode(Integer problemId, RunUserCodeRequest request);
    String enqueueSubmitUserCode(Integer problemId, String userCode, Integer userId);
    SubmissionResultDto getSubmitResultAndUpdateSubmission(String jobId, Integer lessonId, String authorizationHeader);
    String getSolutionCodeByProblemId(Integer problemId);
    List<Map<String, Object>> getTestCasesByProblemId(Integer problemId);
    Page<ProblemSearchDto> searchProblems(int page, int size, String name, String difficulty, String topic, String solved, Integer userId);

    String processEngagementAndTuner(
        JsonNode result,
        Float timeTaken,
        String level,
        Integer userId,
        Integer problemId,
        Boolean isSubmit
    );
}