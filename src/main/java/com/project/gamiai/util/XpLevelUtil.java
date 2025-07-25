package com.project.gamiai.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.project.gamiai.domain.Leaderboard;
import com.project.gamiai.repository.LeaderboardRepository;

@Component
public class XpLevelUtil {

    @Autowired
    private LeaderboardRepository leaderboardRepository;

    /**
     * Awards XP for completing a lesson and updates the user's leaderboard stats.
     * @param userId The ID of the user.
     * @param difficulty The difficulty of the completed lesson.
     * @return The amount of XP gained.
     */
    public int awardXpForLesson(Integer userId, String difficulty) {
        return updateLeaderboard(userId, difficulty, "lesson");
    }

    /**
     * Awards XP for solving a problem and updates the user's leaderboard stats.
     * @param userId The ID of the user.
     * @param difficulty The difficulty of the solved problem.
     * @return The amount of XP gained.
     */
    public int awardXpForProblem(Integer userId, String difficulty) {
        return updateLeaderboard(userId, difficulty, "problem");
    }

    private int updateLeaderboard(Integer userId, String difficulty, String activityType) {
        int xpGained = getXpFromDifficulty(difficulty);

        if (xpGained > 0) {
            Leaderboard leaderboard = leaderboardRepository.findByUserId(userId)
                    .orElseGet(() -> createNewLeaderboardEntry(userId));

            String currentLevel = leaderboard.getLevel();
            leaderboard.setXp(leaderboard.getXp() + xpGained);

            if ("lesson".equals(activityType)) {
                leaderboard.setLessonsCompleted(leaderboard.getLessonsCompleted() + 1);
            } else if ("problem".equals(activityType)) {
                leaderboard.setProblemsSolved(leaderboard.getProblemsSolved() + 1);
            }

            String newLevel = getLevelFromXp(leaderboard.getXp());
            if (isLevelUp(currentLevel, newLevel)) {
                leaderboard.setLevel(newLevel);
            }

            leaderboardRepository.save(leaderboard);
        }
        return xpGained;
    }

    private Leaderboard createNewLeaderboardEntry(Integer userId) {
        Leaderboard newEntry = new Leaderboard();
        newEntry.setUserId(userId);
        newEntry.setXp(0);
        newEntry.setProblemsSolved(0);
        newEntry.setLessonsCompleted(0);
        newEntry.setLevel("beginner");
        return newEntry;
    }

    private int getXpFromDifficulty(String difficulty) {
        if (difficulty == null) return 0;
        return switch (difficulty.toLowerCase()) {
            case "easy" -> 10;
            case "medium" -> 20;
            case "hard" -> 40;
            default -> 0;
        };
    }

    private String getLevelFromXp(int totalXp) {
        if (totalXp >= 700) return "expert";
        if (totalXp >= 300) return "advanced";
        if (totalXp >= 100) return "intermediate";
        return "beginner";
    }

    private boolean isLevelUp(String currentLevel, String newLevel) {
        return getLevelRank(newLevel) > getLevelRank(currentLevel);
    }

    private int getLevelRank(String level) {
        if (level == null) return -1;
        return switch (level.toLowerCase()) {
            case "beginner" -> 0;
            case "intermediate" -> 1;
            case "advanced" -> 2;
            case "expert" -> 3;
            default -> -1;
        };
    }
}