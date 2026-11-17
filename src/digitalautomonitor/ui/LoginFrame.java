package digitalautomonitor.ui;

import digitalautomonitor.service.AlertService;
import digitalautomonitor.service.BehaviourMonitorService;
import digitalautomonitor.service.EventLogService;
import digitalautomonitor.service.ReportService;
import digitalautomonitor.service.SimulationService;
import digitalautomonitor.ui.components.CustomUITheme;
import digitalautomonitor.ui.components.StatusIndicator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Authentication and Entry portal for the Digital Autoimmune Monitor.
 */
public class LoginFrame extends JFrame {

    private final SimulationService simulationService;
    private final EventLogService eventLogService;
    private final AlertService alertService;
    private final BehaviourMonitorService monitorService;
    private final ReportService reportService;

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> comboSecurityLevel;
    private JCheckBox chkRemember;

    public LoginFrame(SimulationService simulationService, ReportService reportService) {
        super("Digital Autoimmune Monitor | Authentication");
        this.simulationService = simulationService;
        this.reportService = reportService;
        this.eventLogService = simulationService.getEventLogService();
        this.alertService = simulationService.getAlertService();
        this.monitorService = simulationService.getMonitorService();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 520);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(CustomUITheme.BG_DARK);

        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(CustomUITheme.BG_HEADER);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(new EmptyBorder(24, 20, 20, 20));

        JLabel titleLabel = new JLabel("DIGITAL AUTOIMMUNE MONITOR");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(CustomUITheme.ACCENT_CYAN);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Behaviour-Based System Anomaly Detection");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(CustomUITheme.TEXT_MUTED);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(6));
        headerPanel.add(subtitleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(CustomUITheme.BG_DARK);
        formPanel.setBorder(new EmptyBorder(20, 36, 10, 36));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Username
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35;
        JLabel lblUser = new JLabel("Username:");
        lblUser.setFont(CustomUITheme.FONT_BODY_BOLD);
        lblUser.setForeground(CustomUITheme.TEXT_PRIMARY);
        formPanel.add(lblUser, gbc);

        gbc.gridx = 1; gbc.weightx = 0.65;
        txtUsername = new JTextField("admin", 15);
        txtUsername.setBackground(CustomUITheme.PANEL_BG);
        txtUsername.setForeground(CustomUITheme.TEXT_PRIMARY);
        txtUsername.setCaretColor(CustomUITheme.ACCENT_CYAN);
        txtUsername.setFont(CustomUITheme.FONT_BODY);
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(CustomUITheme.BORDER_COLOR, 1, true),
                new EmptyBorder(6, 8, 6, 8)
        ));
        formPanel.add(txtUsername, gbc);
        row++;

        // Password
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lblPass = new JLabel("Password:");
        lblPass.setFont(CustomUITheme.FONT_BODY_BOLD);
        lblPass.setForeground(CustomUITheme.TEXT_PRIMARY);
        formPanel.add(lblPass, gbc);

        gbc.gridx = 1;
        txtPassword = new JPasswordField("admin123", 15);
        txtPassword.setBackground(CustomUITheme.PANEL_BG);
        txtPassword.setForeground(CustomUITheme.TEXT_PRIMARY);
        txtPassword.setCaretColor(CustomUITheme.ACCENT_CYAN);
        txtPassword.setFont(CustomUITheme.FONT_BODY);
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(CustomUITheme.BORDER_COLOR, 1, true),
                new EmptyBorder(6, 8, 6, 8)
        ));
        formPanel.add(txtPassword, gbc);
        row++;

        // Security level
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lblLevel = new JLabel("Security Level:");
        lblLevel.setFont(CustomUITheme.FONT_BODY_BOLD);
        lblLevel.setForeground(CustomUITheme.TEXT_PRIMARY);
        formPanel.add(lblLevel, gbc);

        gbc.gridx = 1;
        String[] levels = {"SecOps Tier 3 (Full SOC Control)", "Threat Analyst (Read/Investigate)", "Incident Commander (Admin)"};
        comboSecurityLevel = new JComboBox<>(levels);
        comboSecurityLevel.setBackground(CustomUITheme.PANEL_BG);
        comboSecurityLevel.setForeground(CustomUITheme.TEXT_PRIMARY);
        comboSecurityLevel.setFont(CustomUITheme.FONT_BODY);
        formPanel.add(comboSecurityLevel, gbc);
        row++;

        // Remember session
        gbc.gridx = 1; gbc.gridy = row;
        chkRemember = new JCheckBox("Remember secure session", true);
        chkRemember.setBackground(CustomUITheme.BG_DARK);
        chkRemember.setForeground(CustomUITheme.TEXT_MUTED);
        chkRemember.setFont(CustomUITheme.FONT_SMALL);
        formPanel.add(chkRemember, gbc);
        row++;

        // Status Indicator
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        StatusIndicator statusLed = new StatusIndicator(CustomUITheme.HEALTHY_GREEN, "System Status: SECURE");
        formPanel.add(statusLed, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Buttons Footer
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 18));
        buttonPanel.setBackground(CustomUITheme.PANEL_BG);
        buttonPanel.setBorder(new LineBorder(CustomUITheme.BORDER_COLOR, 1, false));

        JButton btnLogin = CustomUITheme.createStyledButton("Authenticate", CustomUITheme.HEALTHY_GREEN);
        JButton btnClear = CustomUITheme.createStyledButton("Clear", CustomUITheme.BORDER_COLOR);
        JButton btnExit = CustomUITheme.createStyledButton("Exit", CustomUITheme.CRITICAL_RED);

        btnLogin.addActionListener(e -> attemptLogin());
        btnClear.addActionListener(e -> {
            txtUsername.setText("");
            txtPassword.setText("");
        });
        btnExit.addActionListener(e -> System.exit(0));

        // Enter key shortcut on password
        txtPassword.addActionListener(e -> attemptLogin());

        buttonPanel.add(btnLogin);
        buttonPanel.add(btnClear);
        buttonPanel.add(btnExit);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void attemptLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a valid username.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Credentials verification: admin / admin123
        if ("admin".equalsIgnoreCase(username) && "admin123".equals(password)) {
            openMainDashboard();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Access Denied: Invalid credentials.\n(Hint: Username: admin | Password: admin123)",
                    "Authentication Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openMainDashboard() {
        dispose();
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame(simulationService, reportService);
            mainFrame.setVisible(true);
        });
    }
}
