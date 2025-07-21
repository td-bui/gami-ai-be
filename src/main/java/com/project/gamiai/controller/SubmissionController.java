package com.project.gamiai.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.gamiai.dto.response.SubmissionDetailDto;
import com.project.gamiai.dto.response.SubmissionListItemDto;
import com.project.gamiai.service.SubmissionService;

@RestController
@RequestMapping("/api/submissions")
public class SubmissionController {

    @Autowired
    private SubmissionService submissionService;

    // 1. Get list of submissions for a user or problem (you can adjust params as needed)
    @GetMapping
    public List<SubmissionListItemDto> getSubmissions(
            @RequestHeader("user-id") Integer userId,
            @RequestParam(required = false) Integer problemId
    ) {
        return submissionService.getSubmissionList(userId, problemId);
    }

    // 2. Get detail of a submission by id
    @GetMapping("/{submissionId}")
    public SubmissionDetailDto getSubmissionDetail(@PathVariable Integer submissionId) {
        return submissionService.getSubmissionDetail(submissionId);
    }
}