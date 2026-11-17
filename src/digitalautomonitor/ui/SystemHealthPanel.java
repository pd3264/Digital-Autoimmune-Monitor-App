package digitalautomonitor.ui;

import digitalautomonitor.model.SystemMetric;
import digitalautomonitor.ui.components.CustomUITheme;
import digitalautomonitor.ui.components.StatusIndicator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * System Health Panel monitoring host vitals, background daemon health,
 * and security subsystem integrity in simulated real-time.
 */
public class SystemHealthPanel extends JPanel {

    private final List<SystemMetric> metrics = new ArrayList<>();
    private final List<JProgressBar> progressBars = new ArrayList<>();
    private final List<JLabel> percentLabels = new ArrayList<>();
    private final List<StatusIndicator> statusIndicators = new ArrayList<>();

    private Timer healthTimer;
    private final Random random = new Random();

    public SystemHealthPanel() {
        setLayout(new BorderLayout(12, 12));
        setBackground(CustomUITheme.BG_DARK);
        setBorder(new EmptyBorder(14, 16, 14, 16));

        initMetrics();
        initUI();
        startSimulatedTicker();
    }

    private void initMetrics() {
        metrics.add(new SystemMetric("CPU Utilization", 43, "ONLINE", "8 Cores @ 3.6 GHz | SOC Load Normal"));
        metrics.add(new SystemMetric("Memory Allocation", 61, "ONLINE", "19.5 GB / 32 GB RAM Active"));
        metrics.add(new SystemMetric("Disk Storage Array", 72, "ONLINE", "720 GB / 1 TB NVMe RAID Array"));
        metrics.add(new SystemMetric("Network Bandwidth", 28, "ONLINE", "280 Mbps / 1 Gbps Backbone Egress"));
        metrics.add(new SystemMetric("Security Engine Core", 98, "ONLINE", "5 Polymorphic Rule Daemons Active"));
        metrics.add(new SystemMetric("Event Collector Agent", 85, "ONLINE", "1,200 events/sec Ingestion Buffer"));
        metrics.add(new SystemMetric("Immune Memory DB", 54, "ONLINE", "ACID Telemetry Store Synchronized"));
    }

    private void initUI() {
        // TOP: Overview Card
        JPanel topCard = CustomUITheme.createCardPanel();
        topCard.setLayout(new BorderLayout());

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titlePanel.setOpaque(false);
        JLabel title = new JLabel("ORGANISM & SYSTEM HEALTH TELEMETRY");
        title.setFont(CustomUITheme.FONT_TITLE);
        title.setForeground(CustomUITheme.TEXT_PRIMARY);

        StatusIndicator overallLed = new StatusIndicator(CustomUITheme.HEALTHY_GREEN, "ALL SUBSYSTEMS NOMINAL");

        titlePanel.add(title);
        titlePanel.add(overallLed);
        topCard.add(titlePanel, BorderLayout.WEST);

        JLabel refreshNotice = new JLabel("Live Pulse: 2.5s jitter active");
        refreshNotice.setFont(CustomUITheme.FONT_SMALL);
        refreshNotice.setForeground(CustomUITheme.TEXT_MUTED);
        topCard.add(refreshNotice, BorderLayout.EAST);

        add(topCard, BorderLayout.NORTH);

        // CENTER: 7 Metrics Cards in a Grid
        JPanel grid = new JPanel(new GridLayout(4, 2, 14, 14));
        grid.setOpaque(false);

        for (SystemMetric sm : metrics) {
            JPanel card = new JPanel(new BorderLayout(8, 8));
            card.setBackground(CustomUITheme.PANEL_BG);
            card.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(CustomUITheme.BORDER_COLOR, 1, true),
                    new EmptyBorder(12, 14, 12, 14)
            ));

            // Top row: Metric name & status LED
            JPanel header = new JPanel(new BorderLayout());
            header.setOpaque(false);

            JLabel nameLbl = new JLabel(sm.getName().toUpperCase());
            nameLbl.setFont(CustomUITheme.FONT_BODY_BOLD);
            nameLbl.setForeground(CustomUITheme.TEXT_PRIMARY);

            StatusIndicator led = new StatusIndicator(sm.getStatusColor(), sm.getStatus());
            statusIndicators.add(led);

            header.add(nameLbl, BorderLayout.WEST);
            header.add(led, BorderLayout.EAST);
            card.add(header, BorderLayout.NORTH);

            // Center row: Progress bar
            JProgressBar bar = new JProgressBar(0, 100);
            bar.setValue(sm.getPercentage());
            CustomUITheme.styleProgressBar(bar, CustomUITheme.ACCENT_CYAN);
            progressBars.add(bar);
            card.add(bar, BorderLayout.CENTER);

            // Bottom row: Detail and numeric percentage
            JPanel footer = new JPanel(new BorderLayout());
            footer.setOpaque(false);

            JLabel detailLbl = new JLabel(sm.getDetail());
            detailLbl.setFont(CustomUITheme.FONT_SMALL);
            detailLbl.setForeground(CustomUITheme.TEXT_MUTED);

            JLabel pctLbl = new JLabel(sm.getPercentage() + "%");
            pctLbl.setFont(CustomUITheme.FONT_BODY_BOLD);
            pctLbl.setForeground(CustomUITheme.ACCENT_CYAN);
            percentLabels.add(pctLbl);

            footer.add(detailLbl, BorderLayout.WEST);
            footer.add(pctLbl, BorderLayout.EAST);
            card.add(footer, BorderLayout.SOUTH);

            grid.add(card);
        }

        // 8th slot in grid: Quick health summary card
        JPanel summaryCard = new JPanel(new BorderLayout(8, 8));
        summaryCard.setBackground(CustomUITheme.PANEL_BG_LIGHT);
        summaryCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(CustomUITheme.ACCENT_CYAN, 1, true),
                new EmptyBorder(12, 14, 12, 14)
        ));

        JLabel sumTitle = new JLabel("BIOLOGICAL HOMEOSTASIS INDEX");
        sumTitle.setFont(CustomUITheme.FONT_BODY_BOLD);
        sumTitle.setForeground(CustomUITheme.ACCENT_CYAN);

        JLabel sumVal = new JLabel("98.4% NOMINAL");
        sumVal.setFont(CustomUITheme.FONT_METRIC);
        sumVal.setForeground(CustomUITheme.HEALTHY_GREEN);

        JLabel sumDesc = new JLabel("<html>System cellular vital signs stable. Zero resource exhaustion. All detection antibodies loaded in memory.</html>");
        sumDesc.setFont(CustomUITheme.FONT_SMALL);
        sumDesc.setForeground(CustomUITheme.TEXT_MUTED);

        summaryCard.add(sumTitle, BorderLayout.NORTH);
        summaryCard.add(sumVal, BorderLayout.CENTER);
        summaryCard.add(sumDesc, BorderLayout.SOUTH);

        grid.add(summaryCard);

        add(grid, BorderLayout.CENTER);
    }

    private void startSimulatedTicker() {
        healthTimer = new Timer(2500, e -> {
            for (int i = 0; i < metrics.size(); i++) {
                SystemMetric sm = metrics.get(i);
                // Slight jitter +/- 4%
                int change = random.nextInt(9) - 4;
                int newVal = Math.max(10, Math.min(95, sm.getPercentage() + change));
                sm.setPercentage(newVal);

                JProgressBar bar = progressBars.get(i);
                JLabel lbl = percentLabels.get(i);
                StatusIndicator led = statusIndicators.get(i);

                bar.setValue(newVal);
                lbl.setText(newVal + "%");

                if (newVal >= 85) {
                    bar.setForeground(CustomUITheme.CRITICAL_RED);
                    lbl.setForeground(CustomUITheme.CRITICAL_RED);
                    sm.setStatus("DEGRADED");
                    led.setStatus(CustomUITheme.WARNING_YELLOW, "DEGRADED");
                } else if (newVal >= 70) {
                    bar.setForeground(CustomUITheme.WARNING_YELLOW);
                    lbl.setForeground(CustomUITheme.WARNING_YELLOW);
                    sm.setStatus("ONLINE");
                    led.setStatus(CustomUITheme.HEALTHY_GREEN, "ONLINE");
                } else {
                    bar.setForeground(CustomUITheme.ACCENT_CYAN);
                    lbl.setForeground(CustomUITheme.ACCENT_CYAN);
                    sm.setStatus("ONLINE");
                    led.setStatus(CustomUITheme.HEALTHY_GREEN, "ONLINE");
                }
            }
        });
        healthTimer.start();
    }
}
