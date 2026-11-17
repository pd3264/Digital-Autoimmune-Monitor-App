package digitalautomonitor.model;

import java.awt.Color;

/**
 * Metric measurement for system health and engine performance.
 */
public class SystemMetric {
    private String name;
    private int percentage;
    private String status; // ONLINE, DEGRADED, OFFLINE
    private String detail;

    public SystemMetric(String name, int percentage, String status, String detail) {
        this.name = name;
        this.percentage = percentage;
        this.status = status;
        this.detail = detail;
    }

    public String getName() {
        return name;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = Math.max(0, Math.min(100, percentage));
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Color getStatusColor() {
        if ("ONLINE".equalsIgnoreCase(status)) {
            return new Color(16, 185, 129);
        } else if ("DEGRADED".equalsIgnoreCase(status)) {
            return new Color(245, 158, 11);
        } else {
            return new Color(239, 68, 68);
        }
    }
}
