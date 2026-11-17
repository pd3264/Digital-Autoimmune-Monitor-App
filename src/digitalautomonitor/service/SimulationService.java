package digitalautomonitor.service;

import digitalautomonitor.detection.AnomalyDetector;
import digitalautomonitor.enums.EventType;
import digitalautomonitor.enums.RiskLevel;
import digitalautomonitor.model.BehaviourEvent;
import digitalautomonitor.model.BehaviourProfile;
import digitalautomonitor.model.DetectionResult;
import digitalautomonitor.model.SecurityAlert;
import digitalautomonitor.model.User;

import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

/**
 * Drives automatic event simulation and ties together all core services.
 *
 * Every 4 seconds a background timer generates a random event:
 *   - 75% chance → normal event
 *   - 18% chance → suspicious event
 *   -  7% chance → critical event
 *
 * Each event is logged and run through the AnomalyDetector scoring engine.
 * If the score exceeds the alert threshold, a SecurityAlert is raised.
 */
public final class SimulationService {

    private final EventLogService eventLogService;
    private final AlertService alertService;
    private final BehaviourMonitorService monitorService;
    private final AnomalyDetector anomalyDetector;

    private final Random random = new Random();
    private Timer autoTimer;
    private boolean isMonitoring = true;

    // Settings adjustable from the UI
    private int sensitivity = 65;  // How strict detection is (1–100)
    private int threshold   = 60;  // Minimum score needed to raise an alert

    // UI callbacks — panels register here to receive live updates
    private final List<Consumer<DetectionResult>> detectionListeners     = new ArrayList<>();
    private final List<Consumer<SecurityAlert>>   criticalAlertListeners = new ArrayList<>();

    @SuppressWarnings("this-escape")
    public SimulationService(EventLogService eventLogService, AlertService alertService,
                             BehaviourMonitorService monitorService) {
        this.eventLogService = eventLogService;
        this.alertService    = alertService;
        this.monitorService  = monitorService;
        this.anomalyDetector = new AnomalyDetector();
        startAutoTimer();
    }

    // -----------------------------------------------------------------------
    //  Background timer
    // -----------------------------------------------------------------------

    private void startAutoTimer() {
        autoTimer = new Timer(4000, e -> {
            if (!isMonitoring) return;
            int roll = random.nextInt(100);
            if (roll < 75)      simulateNormalEvent();
            else if (roll < 93) simulateSuspiciousEvent();
            else                simulateCriticalEvent();
        });
        autoTimer.start();
    }

    public synchronized void startMonitoring() {
        isMonitoring = true;
        if (!autoTimer.isRunning()) autoTimer.start();
    }

    public synchronized void pauseMonitoring() {
        isMonitoring = false;
    }

    public synchronized boolean isMonitoring() {
        return isMonitoring;
    }

    // -----------------------------------------------------------------------
    //  Settings
    // -----------------------------------------------------------------------

    public void setSensitivity(int sensitivity) { this.sensitivity = sensitivity; }
    public void setThreshold(int threshold)     { this.threshold   = threshold; }
    public int  getSensitivity()                { return sensitivity; }
    public int  getThreshold()                  { return threshold; }

    // -----------------------------------------------------------------------
    //  Event simulation
    // -----------------------------------------------------------------------

    /** Simulates a low-risk, normal system event. */
    public DetectionResult simulateNormalEvent() {
        String user = pickRandomUser();
        String ip   = "192.168.1." + (10 + random.nextInt(150));

        EventType[] types = {EventType.LOGIN, EventType.FILE_ACCESS, EventType.NETWORK, EventType.SYSTEM};
        EventType type = types[random.nextInt(types.length)];

        String desc, metric, obs, base;
        switch (type) {
            case LOGIN:
                desc = "Standard single-factor login from registered workstation";
                metric = "Login Frequency"; obs = "1 attempt"; base = "1 attempt"; break;
            case FILE_ACCESS:
                desc = "Routine document access within authorized user folder";
                metric = "File Access"; obs = (5 + random.nextInt(8)) + " files/min"; base = "15 files/min"; break;
            case NETWORK:
                desc = "Normal outbound HTTPS traffic to internal company proxy";
                metric = "Network Traffic"; obs = (20 + random.nextInt(40)) + " MB/hr"; base = "120 MB/hr"; break;
            default: // SYSTEM
                desc = "Periodic heartbeat ping from background monitoring agent";
                metric = "System Status"; obs = "Heartbeat OK"; base = "Heartbeat OK"; break;
        }

        return processAndLog(eventLogService.createAndLogEvent(
                user, ip, type, desc, RiskLevel.NORMAL, "Allowed", metric, obs, base, "0%"));
    }

    /** Simulates a medium-severity suspicious event. */
    public DetectionResult simulateSuspiciousEvent() {
        String user = pickRandomUser();
        String ip   = "10.0." + random.nextInt(5) + "." + (20 + random.nextInt(80));

        EventType[] types = {EventType.PROCESS, EventType.USB, EventType.LOGIN, EventType.APPLICATION};
        EventType type = types[random.nextInt(types.length)];

        String desc, metric, obs, base, dev;
        switch (type) {
            case PROCESS:
                desc = "Unusual process launched from user desktop folder";
                metric = "CPU Usage"; obs = (65 + random.nextInt(20)) + "%"; base = "25%"; dev = "+160%"; break;
            case USB:
                desc = "Unrecognized USB flash drive connected to workstation";
                metric = "USB Activity"; obs = "Device connected"; base = "USB Blocked"; dev = "Policy breach"; break;
            case LOGIN:
                desc = "Multiple failed logins followed by successful authentication";
                metric = "Login Frequency"; obs = "6 attempts"; base = "1 attempt"; dev = "+500%"; break;
            default: // APPLICATION
                desc = "Non-standard executable launched from temporary directory";
                metric = "Application Usage"; obs = "custom_tool.exe"; base = "Approved apps"; dev = "+100%"; break;
        }

        return processAndLog(eventLogService.createAndLogEvent(
                user, ip, type, desc, RiskLevel.MEDIUM, "Monitoring", metric, obs, base, dev));
    }

    /** Simulates a critical-severity threat event. */
    public DetectionResult simulateCriticalEvent() {
        String user = pickRandomUser();
        String ip   = "172.16.88." + (100 + random.nextInt(50));

        EventType[] types = {EventType.FILE_ACCESS, EventType.NETWORK, EventType.PROCESS};
        EventType type = types[random.nextInt(types.length)];

        String desc, metric, obs, base, dev;
        switch (type) {
            case FILE_ACCESS:
                desc = "Mass ransomware-like file encryption pattern detected";
                metric = "File Access"; obs = "1,850 files/sec"; base = "15 files/min"; dev = "+12000%"; break;
            case NETWORK:
                desc = "High-speed encrypted data transfer to unknown external server";
                metric = "Network Activity"; obs = "3.4 GB burst"; base = "120 MB/hr"; dev = "+2733%"; break;
            default: // PROCESS
                desc = "Suspicious process masquerading as svchost.exe with elevated privileges";
                metric = "Process Activity"; obs = "svch0st.exe (Root)"; base = "svchost.exe (System)"; dev = "Severe"; break;
        }

        return processAndLog(eventLogService.createAndLogEvent(
                user, ip, type, desc, RiskLevel.CRITICAL, "Quarantined", metric, obs, base, dev));
    }

    // -----------------------------------------------------------------------
    //  Core processing pipeline
    // -----------------------------------------------------------------------

    private String pickRandomUser() {
        List<String> names = monitorService.getAllUsernames();
        return names.get(random.nextInt(names.size()));
    }

    /**
     * Runs the anomaly detection algorithm on an event and handles the results:
     *  1. Updates the user's risk level.
     *  2. Creates a security alert if the score is high enough.
     *  3. Notifies all registered UI listeners.
     */
    private DetectionResult processAndLog(BehaviourEvent event) {
        // Get the user's baseline profile for comparison
        BehaviourProfile profile = monitorService.getProfile(event.getUsername());

        // Run the detection scoring algorithm
        DetectionResult result = anomalyDetector.detectAnomaly(profile, event, sensitivity, threshold);

        // Update the user's displayed risk level
        User u = monitorService.getUserByUsername(event.getUsername());
        if (u != null) {
            RiskLevel detected = result.getRiskLevel();
            if (detected == RiskLevel.CRITICAL) {
                u.setRiskLevel(RiskLevel.CRITICAL);
                u.setBaselineScore(Math.max(40, u.getBaselineScore() - 15));
            } else if (detected == RiskLevel.HIGH) {
                u.setRiskLevel(RiskLevel.HIGH);
                u.setBaselineScore(Math.max(55, u.getBaselineScore() - 8));
            } else if (detected == RiskLevel.LOW) {
                if (u.getRiskLevel() != RiskLevel.CRITICAL && u.getRiskLevel() != RiskLevel.HIGH) {
                    u.setRiskLevel(RiskLevel.LOW);
                }
            }
        }

        // Raise a security alert if the anomaly score is above the configured threshold
        if (result.getAnomalyScore() >= threshold) {
            SecurityAlert alert = alertService.createAlertFromDetection(result, event);
            if (alert != null && alert.getSeverity() == RiskLevel.CRITICAL) {
                notifyCriticalAlertListeners(alert);
            }
        }

        notifyDetectionListeners(result);
        return result;
    }

    // -----------------------------------------------------------------------
    //  Listener registration — UI panels subscribe here for live updates
    // -----------------------------------------------------------------------

    public synchronized void addDetectionListener(Consumer<DetectionResult> listener) {
        detectionListeners.add(listener);
    }

    public synchronized void addCriticalAlertListener(Consumer<SecurityAlert> listener) {
        criticalAlertListeners.add(listener);
    }

    private void notifyDetectionListeners(DetectionResult result) {
        SwingUtilities.invokeLater(() -> {
            for (Consumer<DetectionResult> listener : detectionListeners) {
                try { listener.accept(result); } catch (Exception ignored) {}
            }
        });
    }

    private void notifyCriticalAlertListeners(SecurityAlert alert) {
        SwingUtilities.invokeLater(() -> {
            for (Consumer<SecurityAlert> listener : criticalAlertListeners) {
                try { listener.accept(alert); } catch (Exception ignored) {}
            }
        });
    }

    // -----------------------------------------------------------------------
    //  Accessors for UI panels
    // -----------------------------------------------------------------------

    public AnomalyDetector      getDetectionService() { return anomalyDetector; }
    public AlertService         getAlertService()     { return alertService; }
    public EventLogService      getEventLogService()  { return eventLogService; }
    public BehaviourMonitorService getMonitorService(){ return monitorService; }
}
