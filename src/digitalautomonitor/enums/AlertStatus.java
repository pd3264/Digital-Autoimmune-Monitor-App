package digitalautomonitor.enums;

import java.awt.Color;

/**
 * Status lifecycle for security alerts.
 */
public enum AlertStatus {
    NEW("New", new Color(239, 68, 68)),
    INVESTIGATING("Investigating", new Color(245, 158, 11)),
    ACKNOWLEDGED("Acknowledged", new Color(56, 189, 248)),
    RESOLVED("Resolved", new Color(16, 185, 129));

    private final String label;
    private final Color badgeColor;

    AlertStatus(String label, Color badgeColor) {
        this.label = label;
        this.badgeColor = badgeColor;
    }

    public String getLabel() {
        return label;
    }

    public Color getBadgeColor() {
        return badgeColor;
    }

    @Override
    public String toString() {
        return label;
    }
}
