package digitalautomonitor.ui.dialogs;

import digitalautomonitor.model.BehaviourProfile;
import digitalautomonitor.model.User;
import digitalautomonitor.service.BehaviourMonitorService;
import digitalautomonitor.ui.components.CustomUITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Floating internal window displaying a user's behavioural baseline profile
 * inside the JDesktopPane workspace. Opens when the user clicks a profile card.
 *
 * Shows: CPU baseline, file access rate, login frequency, and network usage.
 */
public class InternalProfileFrame extends JInternalFrame {

    private final BehaviourMonitorService monitorService;
    private final JComboBox<String> comboUsers;

    // Score display (replaces the deleted CircularRiskGauge component)
    private final JLabel lblScoreValue;

    // Baseline metric labels
    private final JLabel lblCpu;
    private final JLabel lblFiles;
    private final JLabel lblLogins;
    private final JLabel lblNetwork;

    public InternalProfileFrame(BehaviourMonitorService monitorService) {
        super("Behaviour Profile Inspector", true, true, true, true);
        this.monitorService = monitorService;

        setSize(450, 360);
        setLocation(530, 30);
        getContentPane().setBackground(CustomUITheme.BG_DARK);
        setLayout(new BorderLayout());

        // TOP: User selector dropdown
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        topPanel.setBackground(CustomUITheme.PANEL_BG);

        JLabel lblSelect = new JLabel("Entity:");
        lblSelect.setFont(CustomUITheme.FONT_BODY_BOLD);
        lblSelect.setForeground(CustomUITheme.TEXT_PRIMARY);

        comboUsers = new JComboBox<>(monitorService.getAllUsernames().toArray(new String[0]));
        comboUsers.setBackground(CustomUITheme.PANEL_BG_LIGHT);
        comboUsers.setForeground(CustomUITheme.TEXT_PRIMARY);
        comboUsers.addActionListener(e -> updateProfile());

        topPanel.add(lblSelect);
        topPanel.add(comboUsers);
        add(topPanel, BorderLayout.NORTH);

        // CENTER: Score panel (left) + Metrics panel (right)
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        centerPanel.setBackground(CustomUITheme.BG_DARK);
        centerPanel.setBorder(new EmptyBorder(12, 12, 12, 12));

        // LEFT: Large score number (0-100) in place of the circular gauge
        JPanel scoreBox = new JPanel(new BorderLayout(4, 4));
        scoreBox.setBackground(CustomUITheme.PANEL_BG);
        scoreBox.setBorder(new EmptyBorder(10, 10, 10, 10));

        lblScoreValue = new JLabel("75", SwingConstants.CENTER);
        lblScoreValue.setFont(new Font("Segoe UI", Font.BOLD, 52));
        lblScoreValue.setForeground(CustomUITheme.ACCENT_CYAN);

        JLabel lblScoreCaption = new JLabel("BASELINE SCORE", SwingConstants.CENTER);
        lblScoreCaption.setFont(CustomUITheme.FONT_SMALL);
        lblScoreCaption.setForeground(CustomUITheme.TEXT_MUTED);

        scoreBox.add(lblScoreValue, BorderLayout.CENTER);
        scoreBox.add(lblScoreCaption, BorderLayout.SOUTH);
        centerPanel.add(scoreBox);

        // RIGHT: Baseline metric labels
        JPanel metricsPanel = new JPanel(new GridLayout(4, 1, 4, 4));
        metricsPanel.setBackground(CustomUITheme.PANEL_BG);
        metricsPanel.setBorder(new EmptyBorder(8, 10, 8, 10));

        lblCpu     = createMetricLabel("CPU Baseline: 25%");
        lblFiles   = createMetricLabel("File Rate: 15/min");
        lblLogins  = createMetricLabel("Login Freq: 2/hr");
        lblNetwork = createMetricLabel("Network: 120 MB/hr");

        metricsPanel.add(lblCpu);
        metricsPanel.add(lblFiles);
        metricsPanel.add(lblLogins);
        metricsPanel.add(lblNetwork);

        centerPanel.add(metricsPanel);
        add(centerPanel, BorderLayout.CENTER);

        updateProfile();
    }

    private JLabel createMetricLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(CustomUITheme.FONT_BODY);
        l.setForeground(CustomUITheme.TEXT_PRIMARY);
        return l;
    }

    /** Refreshes all displayed values whenever a different user is selected. */
    private void updateProfile() {
        String user = (String) comboUsers.getSelectedItem();
        if (user == null) return;

        BehaviourProfile p = monitorService.getProfile(user);
        User u = monitorService.getUserByUsername(user);

        if (p != null) {
            lblCpu.setText("CPU Baseline: " + p.getBaselineCpuPercent() + "%");
            lblFiles.setText("File Rate: " + p.getBaselineFileAccessCount() + " files/min");
            lblLogins.setText("Login Freq: " + p.getBaselineLoginFrequency() + " logins/day");
            lblNetwork.setText("Network: " + p.getBaselineNetworkMB() + " MB/hr");
        }

        if (u != null) {
            // Show the risk score (0=safest, 100=most risky) in the score box
            int riskScore = 100 - u.getBaselineScore();
            lblScoreValue.setText(String.valueOf(riskScore));

            // Colour changes based on risk level (matches main colour palette)
            if (riskScore >= 80) {
                lblScoreValue.setForeground(CustomUITheme.CRITICAL_RED);
            } else if (riskScore >= 60) {
                lblScoreValue.setForeground(CustomUITheme.WARNING_YELLOW);
            } else {
                lblScoreValue.setForeground(CustomUITheme.ACCENT_CYAN);
            }
        }
    }
}
