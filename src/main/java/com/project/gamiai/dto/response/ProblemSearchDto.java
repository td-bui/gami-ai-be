package com.project.gamiai.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProblemSearchDto {
    private Integer id;
    private String title;
    private String difficulty;
    private String topicName;
    private Boolean isSolved;
}