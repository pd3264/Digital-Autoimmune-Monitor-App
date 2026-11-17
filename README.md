# Digital Autoimmune Monitor
### Behaviour-Based System Anomaly Detection and Security Monitoring Platform

A cybersecurity monitoring platform inspired by the human biological immune system. Built entirely with **pure Java, Java Swing, JFrame, and Java2D (AWT)** with zero external dependencies.

---

## 🧬 Biological Immune System vs. Digital Security Metaphor

| Human Immune System | Digital Autoimmune Monitor | Operational Role |
| :--- | :--- | :--- |
| **Immune System** | Security Monitoring Engine | Autonomous central orchestrator & telemetry engine |
| **Normal Cells** | Normal System Behaviour | Baseline operations within healthy thresholds |
| **Antibodies** | Detection Rules (`DetectionRule`) | Specialized heuristic engines evaluating vectors |
| **Antigens** | Suspicious Events (`BehaviourEvent`) | Observed activity flagged for variance analysis |
| **Inflammation** | Security Alert (`SecurityAlert`) | Early warning alerts attracting SOC triage |
| **Immune Memory** | Behavioural Baselines (`BehaviourProfile`) | Historical profiles and learned variance tolerances |
| **Pathogen** | Anomalous Activity | Severe multi-vector deviation or malware payload |
| **Immune Response** | Security Response (`Incident`) | Session quarantine, process suspension, containment |
| **Recovery** | Incident Resolution | Restoration of homeostasis and updated baselines |

---

## 🖥️ UI Architecture & Pages

The application provides a modern dark cybersecurity SOC dashboard (1400x850):

1. **Authentication Gate (`LoginFrame`)**: Secure entry portal with level selector and status indicator.
2. **Dashboard (`DashboardPanel`)**: 6 KPI cards, Behavioural Immune Status ratios, custom real-time line chart, and recent security events table.
3. **Behaviour Monitor (`BehaviourMonitorPanel`)**: Real-time progress bars for 9 behaviour categories (CPU, Memory, File Access, Process, Network, USB, Logins, Apps, Session) and behavioural history table.
4. **Anomaly Detection (`AnomalyDetectionPanel`)**: Explainable Anomaly Detection AI reasoning breakdown, sensitivity sliders, threshold spinners, and custom-painted `CircularRiskGauge`.
5. **Security Alerts (`SecurityAlertsPanel`)**: Alert repository with severity badges and in-depth investigation modal (`AlertDetailsDialog`).
6. **User Profiles (`UserProfilesPanel`)**: Enrolled identity management, risk thresholds, and registration dialog (`AddUserDialog`).
7. **Event Logs (`EventLogsPanel`)**: Full telemetry viewer with `JTree` category filtering, `TableRowSorter` / `RowFilter` live search, right-click `JPopupMenu`, and CSV/TXT export.
8. **Threat Intelligence (`ThreatIntelligencePanel`)**: IOC signatures table and formatted HTML threat briefing via `JEditorPane`.
9. **Incident Response (`IncidentResponsePanel`)**: 5-stage lifecycle tracker (`SecurityTimeline`), status actions, and analyst notes log.
10. **System Health (`SystemHealthPanel`)**: Real-time jittering daemon vitals (CPU, Memory, Disk, Network, Security Engine, Collector, DB) with status LEDs.
11. **Reports (`ReportsPanel`)**: Configurable intelligence report generator with formatted TXT export.
12. **Settings (`SettingsPanel`)**: Full preferences tabbed console (`JTabbedPane`) demonstrating `JSlider`, `JSpinner`, `JCheckBox`, `JRadioButton`, `ButtonGroup`, and `JColorChooser`.
13. **About (`AboutPanel`)**: Biological immune comparison matrix and safety disclaimer.
14. **MDI Workspace (`DesktopWorkspacePanel`)**: Demonstrates `JDesktopPane` and `JInternalFrame` floating windows for live events, profile inspection, and alert triage.

---

## 🔑 Login Credentials

* **Username:** `admin`
* **Password:** `admin123`
* **Security Tier:** SecOps Tier 3 (Full SOC Control)

---

## 🛠️ Java Swing & AWT Components Showcased

* **Containers & Windows:** `JFrame`, `JPanel`, `JDesktopPane`, `JInternalFrame`, `JDialog`, `JOptionPane`, `JScrollPane`, `JSplitPane`, `JTabbedPane`
* **Controls & Inputs:** `JButton`, `JToggleButton`, `JCheckBox`, `JRadioButton`, `ButtonGroup`, `JTextField`, `JPasswordField`, `JTextArea`, `JTextPane`, `JEditorPane`, `JComboBox`, `JSlider`, `JSpinner`, `JFormattedTextField`
* **Data & Navigation:** `JTable`, `JTree`, `JMenuBar`, `JMenu`, `JMenuItem`, `JCheckBoxMenuItem`, `JRadioButtonMenuItem`, `JPopupMenu`, `JToolBar`, `JSeparator`
* **Visual & Progress:** `JProgressBar`, `CircularRiskGauge` (Custom Graphics2D), `BehaviourChartPanel` (Custom Graphics2D), `StatusIndicator` (Custom Graphics2D), `SecurityTimeline` (Custom Graphics2D), `StatCard`
* **Dialog Utilities:** `JFileChooser`, `JColorChooser`
* **Layout Managers:** `BorderLayout`, `FlowLayout`, `GridLayout`, `GridBagLayout`, `BoxLayout`, `CardLayout`
* **Concurrency:** `javax.swing.Timer`, `SwingUtilities.invokeLater` (Thread-safe EDT dispatch)

---

## 📦 Production Deployment & Distribution

The application includes an automated packaging engine that compiles and packages the entire platform into a standalone executable JAR in `dist/`.

### Automated Deployment:
```powershell
.\deploy.ps1
```
*(Or on Windows CMD: `deploy.bat`)*

### Distribution Artifacts in `dist/`:
* **`dist/DigitalAutoimmuneMonitor.jar`**: Standalone executable JAR with bundled manifest and classes (zero external dependencies).
* **`dist/DigitalAutoimmuneMonitor.bat`**: One-click launcher for Windows (launches in background with `javaw`).
* **`dist/DigitalAutoimmuneMonitor-Console.bat`**: Windows launcher with live console telemetry and diagnostic output.
* **`dist/DigitalAutoimmuneMonitor.sh`**: Standalone shell script for Linux / macOS environments.
* **`DigitalAutoimmuneMonitor.bat`**: Root-level convenience launcher.

---

## 🚀 How to Run the Application

### Option 1: One-Click Executable Launch (Recommended)
* Double-click **`DigitalAutoimmuneMonitor.bat`** in the project root or in `dist/`.

### Option 2: Run Executable JAR via CLI
```powershell
# Standard GUI Launch (Login Portal)
java -jar dist\DigitalAutoimmuneMonitor.jar

# Direct SOC Dashboard Launch (Bypass Login)
java -jar dist\DigitalAutoimmuneMonitor.jar --dashboard

# Run Built-in Architectural Audit Suite
java -jar dist\DigitalAutoimmuneMonitor.jar --audit
```

### Option 3: IntelliJ IDEA / Eclipse IDE
* **IntelliJ:** Open folder, confirm `src` as Sources Root, run `Main.java`.
* **Eclipse:** Import Existing Projects into Workspace, run `Main.java`.

### Option 4: Development Scripts
```powershell
.\build.ps1   # Compiles all 63 source files into bin/
.\run.ps1     # Runs Main directly from bin/
```

---

## 🛡️ Academic Simulation Notice
*This software is an educational simulation modeling cybersecurity telemetry and immune response heuristics. All activity, alerts, and system health metrics are safely generated in memory without interacting maliciously with host systems, networks, or credentials.*
