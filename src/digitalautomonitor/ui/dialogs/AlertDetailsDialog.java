package digitalautomonitor.ui.dialogs;

import digitalautomonitor.enums.AlertStatus;
import digitalautomonitor.model.SecurityAlert;
import digitalautomonitor.service.AlertService;
import digitalautomonitor.ui.components.CustomUITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Detailed investigation dialog for a security alert (Inflammation inspection).
 */
public class AlertDetailsDialog extends JDialog {

    private final SecurityAlert alert;
    private final AlertService alertService;
    private JLabel statusLabel;

    public AlertDetailsDialog(Frame owner, SecurityAlert alert, AlertService alertService) {
        super(owner, "Security Alert Investigation | " + alert.getAlertId(), true);
        this.alert = alert;
        this.alertService = alertService;

        setSize(650, 520);
        setLocationRelativeTo(owner);
        getContentPane().setBackground(CustomUITheme.BG_DARK);
        setLayout(new BorderLayout(10, 10));

        initUI();
    }

    private void initUI() {
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(CustomUITheme.PANEL_BG);
        headerPanel.setBorder(new EmptyBorder(14, 18, 14, 18));

        JLabel title = new JLabel("INFLAMMATORY ALERT INSPECTION");
        title.setFont(CustomUITheme.FONT_TITLE);
        title.setForeground(alert.getSeverity().getColor());

        JLabel idLabel = new JLabel(alert.getAlertId() + " (" + alert.getSeverity().getDisplayName() + ")");
        idLabel.setFont(CustomUITheme.FONT_SUBTITLE);
        idLabel.setForeground(CustomUITheme.TEXT_MUTED);

        headerPanel.add(title, BorderLayout.WEST);
        headerPanel.add(idLabel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Content
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(CustomUITheme.BG_DARK);
        contentPanel.setBorder(new EmptyBorder(16, 20, 16, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // User & Time
        addDetailRow(contentPanel, gbc, row++, "Monitored User:", alert.getUsername());
        addDetailRow(contentPanel, gbc, row++, "Timestamp:", alert.getFormattedFullTime());
        addDetailRow(contentPanel, gbc, row++, "Category:", alert.getCategory().getCategoryName());

        // Status
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        JLabel lblStatusKey = new JLabel("Current Status:");
        lblStatusKey.setFont(CustomUITheme.FONT_BODY_BOLD);
        lblStatusKey.setForeground(CustomUITheme.TEXT_MUTED);
        contentPanel.add(lblStatusKey, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        statusLabel = new JLabel(alert.getStatus().getLabel());
        statusLabel.setFont(CustomUITheme.FONT_BODY_BOLD);
        statusLabel.setForeground(alert.getStatus().getBadgeColor());
        contentPanel.add(statusLabel, gbc);
        row++;

        // Description
        addDetailRow(contentPanel, gbc, row++, "Event Detail:", alert.getDescription());

        // Detection Reason
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        JLabel lblReason = new JLabel("Detection Reason:");
        lblReason.setFont(CustomUITheme.FONT_BODY_BOLD);
        lblReason.setForeground(CustomUITheme.TEXT_MUTED);
        contentPanel.add(lblReason, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JTextArea txtReason = new JTextArea(alert.getDetectionReason());
        txtReason.setEditable(false);
        txtReason.setLineWrap(true);
        txtReason.setWrapStyleWord(true);
        txtReason.setBackground(CustomUITheme.PANEL_BG);
        txtReason.setForeground(CustomUITheme.TEXT_PRIMARY);
        txtReason.setFont(CustomUITheme.FONT_BODY);
        txtReason.setBorder(new EmptyBorder(6, 8, 6, 8));
        contentPanel.add(txtReason, gbc);
        row++;

        // Behaviour Deviation
        addDetailRow(contentPanel, gbc, row++, "Baseline Deviation:", alert.getDeviationSummary());

        // Recommended Action
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        JLabel lblRec = new JLabel("Recommended Action:");
        lblRec.setFont(CustomUITheme.FONT_BODY_BOLD);
        lblRec.setForeground(CustomUITheme.ACCENT_CYAN);
        contentPanel.add(lblRec, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JTextArea txtRec = new JTextArea(alert.getRecommendedAction());
        txtRec.setEditable(false);
        txtRec.setLineWrap(true);
        txtRec.setWrapStyleWord(true);
        txtRec.setBackground(CustomUITheme.PANEL_BG);
        txtRec.setForeground(CustomUITheme.HEALTHY_GREEN);
        txtRec.setFont(CustomUITheme.FONT_BODY_BOLD);
        txtRec.setBorder(new EmptyBorder(6, 8, 6, 8));
        contentPanel.add(txtRec, gbc);

        JScrollPane scroll = new JScrollPane(contentPanel);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(CustomUITheme.BG_DARK);
        add(scroll, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        buttonPanel.setBackground(CustomUITheme.PANEL_BG);
        buttonPanel.setBorder(new LineBorder(CustomUITheme.BORDER_COLOR, 1, false));

        JButton btnAck = CustomUITheme.createStyledButton("Acknowledge Alert", CustomUITheme.ACCENT_BLUE);
        JButton btnResolve = CustomUITheme.createStyledButton("Mark Resolved", CustomUITheme.HEALTHY_GREEN);
        JButton btnClose = CustomUITheme.createStyledButton("Close", CustomUITheme.BORDER_COLOR);

        btnAck.addActionListener(e -> {
            alertService.acknowledgeAlert(alert.getAlertId(), "admin");
            statusLabel.setText(AlertStatus.ACKNOWLEDGED.getLabel());
            statusLabel.setForeground(AlertStatus.ACKNOWLEDGED.getBadgeColor());
            JOptionPane.showMessageDialog(this, "Alert " + alert.getAlertId() + " acknowledged by lead analyst.",
                    "Alert Acknowledged", JOptionPane.INFORMATION_MESSAGE);
        });

        btnResolve.addActionListener(e -> {
            alertService.resolveAlert(alert.getAlertId());
            statusLabel.setText(AlertStatus.RESOLVED.getLabel());
            statusLabel.setForeground(AlertStatus.RESOLVED.getBadgeColor());
            JOptionPane.showMessageDialog(this, "Alert " + alert.getAlertId() + " successfully resolved. Homeostasis restored.",
                    "Alert Resolved", JOptionPane.INFORMATION_MESSAGE);
        });

        btnClose.addActionListener(e -> dispose());

        buttonPanel.add(btnAck);
        buttonPanel.add(btnResolve);
        buttonPanel.add(btnClose);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addDetailRow(JPanel panel, GridBagConstraints gbc, int row, String label, String value) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        JLabel lbl = new JLabel(label);
        lbl.setFont(CustomUITheme.FONT_BODY_BOLD);
        lbl.setForeground(CustomUITheme.TEXT_MUTED);
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JLabel val = new JLabel(value != null ? value : "N/A");
        val.setFont(CustomUITheme.FONT_BODY);
        val.setForeground(CustomUITheme.TEXT_PRIMARY);
        panel.add(val, gbc);
    }
}
