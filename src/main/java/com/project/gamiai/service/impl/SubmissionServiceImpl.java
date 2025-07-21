package com.project.gamiai.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.project.gamiai.domain.Problem;
import com.project.gamiai.domain.Submission;
import com.project.gamiai.dto.response.SubmissionDetailDto;
import com.project.gamiai.dto.response.SubmissionListItemDto;
import com.project.gamiai.repository.ProblemRepository;
import com.project.gamiai.repository.SubmissionRepository;
import com.project.gamiai.service.SubmissionService;

@Service
public class SubmissionServiceImpl implements SubmissionService {

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private ProblemRepository problemRepository;

    // Inject the AI feedback URL from application.yaml
    @Value("${ai.agent.url:http://localhost:4000}")
    private String aiFeedbackUrl;

    @Override
    public void createPendingSubmission(String code, Integer problemId, Integer userId, String jobId) {
        Submission submission = new Submission();
        submission.setCode(code);
        submission.setProblemId(problemId);
        submission.setUserId(userId);
        submission.setStatus("Pending");
        submission.setSubmittedAt(LocalDateTime.now());
        submission.setJobId(jobId);
        submissionRepository.save(submission);
    }

    @Override
    public boolean hasAcceptedSubmission(Integer userId, Integer problemId) {

        return submissionRepository.existsByUserIdAndProblemIdAndStatus(userId, problemId, "Accepted");
    }

    @Override
    public Submission updateSubmissionWithResult(String jobId, JsonNode resultBody, String authorizationHeader) {
        Submission submission = submissionRepository.findByJobId(jobId);
        if (submission != null) {
            String status = "Accepted";
            // Check for error in resultBody
            if (resultBody.has("error") && !resultBody.get("error").isNull()
                    && !resultBody.get("error").asText().isEmpty()) {
                status = "Failed";
            } else if (resultBody.has("results") && resultBody.get("results").isArray()) {
                for (JsonNode res : resultBody.get("results")) {
                    if (!res.path("passed").asBoolean(false)) {
                        status = "Failed";
                        break;
                    }
                }
            }
            submission.setStatus(status);

            if (resultBody.has("results") && resultBody.get("results").isArray()) {
                double maxRuntime = 0, maxMemory = 0;
                for (JsonNode res : resultBody.get("results")) {
                    maxRuntime = Math.max(maxRuntime, res.path("userRuntime").asDouble(0));
                    maxMemory = Math.max(maxMemory, res.path("userMemory").asDouble(0));
                }
                submission.setRuntime((float) maxRuntime);
                submission.setMemory((float) maxMemory);
            }

            // Only get feedback if status is Accepted
            if ("Accepted".equals(status)) {
                try {
                    // Fetch problem title and description from ProblemRepository
                    Problem problem = problemRepository.findById(submission.getProblemId()).orElse(null);
                    String problemTitle = problem != null ? problem.getTitle() : "";
                    String problemDescription = problem != null ? problem.getDescription() : "";

                    String aiApiUrl = aiFeedbackUrl + "/api/ai/feedback"; // <-- Use /feedback endpoint
                    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                    com.fasterxml.jackson.databind.node.ObjectNode aiPayload = mapper.createObjectNode();
                    aiPayload.put("problem_title", problemTitle);
                    aiPayload.put("problem_description", problemDescription);
                    aiPayload.put("user_code", submission.getCode());
                    aiPayload.put("running_result", resultBody != null ? resultBody.toString() : "");

                    org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
                    headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
                    // Attach the authorization header if provided
                    if (authorizationHeader != null && !authorizationHeader.isEmpty()) {
                        headers.set("Authorization", authorizationHeader);
                    }
                    org.springframework.http.HttpEntity<String> aiEntity = new org.springframework.http.HttpEntity<>(aiPayload.toString(), headers);

                    org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
                    org.springframework.http.ResponseEntity<String> aiResponse =
                        restTemplate.postForEntity(aiApiUrl, aiEntity, String.class);

                    if (aiResponse.getStatusCode().is2xxSuccessful() && aiResponse.getBody() != null) {
                        // The /feedback endpoint returns JSON: {"feedback": "..."}
                        String body = aiResponse.getBody();
                        String feedback = null;
                        try {
                            com.fasterxml.jackson.databind.JsonNode json = mapper.readTree(body);
                            if (json.has("feedback")) {
                                feedback = json.get("feedback").asText();
                            }
                        } catch (Exception parseEx) {
                            // fallback: use raw body
                            feedback = body;
                        }
                        submission.setFeedback(feedback);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            submissionRepository.save(submission);
        }
        return submission;
    }

    @Override
    public List<SubmissionListItemDto> getSubmissionList(Integer userId, Integer problemId) {
        List<Submission> submissions;
        if (userId != null && problemId != null) {
            submissions = submissionRepository.findByUserIdAndProblemIdOrderBySubmittedAtDesc(userId, problemId);
        } else if (userId != null) {
            submissions = submissionRepository.findByUserIdOrderBySubmittedAtDesc(userId);
        } else if (problemId != null) {
            submissions = submissionRepository.findByProblemIdOrderBySubmittedAtDesc(problemId);
        } else {
            submissions = submissionRepository.findAll();
        }
        return submissions.stream().map(sub -> {
            SubmissionListItemDto dto = new SubmissionListItemDto();
            dto.setId(sub.getId());
            dto.setStatus(sub.getStatus());
            dto.setLanguage(sub.getLanguage());
            dto.setRuntime(sub.getRuntime());
            dto.setMemory(sub.getMemory());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public SubmissionDetailDto getSubmissionDetail(Integer submissionId) {
        Submission sub = submissionRepository.findById(submissionId).orElse(null);
        if (sub == null)
            return null;
        SubmissionDetailDto dto = new SubmissionDetailDto();
        dto.setId(sub.getId());
        dto.setCode(sub.getCode());
        dto.setLanguage(sub.getLanguage());
        dto.setRuntime(sub.getRuntime());
        dto.setMemory(sub.getMemory());
        dto.setStatus(sub.getStatus());
        dto.setSubmittedAt(sub.getSubmittedAt() != null ? sub.getSubmittedAt().toString() : null);
        dto.setFeedback(sub.getFeedback());
        // Add more fields if needed
        return dto;
    }
}