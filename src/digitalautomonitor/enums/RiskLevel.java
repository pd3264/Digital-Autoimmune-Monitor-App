package digitalautomonitor.enums;

import java.awt.Color;

/**
 * Represents the risk severity classification of an event or entity.
 * Aligns with the biological immune response levels.
 */
public enum RiskLevel {
    NORMAL("Normal", 0, 29, new Color(16, 185, 129), "Healthy cellular state; baseline operation"),
    LOW("Low Risk", 30, 59, new Color(56, 189, 248), "Minor deviation detected; routine observation"),
    MEDIUM("Medium Risk", 60, 69, new Color(245, 158, 11), "Elevated anomaly; localized immune response"),
    HIGH("High Risk", 70, 79, new Color(249, 115, 22), "Significant deviation; active antibody intervention"),
    CRITICAL("Critical", 80, 100, new Color(239, 68, 68), "Severe pathogen-level anomaly; acute inflammation response");

    private final String displayName;
    private final int minScore;
    private final int maxScore;
    private final Color color;
    private final String description;

    RiskLevel(String displayName, int minScore, int maxScore, Color color, String description) {
        this.displayName = displayName;
        this.minScore = minScore;
        this.maxScore = maxScore;
        this.color = color;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getMinScore() {
        return minScore;
    }

    public int getMaxScore() {
        return maxScore;
    }

    public Color getColor() {
        return color;
    }

    public String getDescription() {
        return description;
    }

    public static RiskLevel fromScore(int score) {
        if (score < 30) return NORMAL;
        if (score < 60) return LOW;
        if (score < 70) return MEDIUM;
        if (score < 80) return HIGH;
        return CRITICAL;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
