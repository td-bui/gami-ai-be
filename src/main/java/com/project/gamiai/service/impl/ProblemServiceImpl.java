package com.project.gamiai.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.project.gamiai.domain.EngagementLog;
import com.project.gamiai.domain.LessonProblem;
import com.project.gamiai.domain.LessonProgress;
import com.project.gamiai.domain.Problem;
import com.project.gamiai.domain.Submission;
import com.project.gamiai.domain.TestCase;
import com.project.gamiai.domain.UserProgress;
import com.project.gamiai.dto.request.RunUserCodeRequest;
import com.project.gamiai.dto.response.ProblemDetailDto;
import com.project.gamiai.dto.response.ProblemSearchDto;
import com.project.gamiai.dto.response.SubmissionResultDto;
import com.project.gamiai.dto.response.TestCaseDto;
import com.project.gamiai.mapper.ProblemMapper;
import com.project.gamiai.repository.EngagementLogRepository;
import com.project.gamiai.repository.LessonProblemRepository;
import com.project.gamiai.repository.LessonProgressRepository;
import com.project.gamiai.repository.ProblemRepository;
import com.project.gamiai.repository.TestCaseRepository;
import com.project.gamiai.repository.TopicRepository;
import com.project.gamiai.repository.UserProgressRepository;
import com.project.gamiai.service.ProblemService;
import com.project.gamiai.service.SubmissionService;

@Service
public class ProblemServiceImpl implements ProblemService {

    @Value("${execution.server.url:http://gami-ai-exec:8000}")
    private String execApiUrl;

    @Value("${ai.agent.url:http://localhost:4000}")
    private String tunerAgentUrl;

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private TestCaseRepository testCaseRepository;

    @Autowired
    private LessonProblemRepository lessonProblemRepository;

    @Autowired
    private SubmissionService submissionService;

    @Autowired
    private ProblemMapper problemMapper;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private UserProgressRepository userProgressRepository;

    @Autowired
    private EngagementLogRepository engagementLogRepository;

    @Autowired
    private LessonProgressRepository lessonProgressRepository;

    @Autowired
    private Environment env;


    @Override
    public ProblemDetailDto findProblemById(Integer problemId) {
        // 1. Find the problem by its ID
        Problem problem = problemRepository.findById(problemId).orElse(null);
        if (problem == null) {
            return null;
        }
        // 3. Find all test cases for the problem
        List<TestCase> testCases = testCaseRepository.findByProblemIdAndIsPublicTrue(problemId);

        // 4. Map everything to the DTO
        return mapToDto(problem, testCases);
    }

    // Helper method to map entities to the DTO
    private ProblemDetailDto mapToDto(Problem problem, List<TestCase> testCases) {
        if (problem == null) return null;

        ProblemDetailDto dto = new ProblemDetailDto();
        dto.setId(problem.getId());
        dto.setTitle(problem.getTitle());
        dto.setDescription(problem.getDescription());
        dto.setDifficulty(problem.getDifficulty());
        dto.setConstraints(problem.getConstraints());
        dto.setExamples(problem.getExamples());
        dto.setStarterCode(problem.getStarterCode());
        dto.setNumberOfAccepted(problem.getNumberOfAccepted());
        dto.setNumberOfAttempts(problem.getNumberOfAttempts());
        dto.setCreatedAt(problem.getCreatedAt() != null ? problem.getCreatedAt().toString() : null);
        dto.setCreatedById(problem.getCreatedById());
        dto.setTags(problem.getTags()); // Directly from Problem entity
        dto.setIsHtml(problem.getIsHtml()); // Default to true if null

        // Map test cases
        List<TestCaseDto> testCaseDtos = testCases.stream().map(tc -> {
            TestCaseDto tcd = new TestCaseDto();
            tcd.setId(tc.getId());
            tcd.setInput(tc.getInput());
            tcd.setExpectedOutput(tc.getExpectedOutput());
            // isSample does not exist in TestCase.java, so it's not set
            return tcd;
        }).collect(Collectors.toList());
        dto.setTestCases(testCaseDtos);

        return dto;
    }

    @Override
    public ProblemDetailDto findProblemByLessonId(Integer lessonId) {
        LessonProblem lessonProblem = lessonProblemRepository.findFirstByLessonIdAndIsRequiredTrue(lessonId);
        if (lessonProblem == null) return null;
        return findProblemById(lessonProblem.getProblemId());
    }

    @Override
    public String enqueueRunUserCode(Integer problemId, RunUserCodeRequest request) {
        Problem problem = problemRepository.findById(problemId).orElse(null);
        if (problem == null || problem.getSolutionCode() == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode payload = mapper.createObjectNode();
        payload.put("userCode", request.getUserCode());
        payload.put("solutionCode", problem.getSolutionCode());
        payload.set("testCases", mapper.valueToTree(request.getTestCases()));

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(payload.toString(), headers);

        String url = execApiUrl + "/execute-problem";
        ResponseEntity<JsonNode> execResponse = restTemplate.postForEntity(url, entity, JsonNode.class);

        JsonNode body = execResponse.getBody();
        if (execResponse.getStatusCode().is2xxSuccessful() && body != null && body.has("job_id")) {
            return body.get("job_id").asText();
        }
        return null;
    }

    @Override
    public String getSolutionCodeByProblemId(Integer problemId) {
        Problem problem = problemRepository.findById(problemId).orElse(null);
        return (problem != null) ? problem.getSolutionCode() : null;
    }

    @Override
    public List<Map<String, Object>> getTestCasesByProblemId(Integer problemId) {
        List<TestCase> testCases = testCaseRepository.findByProblemId(problemId);
        return testCases.stream()
                .map(tc -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", tc.getId());
                    map.put("input", tc.getInput());
                    return map;
                })
                .collect(Collectors.toList());
    }

    @Override
    public String enqueueSubmitUserCode(Integer problemId, String userCode, Integer userId) {
        String solutionCode = getSolutionCodeByProblemId(problemId);
        List<Map<String, Object>> testCases = getTestCasesByProblemId(problemId);

        if (solutionCode == null || testCases == null || testCases.isEmpty()) {
            return null;
        }

        Map<String, Object> payload = Map.of(
                "userCode", userCode,
                "solutionCode", solutionCode,
                "testCases", testCases
        );

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        String url = execApiUrl + "/execute-problem";
        ResponseEntity<JsonNode> execResponse = restTemplate.postForEntity(url, entity, JsonNode.class);

        if (execResponse.getStatusCode().is2xxSuccessful() && execResponse.getBody() != null && execResponse.getBody().has("job_id")) {
            String jobId = execResponse.getBody().get("job_id").asText();
            submissionService.createPendingSubmission(userCode, problemId, userId, jobId);
            return jobId;
        }
        return null;
    }

    @Override
    public SubmissionResultDto getSubmitResultAndUpdateSubmission(String jobId, Integer lessonId, String authorizationHeader) {
        RestTemplate restTemplate = new RestTemplate();
        String url = execApiUrl + "/result-problem/" + jobId;
        ResponseEntity<JsonNode> execResponse = restTemplate.getForEntity(url, JsonNode.class);

        JsonNode body = execResponse.getBody();
        String status = body != null && body.has("status") ? body.get("status").asText() : "";

        if (!"finished".equals(status) && !"failed".equals(status)) {
            return null;
        }

        Submission submission = submissionService.updateSubmissionWithResult(jobId, body, authorizationHeader);
        updateProblemStatsAfterSubmission(submission, body);

        // Calculate test case stats
        int total = 0, passed = 0;
        JsonNode failedTestCase = null;
        if (body != null && body.has("results") && body.get("results").isArray()) {
            for (JsonNode res : body.get("results")) {
                total++;
                if (res.path("passed").asBoolean(false)) {
                    passed++;
                } else if (failedTestCase == null) {
                    failedTestCase = res;
                }
            }
        }

        // Only include failedTestCase if not all passed
        if (passed == total) {
            failedTestCase = null;
        }

        // --- Update LessonProgress if all testcases passed and lessonId is not null ---
        if (lessonId != null && total > 0 && passed == total && submission != null && submission.getUserId() != null) {
            LessonProgress lessonProgress = lessonProgressRepository
                .findByUserIdAndLessonId(submission.getUserId(), lessonId)
                .orElseGet(() -> {
                    LessonProgress lp = new LessonProgress();
                    lp.setUserId(submission.getUserId());
                    lp.setLessonId(lessonId);
                    lp.setCompleted(false);
                    return lp;
                });
            lessonProgress.setCompleted(true);
            lessonProgress.setCompletedAt(java.time.LocalDateTime.now());
            lessonProgressRepository.save(lessonProgress);
        }

        return new SubmissionResultDto(submission, total, passed, failedTestCase);
    }

    /**
     * Updates number_of_attempts and number_of_accepted in the problems table after a submission.
     */
    private void updateProblemStatsAfterSubmission(Submission submission, JsonNode resultBody) {
        if (submission != null && submission.getProblemId() != null) {
            Problem problem = problemRepository.findById(submission.getProblemId()).orElse(null);
            if (problem != null) {
                // Always increment attempts
                problem.setNumberOfAttempts(problem.getNumberOfAttempts() + 1);

                // If all testcases passed, increment accepted
                boolean allPassed = false;
                if (resultBody.has("results") && resultBody.get("results").isArray()) {
                    allPassed = true;
                    for (JsonNode res : resultBody.get("results")) {
                        if (!res.path("passed").asBoolean(false)) {
                            allPassed = false;
                            break;
                        }
                    }
                }
                if (allPassed) {
                    problem.setNumberOfAccepted(problem.getNumberOfAccepted() + 1);
                }
                problemRepository.save(problem);
            }
        }
    }

    @Override
    public Page<ProblemSearchDto> searchProblems(int page, int size, String name, String difficulty, String topic, String solved, Integer userId) {
        Pageable pageable = PageRequest.of(page, size);

        String nameFilter = (name != null && !name.trim().isEmpty()) ? name : null;
        String difficultyFilter = (difficulty != null && !"all".equalsIgnoreCase(difficulty)) ? difficulty : null;
        String topicFilter = (topic != null && !"all".equalsIgnoreCase(topic)) ? topic : null;
        String solvedFilter = (solved != null && !"all".equalsIgnoreCase(solved)) ? solved : null;

        return problemRepository.searchProblemsNative(
            nameFilter,
            difficultyFilter,
            topicFilter,
            solvedFilter,
            userId,
            pageable
        );
    }

    @Override
    public String processEngagementAndTuner(
            JsonNode result,
            Float timeTaken,
            String level,
            Integer userId,
            Integer problemId,
            Boolean isSubmit
    ) {
        UserProgress progress = userProgressRepository
                .findByUserIdAndProblemId(userId, problemId)
                .orElseGet(() -> {
                    UserProgress up = new UserProgress();
                    up.setUserId(userId);
                    up.setProblemId(problemId);
                    up.setAttempts(0);
                    up.setSolved(false);
                    up.setFirstSolved(null);
                    return up;
                });
        Problem problem = problemRepository.findById(problemId).orElse(null);
        int passedTestcases = 0;
        int totalTestcases = 0;
        int numActions = progress.getAttempts();
        String difficulty = problem != null ? problem.getDifficulty() : "easy";
        String proficiency = level != null ? level : "beginner";

        // Extract test case stats from results array
        if (result != null && result.has("results") && result.get("results").isArray()) {
            totalTestcases = result.get("results").size();
            for (JsonNode tc : result.get("results")) {
                if (tc.has("passed") && tc.get("passed").asBoolean(false)) {
                    passedTestcases++;
                }
            }
        }

        EngagementLog log = new EngagementLog();
        log.setUserId(userId);
        log.setProblemId(problemId);
        float expectedTime = getExpectedTimeByDifficulty(difficulty);

        log.initializeFromRaw(
            passedTestcases,
            totalTestcases > 0 ? totalTestcases : 1,
            timeTaken != null ? timeTaken : 0f,
            numActions,
            difficulty,
            proficiency,
            expectedTime // pass it here
        );

        Map<String, Object> logs = Map.of(
            "performance", log.getPerformance(),
            "time_taken", log.getTimeTaken(),
            "engagement", log.getEngagement(),
            "difficulty", log.getDifficulty(),
            "proficiency", log.getProficiency()
        );
        Map<String, Object> userActionMetrics = Map.of(
            "gain", (log.getPerformance() != null && log.getPerformance() >= 1.0f) ? 1.0 : 0.0,
            "cost", 0.1,
            "disengagement", log.getDisengagement() != null ? log.getDisengagement() : 0f
        );

        RestTemplate tunerRest = new RestTemplate();
        String tunerUrl = tunerAgentUrl + "/api/ai/tuner-step";
        Map<String, Object> tunerRequest = Map.of(
            "logs", logs,
            "user_action_metrics", userActionMetrics
        );
        var tunerResponse = tunerRest.postForEntity(tunerUrl, tunerRequest, Map.class);

        Map<String, Object> tunerResult = tunerResponse.getBody();
        if (tunerResult != null) {
            log.setAction((String) tunerResult.get("action"));
            if (tunerResult.get("logs") instanceof Map) {
                Map logsMap = (Map) tunerResult.get("logs");
                if (logsMap.get("reward") instanceof Number) {
                    log.setReward(((Number) logsMap.get("reward")).floatValue());
                }
            }
        }
        if (isSubmit){
            // If all testcases passed, mark as solved and set firstSolved if not already set
            boolean allPassed = (log.getPerformance() != null && log.getPerformance() >= 1.0f);
            if (allPassed) {
                progress.setSolved(true);
                if (progress.getFirstSolved() == null) {
                    progress.setFirstSolved(java.time.LocalDateTime.now());
                }
            }    
        }
        progress.setLastAttempted(java.time.LocalDateTime.now());
        progress.setAttempts(progress.getAttempts() == null ? 1 : progress.getAttempts() + 1);
        userProgressRepository.save(progress);

        // Save EngagementLog to DB
        engagementLogRepository.save(log);

        return log.getAction();
    }

    private float getExpectedTimeByDifficulty(String rawDifficulty) {
        String key = "engagement.expected-time." + (rawDifficulty == null ? "easy" : rawDifficulty.trim().toLowerCase());
        String value = env.getProperty(key, "15");
        try {
            return Float.parseFloat(value) * 60; // convert minutes to seconds if needed
        } catch (NumberFormatException e) {
            return 15 * 60f;
        }
    }
}