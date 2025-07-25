package com.project.gamiai.dto.response;

import java.util.Map;
import lombok.Data;

@Data
public class OverallStatsDto {
    private String problemsSolved; // e.g., "67/3626"
    private String acceptanceRate; // e.g., "42.86%"
    private PerformanceDto performanceBeats;
    private Integer attempting;
    private Long totalSubmissions;
    private Map<String, String> byDifficulty; // e.g., {"Easy": "30/886"}
}