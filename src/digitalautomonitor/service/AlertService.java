package digitalautomonitor.service;

import digitalautomonitor.enums.AlertStatus;
import digitalautomonitor.enums.EventType;
import digitalautomonitor.enums.RiskLevel;
import digitalautomonitor.model.BehaviourEvent;
import digitalautomonitor.model.DetectionResult;
import digitalautomonitor.model.SecurityAlert;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Stores and manages security alerts. Provides creation, acknowledgement,
 * and resolution operations, and notifies registered listeners on changes.
 */
public final class AlertService {

    private final List<SecurityAlert> alerts = new ArrayList<>();
    private final AtomicInteger idCounter = new AtomicInteger(500);
    private final List<Runnable> changeListeners = new ArrayList<>();

    public AlertService() {
        seedInitialAlerts();
    }

    // -----------------------------------------------------------------------
    //  Seed data – pre-loaded example alerts
    // -----------------------------------------------------------------------

    private void seedInitialAlerts() {
        seed(RiskLevel.CRITICAL, "user03", EventType.FILE_ACCESS,
                "Mass exfiltration detected in confidential repository",
                "Unusual burst of 1,420 file reads within 40 seconds exceeding learned baseline",
                "Observed: 1,420 files/min | Baseline: 15 files/min (+9366% deviation)",
                "Isolate endpoint from VLAN and freeze user authentication credentials.");

        seed(RiskLevel.CRITICAL, "user09", EventType.PROCESS,
                "Unauthorized binary execution masquerading as svchost.exe",
                "Spawning unsigned executable from temp directory with escalated privileges",
                "Observed: svch0st.exe (Unsigned) | Baseline: Known system binaries only",
                "Terminate process tree, dump memory for malware triage, quarantine host.");

        seed(RiskLevel.HIGH, "admin", EventType.LOGIN,
                "Multiple failed logins followed by rapid success from new IP",
                "Brute-force pattern detected with 14 failed attempts within 3 minutes",
                "Observed: 14 failed logins | Baseline: 0.1 failures/day",
                "Require immediate MFA challenge and verify analyst physical location.");

        seed(RiskLevel.HIGH, "user01", EventType.NETWORK,
                "Abnormal outbound telemetry spike to external IP",
                "Continuous TCP beacons to unregistered ASN exceeding network baseline",
                "Observed: 640 MB / hr | Baseline: 120 MB / hr (+433% deviation)",
                "Throttle egress bandwidth and inspect packet payloads.");

        seed(RiskLevel.MEDIUM, "user05", EventType.USB,
                "Unauthorized removable storage device mounted",
                "Mass storage device connected without cryptographic authorization token",
                "Observed: USB Flash VID:0781 | Baseline: USB storage disabled",
                "Audit transferred files and prompt user for device clearance code.");

        seed(RiskLevel.MEDIUM, "user07", EventType.APPLICATION,
                "Cryptomining process heuristics observed in user session",
                "Sustained 98% GPU/CPU utilization from non-standard background worker",
                "Observed: 98% CPU | Baseline: 25% CPU (+292% deviation)",
                "Throttle process priority and investigate binary hash.");

        seed(RiskLevel.LOW, "user02", EventType.SYSTEM,
                "Off-hours privileged scheduled task created",
                "Task scheduler modified at 03:14 AM by non-service account",
                "Observed: Task 'sys_sync' registered | Baseline: Business hours operations",
                "Review scheduled script content and verify business necessity.");
    }

    private void seed(RiskLevel severity, String username, EventType category,
                      String description, String reason, String deviation, String recommendation) {
        alerts.add(0, new SecurityAlert(
                "ALT-" + idCounter.incrementAndGet(), LocalDateTime.now(),
                severity, username, category, description,
                AlertStatus.NEW, reason, deviation, recommendation));
    }

    // -----------------------------------------------------------------------
    //  Create
    // -----------------------------------------------------------------------

    public synchronized SecurityAlert createAlert(RiskLevel severity, String username, EventType category,
                                                  String description, String reason, String deviation,
                                                  String recommendation) {
        SecurityAlert alert = new SecurityAlert(
                "ALT-" + idCounter.incrementAndGet(), LocalDateTime.now(),
                severity, username, category, description,
                AlertStatus.NEW, reason, deviation, recommendation);
        alerts.add(0, alert);
        notifyListeners();
        return alert;
    }

    /**
     * Creates an alert from a completed detection result. Returns null if the
     * risk level is too low to warrant an alert (Normal or Low).
     */
    public synchronized SecurityAlert createAlertFromDetection(DetectionResult result, BehaviourEvent event) {
        if (result.getRiskLevel() == RiskLevel.NORMAL || result.getRiskLevel() == RiskLevel.LOW) {
            return null;
        }
        String reason    = String.join("; ", result.getContributingFactors());
        String deviation = String.format("Observed: %s | Baseline: %s (Deviation: %s)",
                event.getObservedValue(), event.getBaselineValue(), event.getDeviation());
        return createAlert(result.getRiskLevel(), event.getUsername(), event.getEventType(),
                event.getDescription(), reason, deviation, result.getRecommendedAction());
    }

    // -----------------------------------------------------------------------
    //  Update status
    // -----------------------------------------------------------------------

    public synchronized boolean acknowledgeAlert(String alertId, String analyst) {
        SecurityAlert alert = findById(alertId);
        if (alert == null) return false;
        alert.setStatus(AlertStatus.ACKNOWLEDGED);
        alert.setAcknowledgedBy(analyst);
        notifyListeners();
        return true;
    }

    public synchronized boolean resolveAlert(String alertId) {
        SecurityAlert alert = findById(alertId);
        if (alert == null) return false;
        alert.setStatus(AlertStatus.RESOLVED);
        alert.setResolvedAt(LocalDateTime.now());
        notifyListeners();
        return true;
    }

    public synchronized boolean updateStatus(String alertId, AlertStatus newStatus) {
        SecurityAlert alert = findById(alertId);
        if (alert == null) return false;
        alert.setStatus(newStatus);
        if (newStatus == AlertStatus.RESOLVED) alert.setResolvedAt(LocalDateTime.now());
        notifyListeners();
        return true;
    }

    // -----------------------------------------------------------------------
    //  Query
    // -----------------------------------------------------------------------

    public synchronized List<SecurityAlert> getAllAlerts() {
        return Collections.unmodifiableList(new ArrayList<>(alerts));
    }

    public synchronized SecurityAlert getAlertById(String id) {
        return findById(id);
    }

    public synchronized int getActiveAlertsCount() {
        return (int) alerts.stream().filter(a -> a.getStatus() != AlertStatus.RESOLVED).count();
    }

    public synchronized int getCriticalAlertsCount() {
        return (int) alerts.stream()
                .filter(a -> a.getSeverity() == RiskLevel.CRITICAL && a.getStatus() != AlertStatus.RESOLVED)
                .count();
    }

    // -----------------------------------------------------------------------
    //  Change listeners
    // -----------------------------------------------------------------------

    public synchronized void addChangeListener(Runnable listener) {
        changeListeners.add(listener);
    }

    private void notifyListeners() {
        changeListeners.forEach(r -> { try { r.run(); } catch (Exception ignored) {} });
    }

    // -----------------------------------------------------------------------
    //  Private helper
    // -----------------------------------------------------------------------

    private SecurityAlert findById(String id) {
        if (id == null) return null;
        for (SecurityAlert a : alerts) {
            if (a.getAlertId().equalsIgnoreCase(id)) return a;
        }
        return null;
    }
}
