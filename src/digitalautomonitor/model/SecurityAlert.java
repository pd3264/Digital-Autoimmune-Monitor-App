package digitalautomonitor.model;

import digitalautomonitor.enums.AlertStatus;
import digitalautomonitor.enums.EventType;
import digitalautomonitor.enums.RiskLevel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a security alert (Inflammation) triggered by anomalous behaviour.
 */
public class SecurityAlert {
    private String alertId;
    private LocalDateTime timestamp;
    private RiskLevel severity;
    private String username;
    private EventType category;
    private String description;
    private AlertStatus status;
    private String detectionReason;
    private String deviationSummary;
    private String recommendedAction;
    private String acknowledgedBy;
    private LocalDateTime resolvedAt;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter FULL_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public SecurityAlert(String alertId, LocalDateTime timestamp, RiskLevel severity, String username,
                         EventType category, String description, AlertStatus status, String detectionReason,
                         String deviationSummary, String recommendedAction) {
        this.alertId = alertId;
        this.timestamp = timestamp;
        this.severity = severity;
        this.username = username;
        this.category = category;
        this.description = description;
        this.status = status;
        this.detectionReason = detectionReason;
        this.deviationSummary = deviationSummary;
        this.recommendedAction = recommendedAction;
    }

    public String getAlertId() {
        return alertId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getFormattedTime() {
        return timestamp != null ? timestamp.format(TIME_FORMATTER) : "";
    }

    public String getFormattedFullTime() {
        return timestamp != null ? timestamp.format(FULL_FORMATTER) : "";
    }

    public RiskLevel getSeverity() {
        return severity;
    }

    public void setSeverity(RiskLevel severity) {
        this.severity = severity;
    }

    public String getUsername() {
        return username;
    }

    public EventType getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public AlertStatus getStatus() {
        return status;
    }

    public void setStatus(AlertStatus status) {
        this.status = status;
    }

    public String getDetectionReason() {
        return detectionReason;
    }

    public String getDeviationSummary() {
        return deviationSummary;
    }

    public String getRecommendedAction() {
        return recommendedAction;
    }

    public String getAcknowledgedBy() {
        return acknowledgedBy;
    }

    public void setAcknowledgedBy(String acknowledgedBy) {
        this.acknowledgedBy = acknowledgedBy;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s - %s (%s)", alertId, username, description, severity);
    }
}
