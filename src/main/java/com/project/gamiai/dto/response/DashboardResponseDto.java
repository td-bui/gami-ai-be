package com.project.gamiai.dto.response;

import java.util.List;
import lombok.Data;

@Data
public class DashboardResponseDto {
    private UserInfoResponseDto userInfo;
    private OverallStatsDto overallStats;
    private List<LeaderboardEntryDto> leaderboard;
    private List<CompletedLessonDto> lessonHistory;
    private List<SolvedProblemDto> problemHistory;
}
