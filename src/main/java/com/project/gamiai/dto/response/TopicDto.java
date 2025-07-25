package com.project.gamiai.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopicDto {
    private Integer id;
    private String name;
    private Integer numberOfProblems;
    private String code;
}