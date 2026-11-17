package digitalautomonitor.enums;

/**
 * Categorization of monitored system events and activities.
 */
public enum EventType {
    LOGIN("Authentication", "User authentication, session tokens, and access attempts"),
    FILE_ACCESS("File Access", "File read, write, permission escalation, and volume operations"),
    PROCESS("Process Activity", "Binary execution, thread spawn, privilege changes, and CLI commands"),
    NETWORK("Network Activity", "Socket connections, outbound telemetry, DNS queries, and port scans"),
    USB("USB Activity", "Removable storage mounting, peripheral device connections, and raw block access"),
    APPLICATION("Application Usage", "Software launches, rare binaries, browser extensions, and unauthorized apps"),
    SYSTEM("System Event", "Kernel drivers, service registration, scheduled tasks, and registry mods");

    private final String categoryName;
    private final String description;

    EventType(String categoryName, String description) {
        this.categoryName = categoryName;
        this.description = description;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return categoryName;
    }
}
