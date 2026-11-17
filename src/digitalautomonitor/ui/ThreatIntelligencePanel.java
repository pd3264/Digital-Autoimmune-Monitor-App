package digitalautomonitor.ui;

import digitalautomonitor.enums.RiskLevel;
import digitalautomonitor.ui.components.CustomUITheme;
import digitalautomonitor.ui.components.StatCard;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Threat Intelligence Panel.
 * Displays a simulated feed of known threat indicators (IOCs),
 * with a detail advisory pane for each selected indicator.
 *
 * All data is pre-seeded for demonstration purposes.
 */
public class ThreatIntelligencePanel extends JPanel {

    // -----------------------------------------------------------------------
    //  Simple inner class representing one Threat Indicator of Compromise (IOC).
    //  Replaces the deleted ThreatIndicator model class.
    // -----------------------------------------------------------------------
    private static class ThreatIndicator {
        String indicator;    // What was observed
        String type;         // Classification (e.g., "Process Masquerading")
        String source;       // Which detection engine found it
        RiskLevel risk;      // Severity level
        String firstSeen;    // Time string
        String status;       // Current status (Active / Blocked / Monitored)
        String description;  // Technical description

        ThreatIndicator(String indicator, String type, String source,
                        RiskLevel risk, String firstSeen, String status, String description) {
            this.indicator   = indicator;
            this.type        = type;
            this.source      = source;
            this.risk        = risk;
            this.firstSeen   = firstSeen;
            this.status      = status;
            this.description = description;
        }
    }

    // -----------------------------------------------------------------------
    //  Data and UI components
    // -----------------------------------------------------------------------
    private final List<ThreatIndicator> indicators = new ArrayList<>();
    private DefaultTableModel tableModel;
    private JTable threatTable;
    private JEditorPane advisoryPane;

    public ThreatIntelligencePanel() {
        setLayout(new BorderLayout(12, 12));
        setBackground(CustomUITheme.BG_DARK);
        setBorder(new EmptyBorder(14, 16, 14, 16));

        seedIndicators();
        initUI();
    }

    /** Pre-loads simulated threat data for the demonstration. */
    private void seedIndicators() {
        indicators.add(new ThreatIndicator(
                "svch0st.exe memory injector", "Process Masquerading", "Host Heuristics",
                RiskLevel.CRITICAL, "08:12:00", "Active",
                "Unauthorized binary disguising as Windows Host Process and attempting LSASS access."));
        indicators.add(new ThreatIndicator(
                "Rapid multi-user credential sweep", "Credential Stuffing", "Auth Watchdog",
                RiskLevel.HIGH, "08:34:22", "Investigating",
                "Coordinated brute force across multiple accounts from internal subnet."));
        indicators.add(new ThreatIndicator(
                "1.4GB Encrypted Egress to 198.51.100.22", "Data Exfiltration", "Network IDS",
                RiskLevel.CRITICAL, "09:05:14", "Blocked",
                "High bandwidth uninspected SSL beaconing to untrusted autonomous system."));
        indicators.add(new ThreatIndicator(
                "Mass shadow-copy deletion command", "Ransomware Pre-cursor", "System Auditing",
                RiskLevel.CRITICAL, "09:18:40", "Blocked",
                "vssadmin.exe delete shadows /all executed from elevated command shell."));
        indicators.add(new ThreatIndicator(
                "Unsigned block driver in temp directory", "Kernel Hooking", "Driver Integrity",
                RiskLevel.HIGH, "09:29:11", "Active",
                "Driver loading attempt bypassing Windows driver signature enforcement."));
        indicators.add(new ThreatIndicator(
                "High-frequency volume file attribute alteration", "Ransomware Heuristic", "File System Guard",
                RiskLevel.HIGH, "09:41:05", "Blocked",
                "Rapid re-naming of user files with secondary encrypted extensions."));
        indicators.add(new ThreatIndicator(
                "Direct raw USB mass storage block read", "Physical Exfiltration", "USB Controller",
                RiskLevel.MEDIUM, "10:11:50", "Monitored",
                "Direct hardware block addressing on newly mounted external thumb drive."));
        indicators.add(new ThreatIndicator(
                "Powershell encoded payload string execution", "Defense Evasion", "Process Monitor",
                RiskLevel.MEDIUM, "10:25:33", "Monitored",
                "Powershell.exe launched with -EncodedCommand and execution policy bypass."));
    }

    private void initUI() {
        // ---------------------------------------------------------------
        //  TOP: 4 KPI stat cards
        // ---------------------------------------------------------------
        JPanel kpiRow = new JPanel(new GridLayout(1, 4, 12, 0));
        kpiRow.setOpaque(false);

        kpiRow.add(new StatCard("KNOWN THREATS",   "42", "Cataloged antibody signatures", CustomUITheme.ACCENT_CYAN,  75));
        kpiRow.add(new StatCard("NEW INDICATORS",  "8",  "Emerging antigen markers",      CustomUITheme.WARNING_YELLOW, 25));
        kpiRow.add(new StatCard("BLOCKED EVENTS",  "17", "Neutralized pathogens",         CustomUITheme.HEALTHY_GREEN,  90));
        kpiRow.add(new StatCard("ACTIVE THREATS",  "3",  "Active immune engagements",     CustomUITheme.CRITICAL_RED,   15));

        add(kpiRow, BorderLayout.NORTH);

        // ---------------------------------------------------------------
        //  CENTER: Split pane — table on left, advisory detail on right
        // ---------------------------------------------------------------
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(820);
        splitPane.setBorder(null);
        splitPane.setOpaque(false);

        // LEFT: IOC table
        JPanel tableContainer = CustomUITheme.createCardPanel("Pathogen Indicators of Compromise (IOC)");
        tableContainer.setLayout(new BorderLayout(0, 8));

        String[] cols = {"Indicator", "Type", "Source", "Risk Tier", "First Seen", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        threatTable = new JTable(tableModel);
        CustomUITheme.styleTable(threatTable);

        // Colour the Risk Tier column based on severity
        threatTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                l.setFont(CustomUITheme.FONT_BODY_BOLD);
                String v = String.valueOf(value);
                if (v.equalsIgnoreCase("CRITICAL"))      l.setForeground(CustomUITheme.CRITICAL_RED);
                else if (v.equalsIgnoreCase("HIGH RISK")
                      || v.equalsIgnoreCase("HIGH"))     l.setForeground(CustomUITheme.WARNING_ORANGE);
                else if (v.equalsIgnoreCase("MEDIUM")
                      || v.equalsIgnoreCase("MEDIUM RISK")) l.setForeground(CustomUITheme.WARNING_YELLOW);
                else                                     l.setForeground(CustomUITheme.HEALTHY_GREEN);
                return l;
            }
        });

        // Populate table rows from the indicator list
        for (ThreatIndicator ti : indicators) {
            tableModel.addRow(new Object[]{
                    ti.indicator, ti.type, ti.source,
                    ti.risk.getDisplayName(), ti.firstSeen, ti.status
            });
        }

        // When a row is selected, update the advisory pane on the right
        threatTable.getSelectionModel().addListSelectionListener(e -> {
            int row = threatTable.getSelectedRow();
            if (row >= 0 && row < indicators.size()) {
                updateAdvisory(indicators.get(row), row);
            }
        });

        JScrollPane scrollTable = new JScrollPane(threatTable);
        scrollTable.setBorder(new LineBorder(CustomUITheme.BORDER_COLOR, 1));
        scrollTable.getViewport().setBackground(CustomUITheme.PANEL_BG);
        tableContainer.add(scrollTable, BorderLayout.CENTER);

        splitPane.setLeftComponent(tableContainer);

        // RIGHT: JEditorPane showing HTML threat advisory
        JPanel advisoryCard = CustomUITheme.createCardPanel("Threat Intelligence Briefing");
        advisoryCard.setLayout(new BorderLayout(0, 8));

        advisoryPane = new JEditorPane();
        advisoryPane.setContentType("text/html");
        advisoryPane.setEditable(false);
        advisoryPane.setBackground(CustomUITheme.PANEL_BG_LIGHT);

        JScrollPane scrollAdvisory = new JScrollPane(advisoryPane);
        scrollAdvisory.setBorder(new LineBorder(CustomUITheme.BORDER_COLOR, 1));
        advisoryCard.add(scrollAdvisory, BorderLayout.CENTER);

        splitPane.setRightComponent(advisoryCard);
        add(splitPane, BorderLayout.CENTER);

        // Select the first row by default
        if (!indicators.isEmpty()) {
            threatTable.setRowSelectionInterval(0, 0);
            updateAdvisory(indicators.get(0), 0);
        }
    }

    /**
     * Renders an HTML threat advisory for the selected indicator
     * in the JEditorPane on the right-hand side.
     */
    private void updateAdvisory(ThreatIndicator ti, int rowIndex) {
        String colorHex = ti.risk == RiskLevel.CRITICAL ? "#EF4444" :
                          ti.risk == RiskLevel.HIGH      ? "#F97316" : "#F59E0B";

        String html = "<html><body style='font-family: Segoe UI, sans-serif; " +
                "background-color: #1E293B; color: #F8FAFC; padding: 12px;'>" +
                "<h2 style='color: " + colorHex + "; margin-top: 0;'>THREAT ADVISORY #" + (rowIndex + 101) + "</h2>" +
                "<p style='color: #00F0FF; font-weight: bold; font-size: 13px;'>" + ti.indicator + "</p>" +
                "<hr style='border: 1px solid #27334A;'/>" +
                "<p><b>Classification:</b> <font color='#38BDF8'>" + ti.type + "</font></p>" +
                "<p><b>Source Feed:</b> " + ti.source + "</p>" +
                "<p><b>Severity:</b> <font color='" + colorHex + "'><b>" + ti.risk.getDisplayName().toUpperCase() + "</b></font></p>" +
                "<p><b>Status:</b> " + ti.status + "</p>" +
                "<p><b>First Detected:</b> " + ti.firstSeen + "</p>" +
                "<h3 style='color: #38BDF8;'>Technical Description:</h3>" +
                "<p style='color: #94A3B8; font-size: 12px; line-height: 1.4;'>" + ti.description + "</p>" +
                "<h3 style='color: #10B981;'>Neutralization Protocol:</h3>" +
                "<p style='color: #94A3B8; font-size: 12px;'>" +
                "1. Synthesize targeted detection rule for this indicator pattern.<br/>" +
                "2. Apply host isolation and revoke compromised session tokens.<br/>" +
                "3. Perform memory snapshot and update historical behavioural baselines." +
                "</p></body></html>";

        advisoryPane.setText(html);
        advisoryPane.setCaretPosition(0);
    }
}
