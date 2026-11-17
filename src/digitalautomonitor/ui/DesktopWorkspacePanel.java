package digitalautomonitor.ui;

import digitalautomonitor.service.AlertService;
import digitalautomonitor.service.BehaviourMonitorService;
import digitalautomonitor.service.EventLogService;
import digitalautomonitor.ui.components.CustomUITheme;
import digitalautomonitor.ui.dialogs.InternalAlertsFrame;
import digitalautomonitor.ui.dialogs.InternalEventMonitorFrame;
import digitalautomonitor.ui.dialogs.InternalProfileFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * MDI Workspace demonstrating JDesktopPane and JInternalFrame windows.
 * Houses floating windows for Live Events, Profiles, and Alert Triage.
 */
public class DesktopWorkspacePanel extends JPanel {

    private final EventLogService eventLogService;
    private final AlertService alertService;
    private final BehaviourMonitorService monitorService;

    private JDesktopPane desktopPane;
    private InternalEventMonitorFrame eventFrame;
    private InternalProfileFrame profileFrame;
    private InternalAlertsFrame alertsFrame;

    public DesktopWorkspacePanel(EventLogService eventLogService, AlertService alertService,
                                 BehaviourMonitorService monitorService) {
        this.eventLogService = eventLogService;
        this.alertService = alertService;
        this.monitorService = monitorService;

        setLayout(new BorderLayout());
        setBackground(CustomUITheme.BG_DARK);

        initUI();
    }

    private void initUI() {
        // TOP CONTROL TOOLBAR
        JPanel toolBar = CustomUITheme.createCardPanel();
        toolBar.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 4));

        JLabel title = new JLabel("MDI INTERNAL WORKSPACE:");
        title.setFont(CustomUITheme.FONT_BODY_BOLD);
        title.setForeground(CustomUITheme.ACCENT_CYAN);

        JButton btnSpawnEvents = CustomUITheme.createStyledButton("Open Live Event Monitor", CustomUITheme.ACCENT_CYAN);
        JButton btnSpawnProfile = CustomUITheme.createStyledButton("Open Profile Inspector", CustomUITheme.ACCENT_BLUE);
        JButton btnSpawnAlerts = CustomUITheme.createStyledButton("Open Alert Triage", CustomUITheme.WARNING_YELLOW);
        JButton btnTile = CustomUITheme.createStyledButton("Cascade Windows", CustomUITheme.BORDER_COLOR);

        btnSpawnEvents.addActionListener(e -> openEventMonitor());
        btnSpawnProfile.addActionListener(e -> openProfileInspector());
        btnSpawnAlerts.addActionListener(e -> openAlertTriage());
        btnTile.addActionListener(e -> cascadeWindows());

        toolBar.add(title);
        toolBar.add(btnSpawnEvents);
        toolBar.add(btnSpawnProfile);
        toolBar.add(btnSpawnAlerts);
        toolBar.add(btnTile);

        add(toolBar, BorderLayout.NORTH);

        // DESKTOP PANE
        desktopPane = new JDesktopPane() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                // Custom dark gradient cyber grid background
                g2.setColor(CustomUITheme.BG_DARK);
                g2.fillRect(0, 0, getWidth(), getHeight());

                g2.setColor(new Color(30, 41, 59, 80));
                for (int x = 0; x < getWidth(); x += 40) {
                    g2.drawLine(x, 0, x, getHeight());
                }
                for (int y = 0; y < getHeight(); y += 40) {
                    g2.drawLine(0, y, getWidth(), y);
                }
                g2.dispose();
            }
        };

        add(desktopPane, BorderLayout.CENTER);

        // Open initial windows
        openEventMonitor();
        openProfileInspector();
        openAlertTriage();
    }

    public void openEventMonitor() {
        if (eventFrame == null || eventFrame.isClosed()) {
            eventFrame = new InternalEventMonitorFrame(eventLogService);
            desktopPane.add(eventFrame);
            eventFrame.setVisible(true);
        } else {
            try {
                eventFrame.setIcon(false);
                eventFrame.setSelected(true);
            } catch (Exception ignored) {
            }
        }
    }

    public void openProfileInspector() {
        if (profileFrame == null || profileFrame.isClosed()) {
            profileFrame = new InternalProfileFrame(monitorService);
            desktopPane.add(profileFrame);
            profileFrame.setVisible(true);
        } else {
            try {
                profileFrame.setIcon(false);
                profileFrame.setSelected(true);
            } catch (Exception ignored) {
            }
        }
    }

    public void openAlertTriage() {
        if (alertsFrame == null || alertsFrame.isClosed()) {
            alertsFrame = new InternalAlertsFrame(alertService);
            desktopPane.add(alertsFrame);
            alertsFrame.setVisible(true);
        } else {
            try {
                alertsFrame.setIcon(false);
                alertsFrame.setSelected(true);
            } catch (Exception ignored) {
            }
        }
    }

    private void cascadeWindows() {
        JInternalFrame[] frames = desktopPane.getAllFrames();
        int x = 20;
        int y = 20;
        for (JInternalFrame f : frames) {
            if (!f.isClosed() && !f.isIcon()) {
                f.setLocation(x, y);
                x += 35;
                y += 35;
            }
        }
    }
}
