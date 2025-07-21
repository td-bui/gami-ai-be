package com.project.gamiai.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.project.gamiai.dto.request.RunUserCodeRequest;
import com.project.gamiai.dto.response.ProblemDetailDto;
import com.project.gamiai.dto.response.SubmissionResultDto;
import com.project.gamiai.service.ProblemService;

@RestController
@RequestMapping("/api/problems")
public class ProblemController {

    @Autowired
    private ProblemService problemService;

    @Value("${execution.server.url:http://gami-ai-exec:8000}")
    private String execApiUrl;

    // Load a problem by lessonId (returns the first problem found for the lesson)
    @GetMapping("/by-lesson/{lessonId}")
    public ProblemDetailDto getProblemByLessonId(@PathVariable Integer lessonId) {
        return problemService.findProblemByLessonId(lessonId);
    }

    // Load a problem by problemId
    @GetMapping("/{problemId}")
    public ProblemDetailDto getProblemById(@PathVariable Integer problemId) {
        return problemService.findProblemById(problemId);
    }

    // Run user code: returns only job_id
    @PostMapping("/{problemId}/run-user-code")
    public ResponseEntity<?> runUserCode(
            @PathVariable Integer problemId,
            @RequestBody RunUserCodeRequest request
    ) {
        String jobId = problemService.enqueueRunUserCode(problemId, request);
        if (jobId == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Solution code not found for problem " + problemId);
        }
        return ResponseEntity.ok(Map.of("job_id", jobId));
    }

    // Submit user code: test cases and solution code loaded from DB, returns job_id
    @PostMapping("/{problemId}/submit")
    public ResponseEntity<?> submitUserCode(
            @PathVariable Integer problemId,
            @RequestBody Map<String, Object> request,
            @RequestHeader("user-id") Integer userId
    ) {
        String userCode = (String) request.get("userCode");

        String jobId = problemService.enqueueSubmitUserCode(problemId, userCode, userId);
        if (jobId == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Problem or test cases not found for problem " + problemId);
        }
        return ResponseEntity.ok(Map.of("job_id", jobId));
    }

    // --- Modified result-problem endpoint ---
    @GetMapping("/result-problem/{jobId}")
    public ResponseEntity<?> getResultProblem(
            @PathVariable String jobId,
            @RequestParam(value = "timeTaken", required = false) Float timeTaken,
            @RequestHeader(value = "level", required = false) String level,
            @RequestHeader(value = "user-id", required = false) Integer userId,
            @RequestHeader(value = "problem-id", required = false) Integer problemId
    ) {
        RestTemplate restTemplate = new RestTemplate();
        String url = execApiUrl + "/result-problem/" + jobId;
        ResponseEntity<JsonNode> execResponse = restTemplate.getForEntity(url, JsonNode.class);
        JsonNode result = execResponse.getBody();

        String action = problemService.processEngagementAndTuner(
            result, timeTaken, level, userId, problemId, false
        );
        // String action = "show_motivation"; // Temporary action for testing

        Map<String, Object> response = Map.of(
            "result", result,
            "action", action
        );
        return ResponseEntity.status(execResponse.getStatusCode().value()).body(response);
    }

    // --- Modified result-submit endpoint ---
    @GetMapping("/result-submit/{jobId}")
    public ResponseEntity<?> getSubmitResult(
            @PathVariable String jobId,
            @RequestParam(value = "timeTaken", required = false) Float timeTaken,
            @RequestHeader(value = "level", required = false) String level,
            @RequestHeader(value = "user-id", required = false) Integer userId,
            @RequestHeader(value = "problem-id", required = false) Integer problemId,
            @RequestHeader(value = "lesson-id", required = false) Integer lessonId,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader
    ) {
        SubmissionResultDto body = problemService.getSubmitResultAndUpdateSubmission(jobId, lessonId, authorizationHeader);

        JsonNode result = null;
        try {
            result = new com.fasterxml.jackson.databind.ObjectMapper().convertValue(body, JsonNode.class);
        } catch (Exception e) {
            // fallback: result remains null
        }

        String action = problemService.processEngagementAndTuner(
            result, timeTaken, level, userId, problemId, true
        );
        // String action = "give_hint"; // Temporary action for testing

        Map<String, Object> response = Map.of(
            "result", body,
            "action", action
        );
        return ResponseEntity.ok(response);
    }

    // Search problems with pagination and filters
    @GetMapping("/search")
    public ResponseEntity<?> searchProblems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String topic,
            @RequestParam(required = false) String solved, // "all", "solved", "unsolved"
            @RequestHeader("user-id") Integer userId
    ) {
        return ResponseEntity.ok(
            problemService.searchProblems(page, size, name, difficulty, topic, solved, userId)
        );
    }
}