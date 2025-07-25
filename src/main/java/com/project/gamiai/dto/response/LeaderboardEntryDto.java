package com.project.gamiai.dto.response;

import lombok.Data;

@Data
public class LeaderboardEntryDto {
    private String username;
    private String level;
    private Integer xp;
}