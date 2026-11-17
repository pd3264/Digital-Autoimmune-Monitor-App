package digitalautomonitor.model;

import digitalautomonitor.enums.RiskLevel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a monitored user in the digital organism.
 */
public class User {
    private String userId;
    private String username;
    private String role;
    private String department;
    private int baselineScore;
    private RiskLevel riskLevel;
    private LocalDateTime lastActive;
    private boolean monitoringEnabled;
    private int riskThreshold;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss dd-MMM");

    public User(String userId, String username, String role, String department, int baselineScore,
                RiskLevel riskLevel, LocalDateTime lastActive, boolean monitoringEnabled, int riskThreshold) {
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.department = department;
        this.baselineScore = baselineScore;
        this.riskLevel = riskLevel;
        this.lastActive = lastActive;
        this.monitoringEnabled = monitoringEnabled;
        this.riskThreshold = riskThreshold;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getBaselineScore() {
        return baselineScore;
    }

    public void setBaselineScore(int baselineScore) {
        this.baselineScore = baselineScore;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(RiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }

    public LocalDateTime getLastActive() {
        return lastActive;
    }

    public void setLastActive(LocalDateTime lastActive) {
        this.lastActive = lastActive;
    }

    public String getFormattedLastActive() {
        return lastActive != null ? lastActive.format(FORMATTER) : "N/A";
    }

    public boolean isMonitoringEnabled() {
        return monitoringEnabled;
    }

    public void setMonitoringEnabled(boolean monitoringEnabled) {
        this.monitoringEnabled = monitoringEnabled;
    }

    public int getRiskThreshold() {
        return riskThreshold;
    }

    public void setRiskThreshold(int riskThreshold) {
        this.riskThreshold = riskThreshold;
    }

    @Override
    public String toString() {
        return username + " (" + role + ")";
    }
}
