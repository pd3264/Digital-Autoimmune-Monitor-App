package digitalautomonitor.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents the learned behavioural baseline (Immune Memory) for an entity.
 */
public class BehaviourProfile {
    private String username;
    private int baselineLoginFrequency; // logins per day/hour
    private int baselineFileAccessCount; // files per min
    private int baselineProcessCount;
    private int baselineNetworkMB;
    private int baselineCpuPercent;
    private int baselineMemoryPercent;
    private int baselineSessionDurationMin;
    private final Set<String> authorizedApplications = new HashSet<>();

    public BehaviourProfile(String username) {
        this.username = username;
        // Default healthy baselines
        this.baselineLoginFrequency = 2;
        this.baselineFileAccessCount = 15;
        this.baselineProcessCount = 45;
        this.baselineNetworkMB = 120;
        this.baselineCpuPercent = 25;
        this.baselineMemoryPercent = 40;
        this.baselineSessionDurationMin = 180;

        authorizedApplications.add("chrome.exe");
        authorizedApplications.add("code.exe");
        authorizedApplications.add("outlook.exe");
        authorizedApplications.add("teams.exe");
        authorizedApplications.add("cmd.exe");
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getBaselineLoginFrequency() {
        return baselineLoginFrequency;
    }

    public void setBaselineLoginFrequency(int baselineLoginFrequency) {
        this.baselineLoginFrequency = baselineLoginFrequency;
    }

    public int getBaselineFileAccessCount() {
        return baselineFileAccessCount;
    }

    public void setBaselineFileAccessCount(int baselineFileAccessCount) {
        this.baselineFileAccessCount = baselineFileAccessCount;
    }

    public int getBaselineProcessCount() {
        return baselineProcessCount;
    }

    public void setBaselineProcessCount(int baselineProcessCount) {
        this.baselineProcessCount = baselineProcessCount;
    }

    public int getBaselineNetworkMB() {
        return baselineNetworkMB;
    }

    public void setBaselineNetworkMB(int baselineNetworkMB) {
        this.baselineNetworkMB = baselineNetworkMB;
    }

    public int getBaselineCpuPercent() {
        return baselineCpuPercent;
    }

    public void setBaselineCpuPercent(int baselineCpuPercent) {
        this.baselineCpuPercent = baselineCpuPercent;
    }

    public int getBaselineMemoryPercent() {
        return baselineMemoryPercent;
    }

    public void setBaselineMemoryPercent(int baselineMemoryPercent) {
        this.baselineMemoryPercent = baselineMemoryPercent;
    }

    public int getBaselineSessionDurationMin() {
        return baselineSessionDurationMin;
    }

    public void setBaselineSessionDurationMin(int baselineSessionDurationMin) {
        this.baselineSessionDurationMin = baselineSessionDurationMin;
    }

    public Set<String> getAuthorizedApplications() {
        return Collections.unmodifiableSet(authorizedApplications);
    }

    public void addAuthorizedApplication(String app) {
        this.authorizedApplications.add(app.toLowerCase());
    }

    public boolean isAuthorizedApp(String app) {
        if (app == null) return false;
        return authorizedApplications.contains(app.toLowerCase());
    }

    public void resetToHealthyBaseline() {
        this.baselineLoginFrequency = 2;
        this.baselineFileAccessCount = 15;
        this.baselineProcessCount = 45;
        this.baselineNetworkMB = 120;
        this.baselineCpuPercent = 25;
        this.baselineMemoryPercent = 40;
    }
}
