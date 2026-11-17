package digitalautomonitor.model;

import digitalautomonitor.enums.EventType;
import digitalautomonitor.enums.RiskLevel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a simulated or captured behaviour event (Antigen).
 */
public class BehaviourEvent {
    private String eventId;
    private LocalDateTime timestamp;
    private String username;
    private String ipAddress;
    private EventType eventType;
    private String description;
    private RiskLevel riskLevel;
    private String actionTaken;
    private String metricName;
    private String observedValue;
    private String baselineValue;
    private String deviation;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter FULL_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public BehaviourEvent(String eventId, LocalDateTime timestamp, String username, String ipAddress,
                          EventType eventType, String description, RiskLevel riskLevel, String actionTaken) {
        this(eventId, timestamp, username, ipAddress, eventType, description, riskLevel, actionTaken,
             eventType.getCategoryName(), "N/A", "N/A", "0%");
    }

    public BehaviourEvent(String eventId, LocalDateTime timestamp, String username, String ipAddress,
                          EventType eventType, String description, RiskLevel riskLevel, String actionTaken,
                          String metricName, String observedValue, String baselineValue, String deviation) {
        this.eventId = eventId;
        this.timestamp = timestamp;
        this.username = username;
        this.ipAddress = ipAddress;
        this.eventType = eventType;
        this.description = description;
        this.riskLevel = riskLevel;
        this.actionTaken = actionTaken;
        this.metricName = metricName;
        this.observedValue = observedValue;
        this.baselineValue = baselineValue;
        this.deviation = deviation;
    }

    public String getEventId() {
        return eventId;
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

    public String getUsername() {
        return username;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public EventType getEventType() {
        return eventType;
    }

    public String getDescription() {
        return description;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public String getActionTaken() {
        return actionTaken;
    }

    public String getMetricName() {
        return metricName;
    }

    public String getObservedValue() {
        return observedValue;
    }

    public String getBaselineValue() {
        return baselineValue;
    }

    public String getDeviation() {
        return deviation;
    }

    public void setActionTaken(String actionTaken) {
        this.actionTaken = actionTaken;
    }

    public void setRiskLevel(RiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s - %s (%s)", getFormattedTime(), username, description, riskLevel);
    }
}
