package com.project.gamiai.domain.enums;

public enum ProficiencyLevel {
    BEGINNER,
    INTERMEDIATE,
    ADVANCED,
    EXPERT;

    public static ProficiencyLevel fromString(String value) {
        if (value == null) return BEGINNER;
        switch (value.trim().toLowerCase()) {
            case "beginner":
            case "novice":
                return BEGINNER;
            case "intermediate":
                return INTERMEDIATE;
            case "advanced":
                return ADVANCED;
            case "expert":
            case "mastery":
                return EXPERT;
            default:
                return BEGINNER;
        }
    }
    public int toNumeric() {
        switch (this) {
            case BEGINNER: return 0;
            case INTERMEDIATE: return 1;
            case ADVANCED: return 2;
            case EXPERT: return 3;
            default: return 0;
        }
    }
}