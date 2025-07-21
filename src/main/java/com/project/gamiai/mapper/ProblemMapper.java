package com.project.gamiai.mapper;

import com.project.gamiai.domain.Problem;
import com.project.gamiai.dto.response.ProblemDetailDto;

import org.springframework.stereotype.Component;

@Component
public class ProblemMapper {
    public ProblemDetailDto toDetailDto(Problem problem) {
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
        dto.setTags(problem.getTags());
        // Add more fields as needed
        return dto;
    }
}