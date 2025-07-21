package com.project.gamiai.domain;

import java.time.LocalDateTime;

import com.project.gamiai.domain.enums.DifficultyLevel;
import com.project.gamiai.domain.enums.ProficiencyLevel;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "engagement_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EngagementLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer userId;
    private Integer problemId;

    private LocalDateTime timestamp = LocalDateTime.now();

    private Float performance;
    private Float timeTaken;
    private Float engagement;
    private Float difficulty;
    private Float proficiency;

    private Float disengagement;
    private Float reward;

    private String action;
    private Integer pointsAwarded;
    private String badgeAwarded;
    private Integer leaderboardRank;

    private int maxActions = 5;

    public void initializeFromRaw(
        int passedTestcases, int totalTestcases,
        float rawTimeTaken,
        int numActions,
        String rawDifficulty,
        String rawProficiency,
        float expectedTime
    ) {
        // Performance: passed/total testcases (0-1)
        this.performance = totalTestcases > 0 ? (float) passedTestcases / totalTestcases : 0f;

        // Time taken: normalized (actual/expected, capped at 2.0)
        this.timeTaken = expectedTime > 0 ? Math.min(rawTimeTaken / expectedTime, 2.0f) : 0f;

        // Engagement: normalized (actions/maxActions, capped at 1.0)
        this.engagement = maxActions > 0 ? Math.min((float) numActions / maxActions, 1.0f) : 0f;

        // Difficulty: normalized numeric value using enum
        DifficultyLevel diffEnum = DifficultyLevel.fromString(rawDifficulty);
        this.difficulty = diffEnum.toNumeric() / 2.0f; // 0=easy, 1=medium, 2=hard

        // Proficiency: normalized numeric value using enum
        ProficiencyLevel profEnum = ProficiencyLevel.fromString(rawProficiency);
        this.proficiency = profEnum.toNumeric() / 3.0f; // 0=beginner, ..., 3=expert

        float timeDisengage = expectedTime > 0 ? Math.max((rawTimeTaken - expectedTime) / expectedTime, 0f) : 0f;
        float actionDisengage = maxActions > 0 ? 1.0f - Math.min((float) numActions / maxActions, 1.0f) : 0f;
        float failDisengage = 1.0f - (this.performance != null ? this.performance : 0f);
        this.disengagement = Math.min((timeDisengage + actionDisengage + failDisengage) / 3.0f, 1.0f);
    }
}