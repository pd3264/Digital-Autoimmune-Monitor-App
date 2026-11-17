package digitalautomonitor.ui;

import digitalautomonitor.model.DetectionResult;
import digitalautomonitor.model.SecurityAlert;
import digitalautomonitor.service.AlertService;
import digitalautomonitor.service.BehaviourMonitorService;
import digitalautomonitor.service.EventLogService;
import digitalautomonitor.service.ReportService;
import digitalautomonitor.service.SimulationService;
import digitalautomonitor.ui.components.CustomUITheme;
import digitalautomonitor.ui.components.StatusIndicator;
import digitalautomonitor.ui.dialogs.AlertDetailsDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Main Window for Digital Autoimmune Monitor.
 * Incorporates SOC Header, Navigation Sidebar, CardLayout Content Area,
 * MenuBar, ToolBar, Status Footer, and Critical Alert Notification system.
 */
public class MainFrame extends JFrame {

    private final SimulationService simulationService;
    private final EventLogService eventLogService;
    private final AlertService alertService;
    private final BehaviourMonitorService monitorService;
    private final ReportService reportService;

    // Navigation and Page Containers
    private JPanel mainContentPanel;
    private CardLayout cardLayout;
    private final Map<String, JButton> navButtons = new HashMap<>();
    private String activePage = "Dashboard";

    // Header Components
    private JLabel lblClock;
    private StatusIndicator headerStatusIndicator;
    private JButton btnNotificationBadge;

    // Status Footer Components
    private JProgressBar footerProgress;
    private JLabel lblFooterStatus;
    private JLabel lblFooterMemory;

    // Panels
    private DashboardPanel dashboardPanel;
    private BehaviourMonitorPanel behaviourMonitorPanel;
    private AnomalyDetectionPanel anomalyDetectionPanel;
    private SecurityAlertsPanel securityAlertsPanel;
    private UserProfilesPanel userProfilesPanel;
    private EventLogsPanel eventLogsPanel;
    private ThreatIntelligencePanel threatIntelligencePanel;
    private IncidentResponsePanel incidentResponsePanel;
    private SystemHealthPanel systemHealthPanel;
    private ReportsPanel reportsPanel;
    private SettingsPanel settingsPanel;
    private AboutPanel aboutPanel;
    private DesktopWorkspacePanel desktopWorkspacePanel;

    public MainFrame(SimulationService simulationService, ReportService reportService) {
        super("Digital Autoimmune Monitor | Behaviour-Based Security Platform");
        this.simulationService = simulationService;
        this.reportService = reportService;
        this.eventLogService = simulationService.getEventLogService();
        this.alertService = simulationService.getAlertService();
        this.monitorService = simulationService.getMonitorService();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 850);
        setMinimumSize(new Dimension(1100, 700));
        setLocationRelativeTo(null);
        getContentPane().setBackground(CustomUITheme.BG_DARK);

        initMenuBar();
        initUI();
        startClockTimer();
        registerCriticalAlertNotifier();
    }

    private void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(CustomUITheme.BG_HEADER);
        menuBar.setBorder(new LineBorder(CustomUITheme.BORDER_COLOR, 1));

        // 1. FILE MENU
        JMenu menuFile = createStyledMenu("File", KeyEvent.VK_F);
        JMenuItem itemNewSession = createStyledMenuItem("New Session", KeyEvent.VK_N, KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK));
        JMenuItem itemExportLogs = createStyledMenuItem("Export Logs...", KeyEvent.VK_E, KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.CTRL_DOWN_MASK));
        JMenuItem itemGenerateReport = createStyledMenuItem("Generate Report...", KeyEvent.VK_G, KeyStroke.getKeyStroke(KeyEvent.VK_G, KeyEvent.CTRL_DOWN_MASK));
        JMenuItem itemExit = createStyledMenuItem("Exit Platform", KeyEvent.VK_X, KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_DOWN_MASK));

        itemNewSession.addActionListener(e -> JOptionPane.showMessageDialog(this, "Session state refreshed.", "New Session", JOptionPane.INFORMATION_MESSAGE));
        itemExportLogs.addActionListener(e -> exportLogsAction());
        itemGenerateReport.addActionListener(e -> showPage("Reports"));
        itemExit.addActionListener(e -> System.exit(0));

        menuFile.add(itemNewSession);
        menuFile.add(itemExportLogs);
        menuFile.add(itemGenerateReport);
        menuFile.addSeparator();
        menuFile.add(itemExit);

        // 2. MONITOR MENU
        JMenu menuMonitor = createStyledMenu("Monitor", KeyEvent.VK_M);
        JMenuItem itemStart = createStyledMenuItem("Start Monitoring", KeyEvent.VK_S, KeyStroke.getKeyStroke(KeyEvent.VK_M, KeyEvent.CTRL_DOWN_MASK));
        JMenuItem itemPause = createStyledMenuItem("Pause Monitoring", KeyEvent.VK_P, KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.CTRL_DOWN_MASK));
        JMenuItem itemResetBase = createStyledMenuItem("Reset All Baselines", KeyEvent.VK_R, null);

        itemStart.addActionListener(e -> {
            simulationService.startMonitoring();
            updateFooterMonitoringState();
            JOptionPane.showMessageDialog(this, "Immune monitoring surveillance started.", "Watchdog Active", JOptionPane.INFORMATION_MESSAGE);
        });
        itemPause.addActionListener(e -> {
            simulationService.pauseMonitoring();
            updateFooterMonitoringState();
            JOptionPane.showMessageDialog(this, "Immune monitoring surveillance paused.", "Watchdog Suspended", JOptionPane.WARNING_MESSAGE);
        });
        itemResetBase.addActionListener(e -> {
            for (String u : monitorService.getAllUsernames()) {
                monitorService.resetBaseline(u);
            }
            JOptionPane.showMessageDialog(this, "All user baselines reset to homeostatic defaults.", "Reset Complete", JOptionPane.INFORMATION_MESSAGE);
        });

        menuMonitor.add(itemStart);
        menuMonitor.add(itemPause);
        menuMonitor.add(itemResetBase);

        // 3. VIEW MENU
        JMenu menuView = createStyledMenu("View", KeyEvent.VK_V);
        JMenuItem itemViewDash = createStyledMenuItem("Dashboard View", KeyEvent.VK_D, KeyStroke.getKeyStroke(KeyEvent.VK_1, KeyEvent.CTRL_DOWN_MASK));
        JMenuItem itemViewAlerts = createStyledMenuItem("Security Alerts", KeyEvent.VK_A, KeyStroke.getKeyStroke(KeyEvent.VK_2, KeyEvent.CTRL_DOWN_MASK));
        JMenuItem itemViewEvents = createStyledMenuItem("Event Logs", KeyEvent.VK_L, KeyStroke.getKeyStroke(KeyEvent.VK_3, KeyEvent.CTRL_DOWN_MASK));
        JMenuItem itemViewMdi = createStyledMenuItem("Floating Windows (MDI)", KeyEvent.VK_W, KeyStroke.getKeyStroke(KeyEvent.VK_W, KeyEvent.CTRL_DOWN_MASK));

        JCheckBoxMenuItem chkAutoRefresh = new JCheckBoxMenuItem("Live Auto-Refresh Stream", true);
        styleMenuSubItem(chkAutoRefresh);

        JMenu menuDensity = new JMenu("View Density");
        styleMenuSubItem(menuDensity);
        JRadioButtonMenuItem rbNormal = new JRadioButtonMenuItem("Standard SOC View", true);
        JRadioButtonMenuItem rbCompact = new JRadioButtonMenuItem("Compact Grid View", false);
        styleMenuSubItem(rbNormal);
        styleMenuSubItem(rbCompact);
        ButtonGroup bgDensity = new ButtonGroup();
        bgDensity.add(rbNormal);
        bgDensity.add(rbCompact);
        menuDensity.add(rbNormal);
        menuDensity.add(rbCompact);

        itemViewDash.addActionListener(e -> showPage("Dashboard"));
        itemViewAlerts.addActionListener(e -> showPage("Security Alerts"));
        itemViewEvents.addActionListener(e -> showPage("Event Logs"));
        itemViewMdi.addActionListener(e -> showPage("Floating Windows"));

        menuView.add(itemViewDash);
        menuView.add(itemViewAlerts);
        menuView.add(itemViewEvents);
        menuView.add(itemViewMdi);
        menuView.addSeparator();
        menuView.add(chkAutoRefresh);
        menuView.add(menuDensity);

        // 4. TOOLS MENU
        JMenu menuTools = createStyledMenu("Tools", KeyEvent.VK_T);
        JMenuItem itemRunDetection = createStyledMenuItem("Run Anomaly Detection", KeyEvent.VK_R, KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK));
        JMenuItem itemSimEvent = createStyledMenuItem("Simulate Antigen Event", KeyEvent.VK_S, KeyStroke.getKeyStroke(KeyEvent.VK_T, KeyEvent.CTRL_DOWN_MASK));
        JMenuItem itemHealthCheck = createStyledMenuItem("System Health Diagnostic", KeyEvent.VK_H, null);

        itemRunDetection.addActionListener(e -> showPage("Anomaly Detection"));
        itemSimEvent.addActionListener(e -> simulationService.simulateSuspiciousEvent());
        itemHealthCheck.addActionListener(e -> showPage("System Health"));

        menuTools.add(itemRunDetection);
        menuTools.add(itemSimEvent);
        menuTools.add(itemHealthCheck);

        // 5. HELP MENU
        JMenu menuHelp = createStyledMenu("Help", KeyEvent.VK_H);
        JMenuItem itemDoc = createStyledMenuItem("Immune Model Documentation", KeyEvent.VK_D, KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
        JMenuItem itemAbout = createStyledMenuItem("About Platform", KeyEvent.VK_A, null);

        itemDoc.addActionListener(e -> showPage("About"));
        itemAbout.addActionListener(e -> showPage("About"));

        menuHelp.add(itemDoc);
        menuHelp.add(itemAbout);

        menuBar.add(menuFile);
        menuBar.add(menuMonitor);
        menuBar.add(menuView);
        menuBar.add(menuTools);
        menuBar.add(menuHelp);

        setJMenuBar(menuBar);
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // 1. TOP HEADER & TOOLBAR
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setOpaque(false);

        topContainer.add(createHeaderPanel(), BorderLayout.NORTH);
        topContainer.add(createToolBar(), BorderLayout.SOUTH);
        add(topContainer, BorderLayout.NORTH);

        // 2. MAIN CENTER SPLIT (Sidebar on Left, CardLayout on Right)
        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setOpaque(false);

        centerWrapper.add(createSidebarPanel(), BorderLayout.WEST);

        // Content Area with CardLayout
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(CustomUITheme.BG_DARK);

        // Instantiate all 12 pages + 1 MDI workspace
        dashboardPanel = new DashboardPanel(simulationService);
        behaviourMonitorPanel = new BehaviourMonitorPanel(simulationService);
        anomalyDetectionPanel = new AnomalyDetectionPanel(simulationService);
        securityAlertsPanel = new SecurityAlertsPanel(alertService, this);
        userProfilesPanel = new UserProfilesPanel(monitorService, this);
        eventLogsPanel = new EventLogsPanel(eventLogService, alertService);
        threatIntelligencePanel = new ThreatIntelligencePanel();
        incidentResponsePanel = new IncidentResponsePanel();
        systemHealthPanel = new SystemHealthPanel();
        reportsPanel = new ReportsPanel(reportService, eventLogService, alertService);
        settingsPanel = new SettingsPanel(simulationService);
        aboutPanel = new AboutPanel();
        desktopWorkspacePanel = new DesktopWorkspacePanel(eventLogService, alertService, monitorService);

        mainContentPanel.add(dashboardPanel, "Dashboard");
        mainContentPanel.add(behaviourMonitorPanel, "Behaviour Monitor");
        mainContentPanel.add(anomalyDetectionPanel, "Anomaly Detection");
        mainContentPanel.add(securityAlertsPanel, "Security Alerts");
        mainContentPanel.add(userProfilesPanel, "User Profiles");
        mainContentPanel.add(eventLogsPanel, "Event Logs");
        mainContentPanel.add(threatIntelligencePanel, "Threat Intelligence");
        mainContentPanel.add(incidentResponsePanel, "Incident Response");
        mainContentPanel.add(systemHealthPanel, "System Health");
        mainContentPanel.add(reportsPanel, "Reports");
        mainContentPanel.add(settingsPanel, "Settings");
        mainContentPanel.add(aboutPanel, "About");
        mainContentPanel.add(desktopWorkspacePanel, "Floating Windows");

        centerWrapper.add(mainContentPanel, BorderLayout.CENTER);
        add(centerWrapper, BorderLayout.CENTER);

        // 3. STATUS FOOTER
        add(createFooterPanel(), BorderLayout.SOUTH);

        showPage("Dashboard");
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout(14, 0));
        header.setBackground(CustomUITheme.BG_HEADER);
        header.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(CustomUITheme.BORDER_COLOR, 1),
                new EmptyBorder(8, 16, 8, 16)
        ));

        // LEFT: Title with Glowing Dot
        JPanel leftTitle = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftTitle.setOpaque(false);

        JLabel logoIcon = new JLabel("●");
        logoIcon.setFont(new Font("Segoe UI", Font.BOLD, 20));
        logoIcon.setForeground(CustomUITheme.ACCENT_CYAN);

        JLabel appTitle = new JLabel("DIGITAL AUTOIMMUNE MONITOR");
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        appTitle.setForeground(CustomUITheme.TEXT_PRIMARY);

        leftTitle.add(logoIcon);
        leftTitle.add(appTitle);
        header.add(leftTitle, BorderLayout.WEST);

        // MIDDLE: Subtitle badge
        JPanel middlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        middlePanel.setOpaque(false);
        JLabel subBadge = new JLabel("BEHAVIOUR ANALYSIS ENGINE | SOC DEFENSE CONSOLE");
        subBadge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        subBadge.setForeground(CustomUITheme.TEXT_MUTED);
        middlePanel.add(subBadge);
        header.add(middlePanel, BorderLayout.CENTER);

        // RIGHT: Status, Clock, Notification Badge, User
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        rightPanel.setOpaque(false);

        headerStatusIndicator = new StatusIndicator(CustomUITheme.HEALTHY_GREEN, "SHIELD ACTIVE");

        lblClock = new JLabel();
        lblClock.setFont(CustomUITheme.FONT_MONO);
        lblClock.setForeground(CustomUITheme.TEXT_PRIMARY);
        updateClock();

        btnNotificationBadge = CustomUITheme.createStyledButton("Alerts: " + alertService.getActiveAlertsCount(), CustomUITheme.WARNING_YELLOW);
        btnNotificationBadge.setFont(CustomUITheme.FONT_SMALL);
        btnNotificationBadge.addActionListener(e -> showPage("Security Alerts"));

        JLabel userBadge = new JLabel("admin (SecOps Lead)");
        userBadge.setFont(CustomUITheme.FONT_BODY_BOLD);
        userBadge.setForeground(CustomUITheme.ACCENT_CYAN);

        rightPanel.add(headerStatusIndicator);
        rightPanel.add(lblClock);
        rightPanel.add(btnNotificationBadge);
        rightPanel.add(userBadge);

        header.add(rightPanel, BorderLayout.EAST);
        return header;
    }

    private JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setBackground(CustomUITheme.PANEL_BG);
        toolBar.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(CustomUITheme.BORDER_COLOR, 1),
                new EmptyBorder(4, 10, 4, 10)
        ));

        JButton btnStart = createToolBarButton("Start Monitoring", "Resume automated immune surveillance", e -> {
            simulationService.startMonitoring();
            updateFooterMonitoringState();
        });
        JButton btnPause = createToolBarButton("Pause", "Temporarily pause autonomous loop", e -> {
            simulationService.pauseMonitoring();
            updateFooterMonitoringState();
        });
        JButton btnRunDetect = createToolBarButton("Run Detection", "Perform immediate heuristic anomaly analysis", e -> showPage("Anomaly Detection"));
        JButton btnRefresh = createToolBarButton("Refresh", "Refresh telemetry caches", e -> {
            dashboardPanel.refreshTableData();
            securityAlertsPanel.refreshAlertTable();
            eventLogsPanel.refreshTableData();
        });
        JButton btnExport = createToolBarButton("Export", "Export captured logs to CSV/TXT", e -> exportLogsAction());
        JButton btnSettings = createToolBarButton("Settings", "Configure detection and theme preferences", e -> showPage("Settings"));
        JButton btnMdi = createToolBarButton("MDI Windows", "Open floating internal window desktop", e -> showPage("Floating Windows"));

        toolBar.add(btnStart);
        toolBar.add(btnPause);
        toolBar.addSeparator(new Dimension(10, 20));
        toolBar.add(btnRunDetect);
        toolBar.add(btnRefresh);
        toolBar.add(btnExport);
        toolBar.addSeparator(new Dimension(10, 20));
        toolBar.add(btnSettings);
        toolBar.add(btnMdi);

        return toolBar;
    }

    private JButton createToolBarButton(String text, String tooltip, ActionListener listener) {
        JButton btn = CustomUITheme.createStyledButton(text, CustomUITheme.BORDER_COLOR);
        btn.setToolTipText(tooltip);
        btn.setFont(CustomUITheme.FONT_SMALL);
        btn.addActionListener(listener);
        return btn;
    }

    private JPanel createSidebarPanel() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(CustomUITheme.BG_HEADER);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(CustomUITheme.BORDER_COLOR, 1),
                new EmptyBorder(10, 8, 10, 8)
        ));
        sidebar.setPreferredSize(new Dimension(210, 0));

        String[] pages = {
                "Dashboard", "Behaviour Monitor", "Anomaly Detection", "Security Alerts",
                "User Profiles", "Event Logs", "Threat Intelligence", "Incident Response",
                "System Health", "Reports", "Settings", "About", "Floating Windows"
        };

        for (String page : pages) {
            JButton btn = new JButton(page);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setMaximumSize(new Dimension(195, 36));
            btn.setPreferredSize(new Dimension(195, 36));
            btn.setFont(CustomUITheme.FONT_BODY_BOLD);
            btn.setForeground(CustomUITheme.TEXT_MUTED);
            btn.setBackground(CustomUITheme.BG_HEADER);
            btn.setFocusPainted(false);
            btn.setHorizontalAlignment(SwingConstants.LEFT);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setBorder(new EmptyBorder(6, 14, 6, 14));

            btn.addActionListener(e -> showPage(page));
            navButtons.put(page, btn);
            sidebar.add(btn);
            sidebar.add(Box.createVerticalStrut(3));
        }

        sidebar.add(Box.createVerticalGlue());

        // Quick Stimulus buttons at sidebar bottom
        JPanel quickSimBox = new JPanel();
        quickSimBox.setOpaque(false);
        quickSimBox.setLayout(new GridLayout(3, 1, 0, 4));
        quickSimBox.setMaximumSize(new Dimension(195, 100));

        JButton btnSim1 = CustomUITheme.createStyledButton("+ Sim Normal", CustomUITheme.HEALTHY_GREEN);
        JButton btnSim2 = CustomUITheme.createStyledButton("+ Sim Warning", CustomUITheme.WARNING_YELLOW);
        JButton btnSim3 = CustomUITheme.createStyledButton("+ Sim Critical", CustomUITheme.CRITICAL_RED);

        btnSim1.setFont(CustomUITheme.FONT_SMALL);
        btnSim2.setFont(CustomUITheme.FONT_SMALL);
        btnSim3.setFont(CustomUITheme.FONT_SMALL);

        btnSim1.addActionListener(e -> simulationService.simulateNormalEvent());
        btnSim2.addActionListener(e -> simulationService.simulateSuspiciousEvent());
        btnSim3.addActionListener(e -> simulationService.simulateCriticalEvent());

        quickSimBox.add(btnSim1);
        quickSimBox.add(btnSim2);
        quickSimBox.add(btnSim3);
        sidebar.add(quickSimBox);

        return sidebar;
    }

    private JPanel createFooterPanel() {
        JPanel footer = new JPanel(new BorderLayout(14, 0));
        footer.setBackground(CustomUITheme.BG_HEADER);
        footer.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(CustomUITheme.BORDER_COLOR, 1),
                new EmptyBorder(5, 16, 5, 16)
        ));

        // Left status & progress
        JPanel leftFooter = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftFooter.setOpaque(false);

        lblFooterStatus = new JLabel("MONITORING: ACTIVE | HEURISTICS 5 RULES ARMED");
        lblFooterStatus.setFont(CustomUITheme.FONT_SMALL);
        lblFooterStatus.setForeground(CustomUITheme.HEALTHY_GREEN);

        footerProgress = new JProgressBar();
        footerProgress.setIndeterminate(true);
        footerProgress.setPreferredSize(new Dimension(100, 10));
        CustomUITheme.styleProgressBar(footerProgress, CustomUITheme.ACCENT_CYAN);

        leftFooter.add(lblFooterStatus);
        leftFooter.add(footerProgress);
        footer.add(leftFooter, BorderLayout.WEST);

        // Center: Engine version
        JLabel lblCenter = new JLabel("Java 17+ Pure Swing Engine | Academic SOC Simulation Mode");
        lblCenter.setFont(CustomUITheme.FONT_SMALL);
        lblCenter.setForeground(CustomUITheme.TEXT_MUTED);
        JPanel centerFooter = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        centerFooter.setOpaque(false);
        centerFooter.add(lblCenter);
        footer.add(centerFooter, BorderLayout.CENTER);

        // Right memory
        long free = Runtime.getRuntime().freeMemory() / (1024 * 1024);
        long total = Runtime.getRuntime().totalMemory() / (1024 * 1024);
        lblFooterMemory = new JLabel(String.format("JVM Heap: %dMB / %dMB", total - free, total));
        lblFooterMemory.setFont(CustomUITheme.FONT_SMALL);
        lblFooterMemory.setForeground(CustomUITheme.TEXT_MUTED);

        JPanel rightFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightFooter.setOpaque(false);
        rightFooter.add(lblFooterMemory);
        footer.add(rightFooter, BorderLayout.EAST);

        return footer;
    }

    public void showPage(String pageName) {
        this.activePage = pageName;
        cardLayout.show(mainContentPanel, pageName);

        // Highlight selected nav button
        for (Map.Entry<String, JButton> entry : navButtons.entrySet()) {
            JButton btn = entry.getValue();
            if (entry.getKey().equals(pageName)) {
                btn.setBackground(CustomUITheme.PANEL_BG);
                btn.setForeground(CustomUITheme.ACCENT_CYAN);
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 3, 0, 0, CustomUITheme.ACCENT_CYAN),
                        new EmptyBorder(6, 11, 6, 14)
                ));
            } else {
                btn.setBackground(CustomUITheme.BG_HEADER);
                btn.setForeground(CustomUITheme.TEXT_MUTED);
                btn.setBorder(new EmptyBorder(6, 14, 6, 14));
            }
        }
    }

    private void startClockTimer() {
        Timer timer = new Timer(1000, e -> {
            updateClock();
            long free = Runtime.getRuntime().freeMemory() / (1024 * 1024);
            long total = Runtime.getRuntime().totalMemory() / (1024 * 1024);
            lblFooterMemory.setText(String.format("JVM Heap: %dMB / %dMB", total - free, total));
            btnNotificationBadge.setText("Alerts: " + alertService.getActiveAlertsCount());
        });
        timer.start();
    }

    private void updateClock() {
        String dt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss  |  dd MMM yyyy"));
        lblClock.setText(dt);
    }

    private void updateFooterMonitoringState() {
        boolean active = simulationService.isMonitoring();
        lblFooterStatus.setText(active ? "MONITORING: ACTIVE | HEURISTICS 5 RULES ARMED" : "MONITORING: PAUSED | SURVEILLANCE SUSPENDED");
        lblFooterStatus.setForeground(active ? CustomUITheme.HEALTHY_GREEN : CustomUITheme.WARNING_YELLOW);
        footerProgress.setIndeterminate(active);
        headerStatusIndicator.setStatus(active ? CustomUITheme.HEALTHY_GREEN : CustomUITheme.WARNING_YELLOW,
                active ? "SHIELD ACTIVE" : "SHIELD PAUSED");
    }

    private void registerCriticalAlertNotifier() {
        simulationService.addCriticalAlertListener(alert -> {
            // Requirement 30: When a critical anomaly occurs, display notification
            Object[] options = {"Investigate Alert", "Dismiss"};
            String msg = String.format(
                    "CRITICAL SECURITY EVENT DETECTED (ACUTE PATHOGEN)\n\n" +
                    "Affected User:    %s\n" +
                    "Severity Tier:    %s\n" +
                    "Vector Category:  %s\n" +
                    "Description:      %s\n\n" +
                    "Reason: %s\n" +
                    "Prescriptive: %s",
                    alert.getUsername(), alert.getSeverity().getDisplayName(),
                    alert.getCategory().getCategoryName(), alert.getDescription(),
                    alert.getDetectionReason(), alert.getRecommendedAction()
            );

            int choice = JOptionPane.showOptionDialog(this, msg,
                    "CRITICAL SECURITY ANOMALY ALERT | " + alert.getAlertId(),
                    JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[0]);

            if (choice == JOptionPane.YES_OPTION) {
                AlertDetailsDialog dlg = new AlertDetailsDialog(this, alert, alertService);
                dlg.setVisible(true);
            }
        });
    }

    private void exportLogsAction() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("autoimmune_logs_" + System.currentTimeMillis() + ".csv"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                eventLogService.exportToCsv(chooser.getSelectedFile());
                JOptionPane.showMessageDialog(this, "Logs exported to:\n" + chooser.getSelectedFile().getAbsolutePath(),
                        "Export Complete", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JMenu createStyledMenu(String title, int mnemonic) {
        JMenu m = new JMenu(title);
        m.setMnemonic(mnemonic);
        m.setForeground(CustomUITheme.TEXT_PRIMARY);
        m.setFont(CustomUITheme.FONT_BODY_BOLD);
        return m;
    }

    private JMenuItem createStyledMenuItem(String title, int mnemonic, KeyStroke accelerator) {
        JMenuItem item = new JMenuItem(title);
        item.setMnemonic(mnemonic);
        if (accelerator != null) item.setAccelerator(accelerator);
        styleMenuSubItem(item);
        return item;
    }

    private void styleMenuSubItem(JMenuItem item) {
        item.setBackground(CustomUITheme.PANEL_BG);
        item.setForeground(CustomUITheme.TEXT_PRIMARY);
        item.setFont(CustomUITheme.FONT_BODY);
        item.setBorder(new EmptyBorder(4, 10, 4, 10));
    }
}
