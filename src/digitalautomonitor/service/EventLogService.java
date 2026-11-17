package digitalautomonitor.service;

import digitalautomonitor.enums.EventType;
import digitalautomonitor.enums.RiskLevel;
import digitalautomonitor.model.BehaviourEvent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Manages system behaviour event logs (Antigen history).
 */
public final class EventLogService {

    private final List<BehaviourEvent> events = new ArrayList<>();
    private final AtomicInteger eventIdCounter = new AtomicInteger(1000);
    private final List<Runnable> listeners = new ArrayList<>();

    public EventLogService() {
        seedInitialEvents();
    }

    private void seedInitialEvents() {
        LocalDateTime now = LocalDateTime.now();

        addSeededEvent(new BehaviourEvent("EVT-" + eventIdCounter.incrementAndGet(), now.minusMinutes(2),
                "admin", "192.168.1.10", EventType.LOGIN, "Multiple failed logins",
                RiskLevel.HIGH, "Investigating", "Login Frequency", "8 attempts", "1 attempt", "+700%"));

        addSeededEvent(new BehaviourEvent("EVT-" + eventIdCounter.incrementAndGet(), now.minusMinutes(5),
                "user01", "10.0.4.15", EventType.PROCESS, "Unusual process activity",
                RiskLevel.MEDIUM, "Monitoring", "Process Count", "78 procs", "45 procs", "+73%"));

        addSeededEvent(new BehaviourEvent("EVT-" + eventIdCounter.incrementAndGet(), now.minusMinutes(9),
                "user03", "10.0.4.88", EventType.FILE_ACCESS, "Large file access in secure volume",
                RiskLevel.HIGH, "Blocked", "File Access Rate", "320 files/min", "15 files/min", "+2033%"));

        addSeededEvent(new BehaviourEvent("EVT-" + eventIdCounter.incrementAndGet(), now.minusMinutes(14),
                "admin", "192.168.1.10", EventType.LOGIN, "Normal login with valid credentials",
                RiskLevel.NORMAL, "Resolved", "Login Frequency", "1 attempt", "1 attempt", "0%"));

        addSeededEvent(new BehaviourEvent("EVT-" + eventIdCounter.incrementAndGet(), now.minusMinutes(18),
                "user02", "10.0.2.22", EventType.APPLICATION, "Compiled binary execution in %APPDATA%",
                RiskLevel.MEDIUM, "Flagged", "Application Usage", "custom_tool.exe", "Approved list", "+100%"));

        addSeededEvent(new BehaviourEvent("EVT-" + eventIdCounter.incrementAndGet(), now.minusMinutes(22),
                "user05", "10.0.6.12", EventType.USB, "Unauthorized USB flash drive connected",
                RiskLevel.MEDIUM, "Quarantined", "USB Activity", "Removable Drive", "Blocked", "Policy violation"));

        addSeededEvent(new BehaviourEvent("EVT-" + eventIdCounter.incrementAndGet(), now.minusMinutes(27),
                "user04", "10.0.1.77", EventType.NETWORK, "Normal outbound HTTPS traffic to trusted CDN",
                RiskLevel.NORMAL, "Allowed", "Network Bandwidth", "42 MB/hr", "120 MB/hr", "-65%"));

        addSeededEvent(new BehaviourEvent("EVT-" + eventIdCounter.incrementAndGet(), now.minusMinutes(35),
                "user09", "10.0.9.14", EventType.PROCESS, "High CPU usage via unknown script runner",
                RiskLevel.HIGH, "Throttled", "CPU Usage", "92%", "25%", "+268%"));

        addSeededEvent(new BehaviourEvent("EVT-" + eventIdCounter.incrementAndGet(), now.minusMinutes(42),
                "user03", "10.0.4.88", EventType.NETWORK, "High volume data egress to unclassified external IP",
                RiskLevel.CRITICAL, "Terminated", "Network Activity", "1.8 GB transfer", "120 MB", "+1400%"));

        addSeededEvent(new BehaviourEvent("EVT-" + eventIdCounter.incrementAndGet(), now.minusMinutes(50),
                "user08", "10.0.8.31", EventType.SYSTEM, "Service status check and routine health ping",
                RiskLevel.NORMAL, "Allowed", "System Operations", "Routine probe", "Baseline", "0%"));
    }

    private void addSeededEvent(BehaviourEvent event) {
        events.add(0, event);
    }

    public synchronized void logEvent(BehaviourEvent event) {
        events.add(0, event); // Latest first
        notifyListeners();
    }

    public synchronized BehaviourEvent createAndLogEvent(String username, String ip, EventType type,
                                                         String description, RiskLevel risk, String action,
                                                         String metric, String observed, String baseline, String deviation) {
        String id = "EVT-" + eventIdCounter.incrementAndGet();
        BehaviourEvent event = new BehaviourEvent(id, LocalDateTime.now(), username, ip, type,
                description, risk, action, metric, observed, baseline, deviation);
        logEvent(event);
        return event;
    }

    public synchronized List<BehaviourEvent> getAllEvents() {
        return Collections.unmodifiableList(new ArrayList<>(events));
    }

    public synchronized List<BehaviourEvent> getRecentEvents(int limit) {
        List<BehaviourEvent> copy = new ArrayList<>(events);
        if (copy.size() <= limit) return copy;
        return copy.subList(0, limit);
    }

    public synchronized int getEventsCount() {
        return events.size();
    }

    public synchronized int getEventsTodayCount() {
        // Simulated day baseline + dynamic events
        return 1284 + events.size();
    }

    public synchronized void exportToCsv(File file) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("Event ID,Timestamp,User,IP,Event Type,Description,Risk,Action,Metric,Observed,Baseline,Deviation\n");
            for (BehaviourEvent e : events) {
                writer.write(String.format("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"\n",
                        escape(e.getEventId()),
                        escape(e.getFormattedFullTime()),
                        escape(e.getUsername()),
                        escape(e.getIpAddress()),
                        escape(e.getEventType().getCategoryName()),
                        escape(e.getDescription()),
                        escape(e.getRiskLevel().getDisplayName()),
                        escape(e.getActionTaken()),
                        escape(e.getMetricName()),
                        escape(e.getObservedValue()),
                        escape(e.getBaselineValue()),
                        escape(e.getDeviation())
                ));
            }
        }
    }

    public synchronized void exportToTxt(File file) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("========================================================================================\n");
            writer.write("DIGITAL AUTOIMMUNE MONITOR - BEHAVIOURAL EVENT AUDIT LOG\n");
            writer.write("Generated: " + LocalDateTime.now() + "\n");
            writer.write("Total Logged Events: " + events.size() + "\n");
            writer.write("========================================================================================\n\n");
            for (BehaviourEvent e : events) {
                writer.write(String.format("[%s] %-10s | User: %-10s | IP: %-15s | Type: %-15s | Risk: %-8s | Action: %s\n",
                        e.getFormattedTime(), e.getEventId(), e.getUsername(), e.getIpAddress(),
                        e.getEventType().getCategoryName(), e.getRiskLevel().getDisplayName(), e.getActionTaken()));
                writer.write(String.format("   Detail: %s (Metric: %s, Observed: %s, Baseline: %s, Dev: %s)\n\n",
                        e.getDescription(), e.getMetricName(), e.getObservedValue(), e.getBaselineValue(), e.getDeviation()));
            }
        }
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\"", "\"\"");
    }

    public synchronized void addChangeListener(Runnable r) {
        this.listeners.add(r);
    }

    private void notifyListeners() {
        for (Runnable r : listeners) {
            try {
                r.run();
            } catch (Exception ignored) {
            }
        }
    }
}
