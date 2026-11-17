package digitalautomonitor.ui;

import digitalautomonitor.enums.RiskLevel;
import digitalautomonitor.ui.components.CustomUITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Incident Response Console.
 *
 * Each incident follows a simple 3-stage lifecycle:
 *   DETECTED → INVESTIGATING → RESOLVED
 *
 * This panel lets the security analyst:
 *   1. Select an incident from the dropdown.
 *   2. View its current status, severity, and affected user.
 *   3. Add notes to the incident log.
 *   4. Advance the incident through the three stages.
 */
public class IncidentResponsePanel extends JPanel {

    // -----------------------------------------------------------------------
    //  Simple inner class representing one security incident.
    //  We keep it here so the whole module is self-contained.
    // -----------------------------------------------------------------------
    private static class Incident {
        String id;
        String title;
        RiskLevel severity;
        String affectedUser;
        String detectionTime;
        String assignedAnalyst;
        int statusIndex;   // 0 = DETECTED, 1 = INVESTIGATING, 2 = RESOLVED
        final List<String> notes = new ArrayList<>();

        Incident(String id, String title, RiskLevel severity, String affectedUser,
                 String detectionTime, String assignedAnalyst, int statusIndex) {
            this.id             = id;
            this.title          = title;
            this.severity       = severity;
            this.affectedUser   = affectedUser;
            this.detectionTime  = detectionTime;
            this.assignedAnalyst = assignedAnalyst;
            this.statusIndex    = statusIndex;
        }

        /** Returns the current status label. */
        String getStatusLabel() {
            switch (statusIndex) {
                case 0: return "DETECTED";
                case 1: return "INVESTIGATING";
                case 2: return "RESOLVED";
                default: return "DETECTED";
            }
        }

        /** Returns the colour for the current status. */
        Color getStatusColor() {
            switch (statusIndex) {
                case 0: return new Color(239, 68, 68);   // Red — DETECTED
                case 1: return new Color(245, 158, 11);  // Amber — INVESTIGATING
                case 2: return new Color(16, 185, 129);  // Green — RESOLVED
                default: return Color.GRAY;
            }
        }
    }

    // -----------------------------------------------------------------------
    //  Data
    // -----------------------------------------------------------------------
    private final List<Incident> incidents = new ArrayList<>();
    private Incident activeIncident;

    // -----------------------------------------------------------------------
    //  UI Components
    // -----------------------------------------------------------------------
    private JComboBox<String> comboIncidents;
    private JLabel lblIncidentId;
    private JLabel lblSeverity;
    private JLabel lblUser;
    private JLabel lblTime;
    private JLabel lblStatusBadge;

    // Workflow stage progression bar (3 labelled boxes)
    private JLabel[] stageLabels;

    private JTextArea txtNotesLog;
    private JTextField txtNewNote;

    public IncidentResponsePanel() {
        setLayout(new BorderLayout(12, 12));
        setBackground(CustomUITheme.BG_DARK);
        setBorder(new EmptyBorder(14, 16, 14, 16));

        seedIncidents();
        initUI();
    }

    /** Creates predefined sample incidents for demonstration. */
    private void seedIncidents() {
        Incident inc1 = new Incident(
                "INC-201", "Mass Exfiltration Incident - user03",
                RiskLevel.CRITICAL, "user03", "09:05:14", "admin", 1 /* INVESTIGATING */);
        inc1.notes.add("[09:15:10] Automated detection triggered on 1,420 files/min burst.");
        inc1.notes.add("[09:18:22] Host temporarily blocked from primary database cluster.");
        inc1.notes.add("[09:22:45] Analyst initiated memory dump and file metadata review.");
        incidents.add(inc1);

        Incident inc2 = new Incident(
                "INC-202", "Privilege Escalation via svch0st.exe - user09",
                RiskLevel.CRITICAL, "user09", "08:12:00", "secops_analyst", 2 /* RESOLVED */);
        inc2.notes.add("[08:12:00] Suspicious binary detected in %TEMP% directory.");
        inc2.notes.add("[08:15:30] Process killed; network interface disabled.");
        inc2.notes.add("[08:30:12] Malware hash submitted to threat repository.");
        incidents.add(inc2);

        Incident inc3 = new Incident(
                "INC-203", "Brute Force Authentication Storm - admin",
                RiskLevel.HIGH, "admin", "06:40:00", "admin", 2 /* RESOLVED */);
        inc3.notes.add("[06:40:00] 14 failed login attempts from IP 198.51.100.4.");
        inc3.notes.add("[06:42:15] IP automatically added to firewall blocklist.");
        inc3.notes.add("[07:10:00] User confirmed password reset via hardware key.");
        incidents.add(inc3);

        activeIncident = inc1;
    }

    private void initUI() {
        // ---------------------------------------------------------------
        //  TOP: Incident selector + status badge
        // ---------------------------------------------------------------
        JPanel topCard = CustomUITheme.createCardPanel();
        topCard.setLayout(new BorderLayout(12, 0));

        JPanel leftSelector = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftSelector.setOpaque(false);

        JLabel lblSelect = new JLabel("Active Incident:");
        lblSelect.setFont(CustomUITheme.FONT_BODY_BOLD);
        lblSelect.setForeground(CustomUITheme.TEXT_PRIMARY);

        comboIncidents = new JComboBox<>();
        for (Incident inc : incidents) {
            comboIncidents.addItem(inc.id + " - " + inc.title);
        }
        comboIncidents.setBackground(CustomUITheme.PANEL_BG_LIGHT);
        comboIncidents.setForeground(CustomUITheme.TEXT_PRIMARY);
        comboIncidents.setPreferredSize(new Dimension(360, 30));
        comboIncidents.addActionListener(e -> onIncidentSelected());

        leftSelector.add(lblSelect);
        leftSelector.add(comboIncidents);
        topCard.add(leftSelector, BorderLayout.WEST);

        lblStatusBadge = new JLabel("STATUS: INVESTIGATING");
        lblStatusBadge.setFont(CustomUITheme.FONT_BODY_BOLD);
        lblStatusBadge.setForeground(CustomUITheme.WARNING_YELLOW);
        lblStatusBadge.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(CustomUITheme.WARNING_YELLOW, 1, true),
                new EmptyBorder(4, 10, 4, 10)
        ));

        JPanel rightBadge = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        rightBadge.setOpaque(false);
        rightBadge.add(lblStatusBadge);
        topCard.add(rightBadge, BorderLayout.EAST);

        add(topCard, BorderLayout.NORTH);

        // ---------------------------------------------------------------
        //  CENTER: Stage Pipeline + Details + Notes
        // ---------------------------------------------------------------
        JPanel centerContainer = new JPanel(new BorderLayout(0, 12));
        centerContainer.setOpaque(false);

        // Stage pipeline: DETECTED → INVESTIGATING → RESOLVED
        JPanel timelineCard = CustomUITheme.createCardPanel("Incident Response Workflow Lifecycle");
        timelineCard.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        stageLabels = new JLabel[3];
        String[] stageNames = {"1. DETECTED", "2. INVESTIGATING", "3. RESOLVED"};
        for (int i = 0; i < 3; i++) {
            stageLabels[i] = new JLabel(stageNames[i], SwingConstants.CENTER);
            stageLabels[i].setFont(CustomUITheme.FONT_BODY_BOLD);
            stageLabels[i].setOpaque(true);
            stageLabels[i].setBorder(new EmptyBorder(8, 18, 8, 18));
            timelineCard.add(stageLabels[i]);
            if (i < 2) {
                JLabel arrow = new JLabel("→");
                arrow.setFont(new Font("Segoe UI", Font.BOLD, 18));
                arrow.setForeground(CustomUITheme.TEXT_MUTED);
                timelineCard.add(arrow);
            }
        }
        centerContainer.add(timelineCard, BorderLayout.NORTH);

        // Split: left = metadata, right = notes
        JPanel detailsGrid = new JPanel(new GridLayout(1, 2, 12, 0));
        detailsGrid.setOpaque(false);

        // Left metadata card
        JPanel metaCard = CustomUITheme.createCardPanel("Incident Context & Target Metadata");
        metaCard.setLayout(new GridLayout(5, 1, 6, 8));

        lblIncidentId = buildDetailLabel("Incident ID:", "");
        lblSeverity   = buildDetailLabel("Severity Tier:", "");
        lblUser       = buildDetailLabel("Affected User:", "");
        lblTime       = buildDetailLabel("Detection Time:", "");
        JLabel lblAnalyst = buildDetailLabel("Assigned Lead:", "admin");

        metaCard.add(lblIncidentId);
        metaCard.add(lblSeverity);
        metaCard.add(lblUser);
        metaCard.add(lblTime);
        metaCard.add(lblAnalyst);
        detailsGrid.add(metaCard);

        // Right notes card
        JPanel notesCard = CustomUITheme.createCardPanel("Analyst Incident Notes & Actions");
        notesCard.setLayout(new BorderLayout(0, 8));

        txtNotesLog = new JTextArea();
        txtNotesLog.setEditable(false);
        txtNotesLog.setBackground(CustomUITheme.PANEL_BG_LIGHT);
        txtNotesLog.setForeground(CustomUITheme.TEXT_PRIMARY);
        txtNotesLog.setFont(CustomUITheme.FONT_MONO);
        txtNotesLog.setBorder(new EmptyBorder(8, 10, 8, 10));

        JScrollPane scrollNotes = new JScrollPane(txtNotesLog);
        scrollNotes.setBorder(new LineBorder(CustomUITheme.BORDER_COLOR, 1));
        notesCard.add(scrollNotes, BorderLayout.CENTER);

        // Add note input row
        JPanel addNotePanel = new JPanel(new BorderLayout(6, 0));
        addNotePanel.setOpaque(false);

        txtNewNote = new JTextField();
        txtNewNote.setBackground(CustomUITheme.PANEL_BG_LIGHT);
        txtNewNote.setForeground(CustomUITheme.TEXT_PRIMARY);
        txtNewNote.setCaretColor(CustomUITheme.ACCENT_CYAN);
        txtNewNote.setFont(CustomUITheme.FONT_BODY);

        JButton btnAddNote = CustomUITheme.createStyledButton("Add Note", CustomUITheme.ACCENT_CYAN);
        btnAddNote.addActionListener(e -> addAnalystNote());

        addNotePanel.add(txtNewNote, BorderLayout.CENTER);
        addNotePanel.add(btnAddNote, BorderLayout.EAST);
        notesCard.add(addNotePanel, BorderLayout.SOUTH);

        detailsGrid.add(notesCard);
        centerContainer.add(detailsGrid, BorderLayout.CENTER);
        add(centerContainer, BorderLayout.CENTER);

        // ---------------------------------------------------------------
        //  BOTTOM: Stage transition action buttons
        // ---------------------------------------------------------------
        JPanel bottomBar = CustomUITheme.createCardPanel();
        bottomBar.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 4));

        JButton btnDetect     = CustomUITheme.createStyledButton("Mark Detected",    CustomUITheme.CRITICAL_RED);
        JButton btnInvestigate = CustomUITheme.createStyledButton("Start Investigation", CustomUITheme.ACCENT_BLUE);
        JButton btnResolve    = CustomUITheme.createStyledButton("Resolve Incident", CustomUITheme.HEALTHY_GREEN);

        // Each button advances the incident to the corresponding stage
        btnDetect.addActionListener(e     -> updateIncidentStage(0));  // DETECTED
        btnInvestigate.addActionListener(e -> updateIncidentStage(1)); // INVESTIGATING
        btnResolve.addActionListener(e    -> updateIncidentStage(2));  // RESOLVED

        bottomBar.add(btnDetect);
        bottomBar.add(btnInvestigate);
        bottomBar.add(btnResolve);
        add(bottomBar, BorderLayout.SOUTH);

        // Populate the view with the first incident
        refreshIncidentView();
    }

    /** Creates a key: value HTML label. */
    private JLabel buildDetailLabel(String key, String val) {
        JLabel l = new JLabel(
                "<html><font color='#94A3B8'>" + key + "</font> " +
                "<font color='#F8FAFC'><b>" + val + "</b></font></html>"
        );
        l.setFont(CustomUITheme.FONT_BODY);
        return l;
    }

    /** Called when the user selects a different incident from the dropdown. */
    private void onIncidentSelected() {
        int idx = comboIncidents.getSelectedIndex();
        if (idx >= 0 && idx < incidents.size()) {
            activeIncident = incidents.get(idx);
            refreshIncidentView();
        }
    }

    /**
     * Updates all UI components to reflect the currently selected incident.
     * This is the single method responsible for keeping the view in sync
     * with the data — no scattered updates elsewhere.
     */
    private void refreshIncidentView() {
        if (activeIncident == null) return;

        // Update status badge
        Color statusColor = activeIncident.getStatusColor();
        lblStatusBadge.setText("STATUS: " + activeIncident.getStatusLabel());
        lblStatusBadge.setForeground(statusColor);
        lblStatusBadge.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(statusColor, 1, true),
                new EmptyBorder(4, 10, 4, 10)
        ));

        // Update the 3-stage timeline — highlight the current stage
        for (int i = 0; i < stageLabels.length; i++) {
            if (i == activeIncident.statusIndex) {
                stageLabels[i].setBackground(statusColor);
                stageLabels[i].setForeground(Color.WHITE);
            } else {
                stageLabels[i].setBackground(CustomUITheme.PANEL_BG);
                stageLabels[i].setForeground(CustomUITheme.TEXT_MUTED);
            }
        }

        // Update metadata labels
        lblIncidentId.setText(html("Incident ID:", activeIncident.id, "#F8FAFC"));
        String sevColor = activeIncident.severity == RiskLevel.CRITICAL ? "#EF4444" : "#F97316";
        lblSeverity.setText(html("Severity Tier:", activeIncident.severity.getDisplayName(), sevColor));
        lblUser.setText(html("Affected User:", activeIncident.affectedUser, "#00F0FF"));
        lblTime.setText(html("Detection Time:", activeIncident.detectionTime, "#F8FAFC"));

        // Update notes log
        StringBuilder sb = new StringBuilder();
        for (String note : activeIncident.notes) {
            sb.append(note).append("\n");
        }
        txtNotesLog.setText(sb.toString());
        txtNotesLog.setCaretPosition(txtNotesLog.getDocument().getLength());
    }

    private String html(String key, String val, String color) {
        return "<html><font color='#94A3B8'>" + key + "</font> <font color='" + color + "'><b>" + val + "</b></font></html>";
    }

    /** Appends a timestamped note to the active incident's log. */
    private void addAnalystNote() {
        String note = txtNewNote.getText().trim();
        if (note.isEmpty() || activeIncident == null) return;
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        activeIncident.notes.add("[" + timestamp + "] (admin): " + note);
        txtNewNote.setText("");
        refreshIncidentView();
    }

    /**
     * Updates the incident's stage (0=DETECTED, 1=INVESTIGATING, 2=RESOLVED)
     * and adds an automatic note recording the transition.
     */
    private void updateIncidentStage(int newStatusIndex) {
        if (activeIncident == null) return;
        activeIncident.statusIndex = newStatusIndex;
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        activeIncident.notes.add("[" + timestamp + "] Workflow advanced to stage: " + activeIncident.getStatusLabel());
        refreshIncidentView();
        JOptionPane.showMessageDialog(this,
                "Incident " + activeIncident.id + " transitioned to: " + activeIncident.getStatusLabel(),
                "Workflow Advanced", JOptionPane.INFORMATION_MESSAGE);
    }
}
