package com.project.gamiai.domain.enums;

public enum DifficultyLevel {
    EASY,
    MEDIUM,
    HARD;

    public static DifficultyLevel fromString(String value) {
        if (value == null) return EASY;
        switch (value.trim().toLowerCase()) {
            case "easy": return EASY;
            case "medium": return MEDIUM;
            case "hard": return HARD;
            default: return EASY;
        }
    }

    public int toNumeric() {
        switch (this) {
            case EASY: return 0;
            case MEDIUM: return 1;
            case HARD: return 2;
            default: return 0;
        }
    }
}