package digitalautomonitor.ui;

import digitalautomonitor.enums.RiskLevel;
import digitalautomonitor.model.DetectionResult;
import digitalautomonitor.service.BehaviourMonitorService;
import digitalautomonitor.service.SimulationService;
import digitalautomonitor.ui.components.CustomUITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Anomaly Detection Engine Interface.
 * Shows sensitivity controls, anomaly scores, risk classification,
 * and a full text explanation of why an event was flagged.
 */
public class AnomalyDetectionPanel extends JPanel {

    private final SimulationService simulationService;
    private final BehaviourMonitorService monitorService;

    private JComboBox<String> comboUser;
    private JSlider sliderSensitivity;
    private JSpinner spinnerThreshold;
    private JCheckBox chkAutoDetection;

    // Score display bars
    private JProgressBar barBaseline;
    private JProgressBar barAnomaly;
    private JProgressBar barConfidence;
    private JLabel lblRiskBadge;

    // Large score number label (replaces circular gauge)
    private JLabel lblScoreNumber;

    private JTextPane txtExplanation;

    public AnomalyDetectionPanel(SimulationService simulationService) {
        this.simulationService = simulationService;
        this.monitorService = simulationService.getMonitorService();

        setLayout(new BorderLayout(12, 12));
        setBackground(CustomUITheme.BG_DARK);
        setBorder(new EmptyBorder(14, 16, 14, 16));

        initUI();
        registerListeners();
    }

    private void initUI() {
        // ---------------------------------------------------------------
        //  TOP: Detection Engine Configuration Card
        // ---------------------------------------------------------------
        JPanel controlCard = CustomUITheme.createCardPanel("Anomaly Detection Engine Configuration");
        controlCard.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 0: User selector + Sensitivity slider
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.15;
        JLabel lblUser = new JLabel("Target Entity:");
        lblUser.setFont(CustomUITheme.FONT_BODY_BOLD);
        lblUser.setForeground(CustomUITheme.TEXT_PRIMARY);
        controlCard.add(lblUser, gbc);

        gbc.gridx = 1; gbc.weightx = 0.35;
        comboUser = new JComboBox<>(monitorService.getAllUsernames().toArray(new String[0]));
        comboUser.setBackground(CustomUITheme.PANEL_BG_LIGHT);
        comboUser.setForeground(CustomUITheme.TEXT_PRIMARY);
        controlCard.add(comboUser, gbc);

        gbc.gridx = 2; gbc.weightx = 0.2;
        JLabel lblSens = new JLabel("Sensitivity (1-100):");
        lblSens.setFont(CustomUITheme.FONT_BODY_BOLD);
        lblSens.setForeground(CustomUITheme.TEXT_PRIMARY);
        controlCard.add(lblSens, gbc);

        gbc.gridx = 3; gbc.weightx = 0.3;
        sliderSensitivity = new JSlider(10, 100, simulationService.getSensitivity());
        sliderSensitivity.setBackground(CustomUITheme.PANEL_BG);
        sliderSensitivity.setForeground(CustomUITheme.ACCENT_CYAN);
        sliderSensitivity.setMajorTickSpacing(25);
        sliderSensitivity.setPaintTicks(true);
        sliderSensitivity.addChangeListener(e -> simulationService.setSensitivity(sliderSensitivity.getValue()));
        controlCard.add(sliderSensitivity, gbc);

        // Row 1: Threshold + Auto-detection + Action buttons
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.15;
        JLabel lblThresh = new JLabel("Alert Threshold:");
        lblThresh.setFont(CustomUITheme.FONT_BODY_BOLD);
        lblThresh.setForeground(CustomUITheme.TEXT_PRIMARY);
        controlCard.add(lblThresh, gbc);

        gbc.gridx = 1; gbc.weightx = 0.35;
        spinnerThreshold = new JSpinner(new SpinnerNumberModel(simulationService.getThreshold(), 10, 95, 5));
        spinnerThreshold.setFont(CustomUITheme.FONT_BODY);
        spinnerThreshold.addChangeListener(e -> simulationService.setThreshold((Integer) spinnerThreshold.getValue()));
        controlCard.add(spinnerThreshold, gbc);

        gbc.gridx = 2; gbc.weightx = 0.2;
        chkAutoDetection = new JCheckBox("Autonomous Watchdog", simulationService.isMonitoring());
        chkAutoDetection.setBackground(CustomUITheme.PANEL_BG);
        chkAutoDetection.setForeground(CustomUITheme.HEALTHY_GREEN);
        chkAutoDetection.setFont(CustomUITheme.FONT_BODY_BOLD);
        chkAutoDetection.addActionListener(e -> {
            if (chkAutoDetection.isSelected()) {
                simulationService.startMonitoring();
            } else {
                simulationService.pauseMonitoring();
            }
        });
        controlCard.add(chkAutoDetection, gbc);

        gbc.gridx = 3; gbc.weightx = 0.3;
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        btnRow.setOpaque(false);

        JButton btnRunDetection = CustomUITheme.createStyledButton("Run Detection", CustomUITheme.ACCENT_CYAN);
        btnRunDetection.addActionListener(e -> executeManualDetection());

        JButton btnReset = CustomUITheme.createStyledButton("Reset Baseline", CustomUITheme.BORDER_COLOR);
        btnReset.addActionListener(e -> {
            String user = (String) comboUser.getSelectedItem();
            if (user != null) {
                monitorService.resetBaseline(user);
                JOptionPane.showMessageDialog(this,
                        "Behavioural baseline for '" + user + "' has been reset to defaults.",
                        "Baseline Restored", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnRow.add(btnRunDetection);
        btnRow.add(btnReset);
        controlCard.add(btnRow, gbc);

        add(controlCard, BorderLayout.NORTH);

        // ---------------------------------------------------------------
        //  CENTER: Left (Score Display) + Right (Explanation text)
        // ---------------------------------------------------------------
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 14, 0));
        centerPanel.setOpaque(false);

        // LEFT: Score gauges card
        JPanel leftMetricsCard = CustomUITheme.createCardPanel("Immune Metric Breakdown");
        leftMetricsCard.setLayout(new BorderLayout(0, 12));

        // Large anomaly score number in the center top (replaces circular gauge)
        JPanel scoreWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        scoreWrapper.setOpaque(false);

        lblScoreNumber = new JLabel("42", SwingConstants.CENTER);
        lblScoreNumber.setFont(new Font("Segoe UI", Font.BOLD, 72));
        lblScoreNumber.setForeground(CustomUITheme.WARNING_YELLOW);
        JLabel lblScoreCaption = new JLabel("ANOMALY SCORE / 100", SwingConstants.CENTER);
        lblScoreCaption.setFont(CustomUITheme.FONT_SMALL);
        lblScoreCaption.setForeground(CustomUITheme.TEXT_MUTED);

        JPanel scoreBox = new JPanel(new BorderLayout(0, 4));
        scoreBox.setOpaque(false);
        scoreBox.add(lblScoreNumber, BorderLayout.CENTER);
        scoreBox.add(lblScoreCaption, BorderLayout.SOUTH);
        scoreWrapper.add(scoreBox);
        leftMetricsCard.add(scoreWrapper, BorderLayout.NORTH);

        // Progress bars
        JPanel barsList = new JPanel(new GridLayout(4, 1, 0, 8));
        barsList.setOpaque(false);

        barBaseline = new JProgressBar(0, 100);
        barBaseline.setValue(15);
        CustomUITheme.styleProgressBar(barBaseline, CustomUITheme.ACCENT_BLUE);
        barsList.add(createMetricBarPanel("BASELINE SCORE", barBaseline));

        barAnomaly = new JProgressBar(0, 100);
        barAnomaly.setValue(42);
        CustomUITheme.styleProgressBar(barAnomaly, CustomUITheme.WARNING_YELLOW);
        barsList.add(createMetricBarPanel("ANOMALY SCORE", barAnomaly));

        barConfidence = new JProgressBar(0, 100);
        barConfidence.setValue(91);
        CustomUITheme.styleProgressBar(barConfidence, CustomUITheme.HEALTHY_GREEN);
        barsList.add(createMetricBarPanel("CONFIDENCE INDEX", barConfidence));

        // Risk badge row
        JPanel badgeRow = new JPanel(new BorderLayout());
        badgeRow.setOpaque(false);
        JLabel lblBadgeTitle = new JLabel("RISK LEVEL CLASSIFICATION:");
        lblBadgeTitle.setFont(CustomUITheme.FONT_BODY_BOLD);
        lblBadgeTitle.setForeground(CustomUITheme.TEXT_MUTED);

        lblRiskBadge = new JLabel("LOW RISK", SwingConstants.CENTER);
        lblRiskBadge.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblRiskBadge.setForeground(CustomUITheme.ACCENT_BLUE);
        lblRiskBadge.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(CustomUITheme.ACCENT_BLUE, 1, true),
                new EmptyBorder(4, 12, 4, 12)
        ));

        badgeRow.add(lblBadgeTitle, BorderLayout.WEST);
        badgeRow.add(lblRiskBadge, BorderLayout.EAST);
        barsList.add(badgeRow);

        leftMetricsCard.add(barsList, BorderLayout.CENTER);
        centerPanel.add(leftMetricsCard);

        // RIGHT: Explainable Anomaly Detection text
        JPanel explainCard = CustomUITheme.createCardPanel("Explainable Anomaly Detection (AI Reasoning)");
        explainCard.setLayout(new BorderLayout(0, 8));

        txtExplanation = new JTextPane();
        txtExplanation.setEditable(false);
        txtExplanation.setBackground(CustomUITheme.PANEL_BG_LIGHT);
        txtExplanation.setForeground(CustomUITheme.TEXT_PRIMARY);
        txtExplanation.setFont(CustomUITheme.FONT_MONO);
        txtExplanation.setBorder(new EmptyBorder(12, 14, 12, 14));

        JScrollPane scrollExplain = new JScrollPane(txtExplanation);
        scrollExplain.setBorder(new LineBorder(CustomUITheme.BORDER_COLOR, 1, true));
        explainCard.add(scrollExplain, BorderLayout.CENTER);

        // Simulation stimulus buttons at the bottom
        JPanel quickSimBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        quickSimBar.setOpaque(false);
        JLabel lblSim = new JLabel("Inject Antigen Stimulus:");
        lblSim.setFont(CustomUITheme.FONT_SMALL);
        lblSim.setForeground(CustomUITheme.TEXT_MUTED);

        JButton btnSimNormal = CustomUITheme.createStyledButton("Simulate Normal", CustomUITheme.HEALTHY_GREEN);
        JButton btnSimSusp = CustomUITheme.createStyledButton("Simulate Suspicious", CustomUITheme.WARNING_YELLOW);
        JButton btnSimCrit = CustomUITheme.createStyledButton("Simulate Critical", CustomUITheme.CRITICAL_RED);

        btnSimNormal.addActionListener(e -> simulationService.simulateNormalEvent());
        btnSimSusp.addActionListener(e -> simulationService.simulateSuspiciousEvent());
        btnSimCrit.addActionListener(e -> simulationService.simulateCriticalEvent());

        quickSimBar.add(lblSim);
        quickSimBar.add(btnSimNormal);
        quickSimBar.add(btnSimSusp);
        quickSimBar.add(btnSimCrit);
        explainCard.add(quickSimBar, BorderLayout.SOUTH);

        centerPanel.add(explainCard);
        add(centerPanel, BorderLayout.CENTER);

        displayInitialExplanation();
    }

    /** Creates a labelled progress bar panel. */
    private JPanel createMetricBarPanel(String title, JProgressBar bar) {
        JPanel p = new JPanel(new BorderLayout(6, 2));
        p.setOpaque(false);
        JLabel lbl = new JLabel(title);
        lbl.setFont(CustomUITheme.FONT_BODY_BOLD);
        lbl.setForeground(CustomUITheme.TEXT_MUTED);
        p.add(lbl, BorderLayout.NORTH);
        p.add(bar, BorderLayout.CENTER);
        return p;
    }

    /** Shows the welcome text before any detection is run. */
    private void displayInitialExplanation() {
        txtExplanation.setText(
            "====================================================================\n" +
            "EXPLAINABLE BEHAVIOURAL ANOMALY DETECTION ENGINE\n" +
            "====================================================================\n\n" +
            "Status: ENGINE ONLINE | 5 DETECTION RULES ARMED\n\n" +
            "Algorithm: Rule-Based Scoring (max 100 points)\n" +
            "  Excessive login attempts   → +20\n" +
            "  High CPU usage             → +15\n" +
            "  High memory usage          → +15\n" +
            "  Unusual file access        → +25\n" +
            "  Unusual network activity   → +25\n\n" +
            "Risk Thresholds:\n" +
            "  0–29   = NORMAL\n" +
            "  30–59  = LOW\n" +
            "  60–79  = HIGH\n" +
            "  80–100 = CRITICAL\n\n" +
            "Select an entity and click 'Run Detection', or use the\n" +
            "Stimulus buttons below to trigger a simulated event.\n"
        );
    }

    /**
     * Called when the user clicks 'Run Detection'.
     * Runs a suspicious event simulation and displays the result.
     */
    private void executeManualDetection() {
        String username = (String) comboUser.getSelectedItem();
        if (username == null) return;
        // Simulate a suspicious event for the selected user and display the result
        DetectionResult result = simulationService.simulateSuspiciousEvent();
        renderResult(result);
    }

    /**
     * Updates all score displays and the explanation text from a DetectionResult.
     * Called both from the manual button and from the auto-detection listener.
     */
    private void renderResult(DetectionResult result) {
        // Update the large score number
        lblScoreNumber.setText(String.valueOf(result.getAnomalyScore()));

        // Update progress bars
        barBaseline.setValue(result.getBaselineScore());
        barAnomaly.setValue(result.getAnomalyScore());
        barConfidence.setValue(result.getConfidenceScore());

        // Update risk badge colour and label
        RiskLevel risk = result.getRiskLevel();
        lblRiskBadge.setText(risk.getDisplayName().toUpperCase());
        lblRiskBadge.setForeground(risk.getColor());
        lblRiskBadge.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(risk.getColor(), 1, true),
                new EmptyBorder(4, 12, 4, 12)
        ));

        // Update score number colour to match risk level
        lblScoreNumber.setForeground(risk.getColor());

        // Build the explanation text
        StringBuilder sb = new StringBuilder();
        sb.append("====================================================================\n");
        sb.append("ANOMALY DETECTION DIAGNOSTIC REPORT\n");
        sb.append("====================================================================\n\n");
        sb.append("Target Entity:        ").append(result.getTargetUser()).append("\n");
        sb.append("Calculated Score:     ").append(result.getAnomalyScore()).append("/100 (").append(risk.getDisplayName()).append(")\n");
        sb.append("Confidence Index:     ").append(result.getConfidenceScore()).append("%\n");
        sb.append("Rules Evaluated:      ").append(result.getEvaluatedRuleCount())
          .append(" rules (").append(result.getTriggeredRuleCount()).append(" triggered)\n\n");
        sb.append("--------------------------------------------------------------------\n");
        sb.append("CONTRIBUTING FACTORS:\n");
        sb.append("--------------------------------------------------------------------\n");

        if (result.getContributingFactors().isEmpty()) {
            sb.append("  [OK] No abnormal variance detected.\n");
            sb.append("       All parameters are within normal baseline ranges.\n");
        } else {
            for (String factor : result.getContributingFactors()) {
                sb.append("  [!] ").append(factor).append("\n");
            }
        }

        sb.append("\n--------------------------------------------------------------------\n");
        sb.append("RECOMMENDED ACTION:\n");
        sb.append("--------------------------------------------------------------------\n");
        sb.append("\"").append(result.getRecommendedAction()).append("\"\n\n");
        sb.append("====================================================================\n");

        txtExplanation.setText(sb.toString());
        txtExplanation.setCaretPosition(0);
    }

    /**
     * Registers with SimulationService so this panel auto-updates
     * every time the background timer fires a new detection result.
     */
    private void registerListeners() {
        simulationService.addDetectionListener(this::renderResult);
    }
}
