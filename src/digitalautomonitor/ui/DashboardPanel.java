package digitalautomonitor.ui;

import digitalautomonitor.enums.RiskLevel;
import digitalautomonitor.model.BehaviourEvent;
import digitalautomonitor.model.DetectionResult;
import digitalautomonitor.service.AlertService;
import digitalautomonitor.service.EventLogService;
import digitalautomonitor.service.SimulationService;
import digitalautomonitor.ui.components.CustomUITheme;
import digitalautomonitor.ui.components.StatCard;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Main SOC Dashboard.
 *
 * Shows:
 *   - 6 KPI stat cards (Health, Score, Alerts, Critical Events, Users, Events Today)
 *   - Behavioural Immune Status with three colour-coded progress bars
 *   - Real-time Anomaly Score Chart (drawn using Java2D directly in paintComponent)
 *   - Recent Security Events JTable
 */
public class DashboardPanel extends JPanel {

    private final SimulationService simulationService;
    private final EventLogService eventLogService;
    private final AlertService alertService;

    // Stat cards
    private StatCard cardHealth;
    private StatCard cardScore;
    private StatCard cardAlerts;
    private StatCard cardCritical;
    private StatCard cardUsers;
    private StatCard cardEvents;

    // Immune Status progress bars
    private JProgressBar barNormal;
    private JProgressBar barSuspicious;
    private JProgressBar barCritical;
    private JLabel lblNormalPct;
    private JLabel lblSuspiciousPct;
    private JLabel lblCriticalPct;

    // Simple inline anomaly score chart
    private ScoreChartPanel scoreChartPanel;

    // Recent events table
    private DefaultTableModel tableModel;
    private JTable eventTable;

    // -----------------------------------------------------------------------
    //  Simple inner class: draws a live line graph of anomaly scores.
    //  Replaces the deleted BehaviourChartPanel component.
    // -----------------------------------------------------------------------
    private static class ScoreChartPanel extends JPanel {
        private final String title;
        private final List<Integer> scores = new ArrayList<>();
        private static final int MAX_POINTS = 40;

        ScoreChartPanel(String title) {
            this.title = title;
            setBackground(CustomUITheme.PANEL_BG);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(CustomUITheme.BORDER_COLOR, 1),
                    new EmptyBorder(6, 6, 6, 6)
            ));
            // Seed with a few values so the chart isn't empty on startup
            for (int i = 0; i < 10; i++) {
                scores.add(10 + (int)(Math.random() * 30));
            }
        }

        /** Adds a new anomaly score data point and triggers a repaint. */
        void addDataPoint(int score) {
            scores.add(Math.max(0, Math.min(100, score)));
            if (scores.size() > MAX_POINTS) {
                scores.remove(0);
            }
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int pad = 30;

            // Draw title
            g2d.setColor(CustomUITheme.TEXT_MUTED);
            g2d.setFont(CustomUITheme.FONT_SMALL);
            g2d.drawString(title, pad, 18);

            if (scores.size() < 2) return;

            // Draw horizontal grid lines at 25, 50, 75, 100
            g2d.setColor(new Color(255, 255, 255, 25));
            for (int gridY : new int[]{25, 50, 75, 100}) {
                int yPos = pad + (h - 2 * pad) - (int)((gridY / 100.0) * (h - 2 * pad));
                g2d.drawLine(pad, yPos, w - pad, yPos);
                g2d.setColor(CustomUITheme.TEXT_MUTED);
                g2d.drawString(String.valueOf(gridY), 2, yPos + 4);
                g2d.setColor(new Color(255, 255, 255, 25));
            }

            // Draw score line
            g2d.setStroke(new BasicStroke(2.0f));
            int chartW = w - 2 * pad;
            int chartH = h - 2 * pad;

            for (int i = 1; i < scores.size(); i++) {
                int x1 = pad + (int)((double)(i - 1) / (MAX_POINTS - 1) * chartW);
                int y1 = pad + chartH - (int)((scores.get(i - 1) / 100.0) * chartH);
                int x2 = pad + (int)((double) i       / (MAX_POINTS - 1) * chartW);
                int y2 = pad + chartH - (int)((scores.get(i)     / 100.0) * chartH);

                // Colour the line based on the score at that point
                int score = scores.get(i);
                if (score >= 80)      g2d.setColor(CustomUITheme.CRITICAL_RED);
                else if (score >= 60) g2d.setColor(CustomUITheme.WARNING_YELLOW);
                else if (score >= 30) g2d.setColor(CustomUITheme.ACCENT_BLUE);
                else                  g2d.setColor(CustomUITheme.HEALTHY_GREEN);

                g2d.drawLine(x1, y1, x2, y2);
            }

            // Draw current score as a large number in the bottom-right corner
            int latest = scores.get(scores.size() - 1);
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 24));
            if (latest >= 80)      g2d.setColor(CustomUITheme.CRITICAL_RED);
            else if (latest >= 60) g2d.setColor(CustomUITheme.WARNING_YELLOW);
            else                   g2d.setColor(CustomUITheme.HEALTHY_GREEN);
            String scoreText = "Score: " + latest;
            g2d.drawString(scoreText, w - pad - g2d.getFontMetrics().stringWidth(scoreText), h - 8);
        }
    }

    public DashboardPanel(SimulationService simulationService) {
        this.simulationService = simulationService;
        this.eventLogService   = simulationService.getEventLogService();
        this.alertService      = simulationService.getAlertService();

        setLayout(new BorderLayout(12, 12));
        setBackground(CustomUITheme.BG_DARK);
        setBorder(new EmptyBorder(14, 16, 14, 16));

        initUI();
        registerListeners();
    }

    private void initUI() {
        // ---------------------------------------------------------------
        //  TOP: 6 KPI stat cards
        // ---------------------------------------------------------------
        JPanel kpiGrid = new JPanel(new GridLayout(1, 6, 10, 10));
        kpiGrid.setOpaque(false);

        cardHealth   = new StatCard("SYSTEM HEALTH",     "98%",   "Autonomous defense optimal",    CustomUITheme.HEALTHY_GREEN,  98);
        cardScore    = new StatCard("BEHAVIOUR SCORE",   "91/100","Organism homeostasis active",   CustomUITheme.ACCENT_CYAN,    91);
        cardAlerts   = new StatCard("ACTIVE ALERTS",     String.valueOf(alertService.getActiveAlertsCount()),  "Inflammatory events in triage", CustomUITheme.WARNING_YELLOW, 35);
        cardCritical = new StatCard("CRITICAL EVENTS",   String.valueOf(alertService.getCriticalAlertsCount()),"Acute pathogen threats",         CustomUITheme.CRITICAL_RED,   20);
        cardUsers    = new StatCard("MONITORED USERS",   String.valueOf(simulationService.getMonitorService().getMonitoredUsersCount()), "Immune memory baselines", CustomUITheme.ACCENT_BLUE, 100);
        cardEvents   = new StatCard("EVENTS TODAY",      String.format("%,d", eventLogService.getEventsTodayCount()), "Antigen telemetry streams", CustomUITheme.TEXT_PRIMARY, 74);

        kpiGrid.add(cardHealth);
        kpiGrid.add(cardScore);
        kpiGrid.add(cardAlerts);
        kpiGrid.add(cardCritical);
        kpiGrid.add(cardUsers);
        kpiGrid.add(cardEvents);

        // ---------------------------------------------------------------
        //  MIDDLE: Immune Status (left) + Score Chart (right)
        // ---------------------------------------------------------------
        JPanel middlePanel = new JPanel(new GridBagLayout());
        middlePanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 8);

        // LEFT: Behavioural Immune Status card with 3 progress bars
        JPanel immuneCard = CustomUITheme.createCardPanel("Behavioural Immune Status");
        immuneCard.setLayout(new BoxLayout(immuneCard, BoxLayout.Y_AXIS));
        immuneCard.setPreferredSize(new Dimension(380, 220));

        JLabel immuneSubtitle = new JLabel("Real-time cellular homeostasis ratio:");
        immuneSubtitle.setFont(CustomUITheme.FONT_BODY);
        immuneSubtitle.setForeground(CustomUITheme.TEXT_MUTED);
        immuneCard.add(immuneSubtitle);
        immuneCard.add(Box.createVerticalStrut(10));

        barNormal = new JProgressBar(0, 100);
        barNormal.setValue(78);
        CustomUITheme.styleProgressBar(barNormal, CustomUITheme.HEALTHY_GREEN);
        lblNormalPct = new JLabel("78%");
        lblNormalPct.setFont(CustomUITheme.FONT_BODY_BOLD);
        lblNormalPct.setForeground(CustomUITheme.HEALTHY_GREEN);
        immuneCard.add(createProgressRow("NORMAL CELLS", barNormal, lblNormalPct, CustomUITheme.HEALTHY_GREEN));
        immuneCard.add(Box.createVerticalStrut(8));

        barSuspicious = new JProgressBar(0, 100);
        barSuspicious.setValue(15);
        CustomUITheme.styleProgressBar(barSuspicious, CustomUITheme.WARNING_YELLOW);
        lblSuspiciousPct = new JLabel("15%");
        lblSuspiciousPct.setFont(CustomUITheme.FONT_BODY_BOLD);
        lblSuspiciousPct.setForeground(CustomUITheme.WARNING_YELLOW);
        immuneCard.add(createProgressRow("SUSPICIOUS ANTIGENS", barSuspicious, lblSuspiciousPct, CustomUITheme.WARNING_YELLOW));
        immuneCard.add(Box.createVerticalStrut(8));

        barCritical = new JProgressBar(0, 100);
        barCritical.setValue(7);
        CustomUITheme.styleProgressBar(barCritical, CustomUITheme.CRITICAL_RED);
        lblCriticalPct = new JLabel("7%");
        lblCriticalPct.setFont(CustomUITheme.FONT_BODY_BOLD);
        lblCriticalPct.setForeground(CustomUITheme.CRITICAL_RED);
        immuneCard.add(createProgressRow("CRITICAL PATHOGENS", barCritical, lblCriticalPct, CustomUITheme.CRITICAL_RED));
        immuneCard.add(Box.createVerticalStrut(10));

        JLabel infoSummary = new JLabel(
                "<html><font color='#94A3B8'>Active Antibodies:</font> <font color='#00F0FF'>5 Rules Armed</font>" +
                " | <font color='#94A3B8'>Inflammation Index:</font> <font color='#10B981'>Low (Controlled)</font></html>");
        infoSummary.setFont(CustomUITheme.FONT_SMALL);
        immuneCard.add(infoSummary);

        gbc.gridx = 0; gbc.weightx = 0.35;
        middlePanel.add(immuneCard, gbc);

        // RIGHT: Live anomaly score chart (replaces deleted BehaviourChartPanel + CyberLayerUI)
        scoreChartPanel = new ScoreChartPanel("Real-Time Anomaly Score Trajectory (SOC Stream)");
        gbc.gridx = 1; gbc.weightx = 0.65;
        gbc.insets = new Insets(0, 0, 0, 0);
        middlePanel.add(scoreChartPanel, gbc);

        // ---------------------------------------------------------------
        //  BOTTOM: Recent Security Events JTable
        // ---------------------------------------------------------------
        JPanel tableContainer = CustomUITheme.createCardPanel("Recent Security Events");
        tableContainer.setLayout(new BorderLayout(0, 8));

        String[] columns = {"Time", "User", "Event / Description", "Category", "Risk Level", "Status / Action"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        eventTable = new JTable(tableModel);
        CustomUITheme.styleTable(eventTable);

        // Colour the Risk Level column
        eventTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                l.setFont(CustomUITheme.FONT_BODY_BOLD);
                String text = String.valueOf(value);
                if ("CRITICAL".equalsIgnoreCase(text))                          l.setForeground(CustomUITheme.CRITICAL_RED);
                else if ("HIGH RISK".equalsIgnoreCase(text) || "HIGH".equalsIgnoreCase(text)) l.setForeground(CustomUITheme.WARNING_ORANGE);
                else if ("MEDIUM RISK".equalsIgnoreCase(text) || "MEDIUM".equalsIgnoreCase(text)) l.setForeground(CustomUITheme.WARNING_YELLOW);
                else if ("LOW RISK".equalsIgnoreCase(text) || "LOW".equalsIgnoreCase(text))   l.setForeground(CustomUITheme.ACCENT_BLUE);
                else                                                             l.setForeground(CustomUITheme.HEALTHY_GREEN);
                return l;
            }
        });

        JScrollPane scrollPane = new JScrollPane(eventTable);
        JViewport viewport = scrollPane.getViewport();
        viewport.setBackground(CustomUITheme.PANEL_BG);
        scrollPane.setBorder(BorderFactory.createLineBorder(CustomUITheme.BORDER_COLOR));
        tableContainer.add(scrollPane, BorderLayout.CENTER);

        // Assemble layout
        JPanel topAndMiddle = new JPanel(new BorderLayout(0, 10));
        topAndMiddle.setOpaque(false);
        topAndMiddle.add(kpiGrid, BorderLayout.NORTH);
        topAndMiddle.add(middlePanel, BorderLayout.CENTER);

        add(topAndMiddle, BorderLayout.NORTH);
        add(tableContainer, BorderLayout.CENTER);

        refreshTableData();
    }

    /** Helper to create a labelled progress bar row with a percentage label. */
    private JPanel createProgressRow(String label, JProgressBar bar, JLabel pctLabel, Color color) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);

        JLabel lbl = new JLabel(label);
        lbl.setFont(CustomUITheme.FONT_BODY_BOLD);
        lbl.setForeground(color);
        lbl.setPreferredSize(new Dimension(140, 20));

        row.add(lbl, BorderLayout.WEST);
        row.add(bar, BorderLayout.CENTER);
        row.add(pctLabel, BorderLayout.EAST);
        return row;
    }

    private void registerListeners() {
        // Update the events table whenever a new event is logged
        eventLogService.addChangeListener(() -> SwingUtilities.invokeLater(this::refreshTableData));

        // Update alert KPI cards when alerts change
        alertService.addChangeListener(() -> SwingUtilities.invokeLater(() -> {
            cardAlerts.setValue(String.valueOf(alertService.getActiveAlertsCount()));
            cardCritical.setValue(String.valueOf(alertService.getCriticalAlertsCount()));
        }));

        // Update chart and scores on every new detection result
        simulationService.addDetectionListener(this::onNewDetection);
    }

    /** Called every time the simulation engine produces a new DetectionResult. */
    private void onNewDetection(DetectionResult result) {
        // Add the score to the live chart
        scoreChartPanel.addDataPoint(result.getAnomalyScore());

        // Update the summary KPI cards
        cardEvents.setValue(String.format("%,d", eventLogService.getEventsTodayCount()));
        cardScore.setValue((100 - (result.getAnomalyScore() / 3)) + "/100");

        // Recalculate the immune distribution bars
        int crit = Math.min(25, 5 + alertService.getCriticalAlertsCount() * 2);
        int susp = Math.min(35, 12 + alertService.getActiveAlertsCount());
        int norm = Math.max(40, 100 - crit - susp);

        barNormal.setValue(norm);
        lblNormalPct.setText(norm + "%");
        barSuspicious.setValue(susp);
        lblSuspiciousPct.setText(susp + "%");
        barCritical.setValue(crit);
        lblCriticalPct.setText(crit + "%");
    }

    /** Populates the events table with the 15 most recent behaviour events. */
    public void refreshTableData() {
        tableModel.setRowCount(0);
        List<BehaviourEvent> events = eventLogService.getRecentEvents(15);
        for (BehaviourEvent e : events) {
            tableModel.addRow(new Object[]{
                    e.getFormattedTime(),
                    e.getUsername(),
                    e.getDescription(),
                    e.getEventType().getCategoryName(),
                    e.getRiskLevel().getDisplayName(),
                    e.getActionTaken()
            });
        }
    }
}
