package digitalautomonitor.service;

import digitalautomonitor.enums.RiskLevel;
import digitalautomonitor.model.BehaviourProfile;
import digitalautomonitor.model.User;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Manages user identities and baseline profiles (Immune Memory).
 */
public class BehaviourMonitorService {

    private final Map<String, BehaviourProfile> profiles = new HashMap<>();
    private final List<User> users = new ArrayList<>();
    private final Set<String> monitoredUsers = new HashSet<>();
    private final List<Runnable> listeners = new ArrayList<>();

    public BehaviourMonitorService() {
        seedUsers();
    }

    private void seedUsers() {
        LocalDateTime now = LocalDateTime.now();

        addUserInternal(new User("USR-01", "admin", "SecOps Lead", "Security Operations", 92, RiskLevel.LOW, now.minusMinutes(1), true, 75));
        addUserInternal(new User("USR-02", "user01", "Systems Engineer", "Infrastructure", 88, RiskLevel.LOW, now.minusMinutes(4), true, 70));
        addUserInternal(new User("USR-03", "user02", "Software Developer", "Engineering", 84, RiskLevel.MEDIUM, now.minusMinutes(12), true, 65));
        addUserInternal(new User("USR-04", "user03", "Data Scientist", "Research & Analytics", 58, RiskLevel.CRITICAL, now.minusMinutes(7), true, 60));
        addUserInternal(new User("USR-05", "user04", "DevOps Engineer", "Cloud Platforms", 94, RiskLevel.NORMAL, now.minusMinutes(19), true, 70));
        addUserInternal(new User("USR-06", "user05", "Product Manager", "Product Development", 78, RiskLevel.MEDIUM, now.minusMinutes(21), true, 65));
        addUserInternal(new User("USR-07", "user06", "QA Analyst", "Engineering", 90, RiskLevel.NORMAL, now.minusMinutes(40), true, 70));
        addUserInternal(new User("USR-08", "user07", "Finance Manager", "Corporate Finance", 65, RiskLevel.HIGH, now.minusMinutes(15), true, 55));
        addUserInternal(new User("USR-09", "user08", "Database Admin", "Data Engineering", 95, RiskLevel.NORMAL, now.minusMinutes(3), true, 75));
        addUserInternal(new User("USR-10", "user09", "Contractor", "External Vendor", 52, RiskLevel.HIGH, now.minusMinutes(9), true, 50));
        addUserInternal(new User("USR-11", "user10", "HR Specialist", "People & Culture", 91, RiskLevel.NORMAL, now.minusMinutes(55), true, 60));
        addUserInternal(new User("USR-12", "user11", "Network Admin", "IT Operations", 89, RiskLevel.NORMAL, now.minusMinutes(2), true, 75));
    }

    private void addUserInternal(User user) {
        users.add(user);
        BehaviourProfile profile = new BehaviourProfile(user.getUsername());
        profiles.put(user.getUsername().toLowerCase(), profile);
        if (user.isMonitoringEnabled()) {
            monitoredUsers.add(user.getUsername().toLowerCase());
        }
    }

    public synchronized void addUser(User user) {
        addUserInternal(user);
        notifyListeners();
    }

    public synchronized boolean removeUser(String userId) {
        Iterator<User> it = users.iterator();
        while (it.hasNext()) {
            User u = it.next();
            if (u.getUserId().equalsIgnoreCase(userId)) {
                it.remove();
                profiles.remove(u.getUsername().toLowerCase());
                monitoredUsers.remove(u.getUsername().toLowerCase());
                notifyListeners();
                return true;
            }
        }
        return false;
    }

    public synchronized User getUserByUsername(String username) {
        if (username == null) return null;
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(username)) {
                return u;
            }
        }
        return null;
    }

    public synchronized BehaviourProfile getProfile(String username) {
        if (username == null) return null;
        BehaviourProfile p = profiles.get(username.toLowerCase());
        if (p == null) {
            p = new BehaviourProfile(username);
            profiles.put(username.toLowerCase(), p);
        }
        return p;
    }

    public synchronized void resetBaseline(String username) {
        BehaviourProfile p = getProfile(username);
        if (p != null) {
            p.resetToHealthyBaseline();
        }
        User u = getUserByUsername(username);
        if (u != null) {
            u.setBaselineScore(90);
            u.setRiskLevel(RiskLevel.NORMAL);
        }
        notifyListeners();
    }

    public synchronized List<User> getAllUsers() {
        return Collections.unmodifiableList(new ArrayList<>(users));
    }

    public synchronized List<String> getAllUsernames() {
        List<String> names = new ArrayList<>();
        for (User u : users) {
            names.add(u.getUsername());
        }
        return names;
    }

    public synchronized int getMonitoredUsersCount() {
        return monitoredUsers.size();
    }

    public synchronized void toggleMonitoring(String username, boolean enabled) {
        User u = getUserByUsername(username);
        if (u != null) {
            u.setMonitoringEnabled(enabled);
        }
        if (enabled) {
            monitoredUsers.add(username.toLowerCase());
        } else {
            monitoredUsers.remove(username.toLowerCase());
        }
        notifyListeners();
    }

    public synchronized void exportBaseline(File file) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("# DIGITAL AUTOIMMUNE MONITOR - BEHAVIOURAL BASELINE REPOSITORY\n");
            writer.write("# Format: Username,LoginFreq,FileAccess,ProcessCount,NetworkMB,CPUPercent,MemPercent\n");
            for (Map.Entry<String, BehaviourProfile> entry : profiles.entrySet()) {
                BehaviourProfile p = entry.getValue();
                writer.write(String.format("%s,%d,%d,%d,%d,%d,%d\n",
                        p.getUsername(), p.getBaselineLoginFrequency(), p.getBaselineFileAccessCount(),
                        p.getBaselineProcessCount(), p.getBaselineNetworkMB(), p.getBaselineCpuPercent(),
                        p.getBaselineMemoryPercent()));
            }
        }
    }

    public synchronized void importBaseline(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] parts = line.split(",");
                if (parts.length >= 7) {
                    String username = parts[0].trim();
                    BehaviourProfile p = getProfile(username);
                    p.setBaselineLoginFrequency(Integer.parseInt(parts[1].trim()));
                    p.setBaselineFileAccessCount(Integer.parseInt(parts[2].trim()));
                    p.setBaselineProcessCount(Integer.parseInt(parts[3].trim()));
                    p.setBaselineNetworkMB(Integer.parseInt(parts[4].trim()));
                    p.setBaselineCpuPercent(Integer.parseInt(parts[5].trim()));
                    p.setBaselineMemoryPercent(Integer.parseInt(parts[6].trim()));
                }
            }
        }
        notifyListeners();
    }

    public synchronized void addChangeListener(Runnable r) {
        listeners.add(r);
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
