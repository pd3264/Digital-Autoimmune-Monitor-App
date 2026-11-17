package digitalautomonitor.ui;

import digitalautomonitor.service.SimulationService;
import digitalautomonitor.ui.components.CustomUITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Settings Panel organized via JTabbedPane showcasing full Swing control set
 * (JSlider, JSpinner, JCheckBox, JRadioButton, ButtonGroup, JColorChooser, JFormattedTextField).
 */
public class SettingsPanel extends JPanel {

    private final SimulationService simulationService;

    // General
    private JCheckBox chkAutoStart;
    private JCheckBox chkAutoRefresh;
    private JCheckBox chkMonitoringEnabled;

    // Detection
    private JSlider sliderSens;
    private JSpinner spinnerThresh;
    private JCheckBox chkLearningMode;

    // Notifications
    private JCheckBox chkNotifications;
    private JCheckBox chkCriticalPopup;
    private JCheckBox chkSoundAlerts;

    // Appearance
    private JRadioButton radioDark;
    private JRadioButton radioMidnight;
    private JCheckBox chkCompactMode;
    private JCheckBox chkAnimations;

    // Security
    private JFormattedTextField txtTimeout;
    private JCheckBox chkRequireAuth;

    public SettingsPanel(SimulationService simulationService) {
        this.simulationService = simulationService;

        setLayout(new BorderLayout(12, 12));
        setBackground(CustomUITheme.BG_DARK);
        setBorder(new EmptyBorder(14, 16, 14, 16));

        initUI();
    }

    private void initUI() {
        // TOP HEADER
        JPanel topCard = CustomUITheme.createCardPanel();
        topCard.setLayout(new BorderLayout());
        JLabel title = new JLabel("PLATFORM PREFERENCES & RUNTIME SETTINGS");
        title.setFont(CustomUITheme.FONT_TITLE);
        title.setForeground(CustomUITheme.TEXT_PRIMARY);
        topCard.add(title, BorderLayout.WEST);
        add(topCard, BorderLayout.NORTH);

        // CENTER: JTabbedPane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(CustomUITheme.PANEL_BG);
        tabbedPane.setForeground(CustomUITheme.ACCENT_CYAN);
        tabbedPane.setFont(CustomUITheme.FONT_BODY_BOLD);

        tabbedPane.addTab("General", createGeneralTab());
        tabbedPane.addTab("Detection", createDetectionTab());
        tabbedPane.addTab("Notifications", createNotificationsTab());
        tabbedPane.addTab("Appearance", createAppearanceTab());
        tabbedPane.addTab("Security", createSecurityTab());

        add(tabbedPane, BorderLayout.CENTER);

        // BOTTOM: Action buttons
        JPanel bottomBar = CustomUITheme.createCardPanel();
        bottomBar.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 4));

        JButton btnSave = CustomUITheme.createStyledButton("Save Settings", CustomUITheme.HEALTHY_GREEN);
        JButton btnReset = CustomUITheme.createStyledButton("Reset to Defaults", CustomUITheme.BORDER_COLOR);

        btnSave.addActionListener(e -> saveSettings());
        btnReset.addActionListener(e -> resetSettings());

        bottomBar.add(btnSave);
        bottomBar.add(btnReset);
        add(bottomBar, BorderLayout.SOUTH);
    }

    private JPanel createGeneralTab() {
        JPanel p = new JPanel(new GridLayout(3, 1, 8, 8));
        p.setBackground(CustomUITheme.PANEL_BG_LIGHT);
        p.setBorder(new EmptyBorder(20, 24, 20, 24));

        chkAutoStart = createCheckBox("Auto-start detection watchdog on launch", true);
        chkAutoRefresh = createCheckBox("Auto-refresh event stream every 4 seconds", true);
        chkMonitoringEnabled = createCheckBox("Enable systemic autonomous immune vigilance", true);

        p.add(chkAutoStart);
        p.add(chkAutoRefresh);
        p.add(chkMonitoringEnabled);
        return p;
    }

    private JPanel createDetectionTab() {
        JPanel p = new JPanel(new GridLayout(3, 1, 8, 12));
        p.setBackground(CustomUITheme.PANEL_BG_LIGHT);
        p.setBorder(new EmptyBorder(20, 24, 20, 24));

        // Slider
        JPanel sliderBox = new JPanel(new BorderLayout(8, 4));
        sliderBox.setOpaque(false);
        JLabel lblS = new JLabel("Detection Sensitivity Index (1 - 100):");
        lblS.setFont(CustomUITheme.FONT_BODY_BOLD);
        lblS.setForeground(CustomUITheme.TEXT_PRIMARY);

        sliderSens = new JSlider(10, 100, simulationService.getSensitivity());
        sliderSens.setBackground(CustomUITheme.PANEL_BG_LIGHT);
        sliderSens.setForeground(CustomUITheme.ACCENT_CYAN);
        sliderSens.setMajorTickSpacing(20);
        sliderSens.setPaintTicks(true);
        sliderSens.setPaintLabels(true);

        sliderBox.add(lblS, BorderLayout.NORTH);
        sliderBox.add(sliderSens, BorderLayout.CENTER);

        // Spinner
        JPanel spinnerBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        spinnerBox.setOpaque(false);
        JLabel lblT = new JLabel("Anomaly Alert Trigger Threshold:");
        lblT.setFont(CustomUITheme.FONT_BODY_BOLD);
        lblT.setForeground(CustomUITheme.TEXT_PRIMARY);

        spinnerThresh = new JSpinner(new SpinnerNumberModel(simulationService.getThreshold(), 10, 95, 5));
        spinnerThresh.setPreferredSize(new Dimension(80, 28));

        spinnerBox.add(lblT);
        spinnerBox.add(spinnerThresh);

        // Learning mode checkbox
        chkLearningMode = createCheckBox("Adaptive Immune Memory Learning Mode (Train Baselines on Normal Traffic)", true);

        p.add(sliderBox);
        p.add(spinnerBox);
        p.add(chkLearningMode);
        return p;
    }

    private JPanel createNotificationsTab() {
        JPanel p = new JPanel(new GridLayout(3, 1, 8, 8));
        p.setBackground(CustomUITheme.PANEL_BG_LIGHT);
        p.setBorder(new EmptyBorder(20, 24, 20, 24));

        chkNotifications = createCheckBox("Enable In-App Real-Time SOC Notifications", true);
        chkCriticalPopup = createCheckBox("Display High-Priority Popups on Critical Pathogen Anomalies", true);
        chkSoundAlerts = createCheckBox("Auditory Alerts on System Degradation (Simulated)", false);

        p.add(chkNotifications);
        p.add(chkCriticalPopup);
        p.add(chkSoundAlerts);
        return p;
    }

    private JPanel createAppearanceTab() {
        JPanel p = new JPanel(new GridLayout(4, 1, 8, 8));
        p.setBackground(CustomUITheme.PANEL_BG_LIGHT);
        p.setBorder(new EmptyBorder(20, 24, 20, 24));

        // Radio group
        JPanel themeBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        themeBox.setOpaque(false);
        JLabel lblTheme = new JLabel("Color Scheme:");
        lblTheme.setFont(CustomUITheme.FONT_BODY_BOLD);
        lblTheme.setForeground(CustomUITheme.TEXT_PRIMARY);

        radioDark = new JRadioButton("Cyber Navy Dark", true);
        radioMidnight = new JRadioButton("Obsidian Matrix", false);
        styleRadio(radioDark);
        styleRadio(radioMidnight);

        ButtonGroup bg = new ButtonGroup();
        bg.add(radioDark);
        bg.add(radioMidnight);

        themeBox.add(lblTheme);
        themeBox.add(radioDark);
        themeBox.add(radioMidnight);

        chkCompactMode = createCheckBox("High-Density SOC Layout (Compact Grid)", false);
        chkAnimations = createCheckBox("Enable Dynamic Glow and Micro-animations", true);

        // Color picker demo button
        JPanel pickerBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pickerBox.setOpaque(false);
        JButton btnColor = CustomUITheme.createStyledButton("Customize Accent Tint (JColorChooser)", CustomUITheme.ACCENT_CYAN);
        btnColor.addActionListener(e -> {
            Color chosen = JColorChooser.showDialog(this, "Select Custom SOC Accent Color", CustomUITheme.ACCENT_CYAN);
            if (chosen != null) {
                JOptionPane.showMessageDialog(this, "Accent color tint adjusted.", "Theme Updated", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        pickerBox.add(btnColor);

        p.add(themeBox);
        p.add(chkCompactMode);
        p.add(chkAnimations);
        p.add(pickerBox);
        return p;
    }

    private JPanel createSecurityTab() {
        JPanel p = new JPanel(new GridLayout(3, 1, 8, 8));
        p.setBackground(CustomUITheme.PANEL_BG_LIGHT);
        p.setBorder(new EmptyBorder(20, 24, 20, 24));

        JPanel timeoutBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        timeoutBox.setOpaque(false);
        JLabel lblTime = new JLabel("Session Inactivity Timeout (Minutes):");
        lblTime.setFont(CustomUITheme.FONT_BODY_BOLD);
        lblTime.setForeground(CustomUITheme.TEXT_PRIMARY);

        txtTimeout = new JFormattedTextField("30");
        txtTimeout.setPreferredSize(new Dimension(80, 28));
        txtTimeout.setBackground(CustomUITheme.PANEL_BG);
        txtTimeout.setForeground(CustomUITheme.TEXT_PRIMARY);

        timeoutBox.add(lblTime);
        timeoutBox.add(txtTimeout);

        chkRequireAuth = createCheckBox("Enforce Strict Authentication for Security Baselines Modification", true);

        p.add(timeoutBox);
        p.add(chkRequireAuth);
        p.add(new JLabel("<html><font color='#94A3B8'>Session Encryption: TLS 1.3 AES-256-GCM | Zero-Trust Organism Architecture</font></html>"));
        return p;
    }

    private JCheckBox createCheckBox(String text, boolean sel) {
        JCheckBox cb = new JCheckBox(text, sel);
        cb.setBackground(CustomUITheme.PANEL_BG_LIGHT);
        cb.setForeground(CustomUITheme.TEXT_PRIMARY);
        cb.setFont(CustomUITheme.FONT_BODY);
        cb.setFocusPainted(false);
        return cb;
    }

    private void styleRadio(JRadioButton rb) {
        rb.setBackground(CustomUITheme.PANEL_BG_LIGHT);
        rb.setForeground(CustomUITheme.TEXT_PRIMARY);
        rb.setFont(CustomUITheme.FONT_BODY);
        rb.setFocusPainted(false);
    }

    private void saveSettings() {
        try {
            int sens = sliderSens.getValue();
            int thresh = (Integer) spinnerThresh.getValue();
            simulationService.setSensitivity(sens);
            simulationService.setThreshold(thresh);

            int timeout = Integer.parseInt(txtTimeout.getText().trim());
            if (timeout <= 0) throw new NumberFormatException();

            JOptionPane.showMessageDialog(this, "Settings persisted successfully.",
                    "Preferences Saved", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Session timeout must be a positive number.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetSettings() {
        sliderSens.setValue(65);
        spinnerThresh.setValue(60);
        simulationService.setSensitivity(65);
        simulationService.setThreshold(60);
        txtTimeout.setText("30");
        chkAutoStart.setSelected(true);
        chkAutoRefresh.setSelected(true);
        chkMonitoringEnabled.setSelected(true);
        chkLearningMode.setSelected(true);
        chkNotifications.setSelected(true);
        chkCriticalPopup.setSelected(true);
        chkSoundAlerts.setSelected(false);
        chkCompactMode.setSelected(false);
        chkAnimations.setSelected(true);
        chkRequireAuth.setSelected(true);
        radioDark.setSelected(true);

        JOptionPane.showMessageDialog(this, "All settings restored to factory defaults.",
                "Defaults Restored", JOptionPane.INFORMATION_MESSAGE);
    }
}
