package digitalautomonitor.ui;

import digitalautomonitor.enums.AlertStatus;
import digitalautomonitor.enums.RiskLevel;
import digitalautomonitor.model.SecurityAlert;
import digitalautomonitor.service.AlertService;
import digitalautomonitor.ui.components.CustomUITheme;
import digitalautomonitor.ui.dialogs.AlertDetailsDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Security Alerts Management Panel (Inflammation Console) with lifecycle actions,
 * severity badges, and detailed investigation modals.
 */
public class SecurityAlertsPanel extends JPanel {

    private final AlertService alertService;
    private final Frame parentFrame;

    private DefaultTableModel tableModel;
    private JTable alertTable;
    private JLabel lblAlertCount;

    public SecurityAlertsPanel(AlertService alertService, Frame parentFrame) {
        this.alertService = alertService;
        this.parentFrame = parentFrame;

        setLayout(new BorderLayout(12, 12));
        setBackground(CustomUITheme.BG_DARK);
        setBorder(new EmptyBorder(14, 16, 14, 16));

        initUI();
        registerListeners();
    }

    private void initUI() {
        // TOP: Action Bar
        JPanel topBar = CustomUITheme.createCardPanel();
        topBar.setLayout(new BorderLayout());

        JPanel leftInfo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftInfo.setOpaque(false);
        JLabel title = new JLabel("SECURITY ALERTS REPOSITORY");
        title.setFont(CustomUITheme.FONT_TITLE);
        title.setForeground(CustomUITheme.TEXT_PRIMARY);

        lblAlertCount = new JLabel("(7 Active Alerts)");
        lblAlertCount.setFont(CustomUITheme.FONT_SUBTITLE);
        lblAlertCount.setForeground(CustomUITheme.ACCENT_CYAN);

        leftInfo.add(title);
        leftInfo.add(lblAlertCount);
        topBar.add(leftInfo, BorderLayout.WEST);

        // Action Buttons: Investigate, Acknowledge, Resolve, Escalate, Refresh
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setOpaque(false);

        JButton btnInvestigate = CustomUITheme.createStyledButton("Investigate", CustomUITheme.ACCENT_CYAN);
        JButton btnAck = CustomUITheme.createStyledButton("Acknowledge", CustomUITheme.ACCENT_BLUE);
        JButton btnResolve = CustomUITheme.createStyledButton("Resolve", CustomUITheme.HEALTHY_GREEN);
        JButton btnEscalate = CustomUITheme.createStyledButton("Escalate", CustomUITheme.CRITICAL_RED);
        JButton btnRefresh = CustomUITheme.createStyledButton("Refresh", CustomUITheme.BORDER_COLOR);

        btnInvestigate.addActionListener(e -> openSelectedAlertDetails());
        btnAck.addActionListener(e -> acknowledgeSelected());
        btnResolve.addActionListener(e -> resolveSelected());
        btnEscalate.addActionListener(e -> escalateSelected());
        btnRefresh.addActionListener(e -> refreshAlertTable());

        btnPanel.add(btnInvestigate);
        btnPanel.add(btnAck);
        btnPanel.add(btnResolve);
        btnPanel.add(btnEscalate);
        btnPanel.add(btnRefresh);

        topBar.add(btnPanel, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // CENTER: Alerts Table
        JPanel tableContainer = CustomUITheme.createCardPanel();
        tableContainer.setLayout(new BorderLayout(0, 8));

        String[] cols = {"Alert ID", "Time", "Severity", "User", "Category", "Description", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        alertTable = new JTable(tableModel);
        CustomUITheme.styleTable(alertTable);

        // Custom Renderer for Severity (Col 2)
        alertTable.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                l.setFont(CustomUITheme.FONT_BODY_BOLD);
                String val = String.valueOf(value);
                if ("CRITICAL".equalsIgnoreCase(val)) l.setForeground(CustomUITheme.CRITICAL_RED);
                else if ("HIGH".equalsIgnoreCase(val) || "HIGH RISK".equalsIgnoreCase(val)) l.setForeground(CustomUITheme.WARNING_ORANGE);
                else if ("MEDIUM".equalsIgnoreCase(val) || "MEDIUM RISK".equalsIgnoreCase(val)) l.setForeground(CustomUITheme.WARNING_YELLOW);
                else if ("LOW".equalsIgnoreCase(val) || "LOW RISK".equalsIgnoreCase(val)) l.setForeground(CustomUITheme.ACCENT_BLUE);
                else l.setForeground(CustomUITheme.HEALTHY_GREEN);
                return l;
            }
        });

        // Custom Renderer for Status (Col 6)
        alertTable.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                l.setFont(CustomUITheme.FONT_BODY_BOLD);
                String val = String.valueOf(value);
                if ("NEW".equalsIgnoreCase(val)) l.setForeground(CustomUITheme.CRITICAL_RED);
                else if ("INVESTIGATING".equalsIgnoreCase(val)) l.setForeground(CustomUITheme.WARNING_YELLOW);
                else if ("ACKNOWLEDGED".equalsIgnoreCase(val)) l.setForeground(CustomUITheme.ACCENT_BLUE);
                else l.setForeground(CustomUITheme.HEALTHY_GREEN);
                return l;
            }
        });

        // Double click to investigate
        alertTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    openSelectedAlertDetails();
                }
            }
        });

        JScrollPane scroll = new JScrollPane(alertTable);
        scroll.getViewport().setBackground(CustomUITheme.PANEL_BG);
        scroll.setBorder(BorderFactory.createLineBorder(CustomUITheme.BORDER_COLOR));
        tableContainer.add(scroll, BorderLayout.CENTER);

        add(tableContainer, BorderLayout.CENTER);

        refreshAlertTable();
    }

    private void openSelectedAlertDetails() {
        int row = alertTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an alert from the table to investigate.",
                    "No Alert Selected", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String id = (String) tableModel.getValueAt(row, 0);
        SecurityAlert alert = alertService.getAlertById(id);
        if (alert != null) {
            AlertDetailsDialog dialog = new AlertDetailsDialog(parentFrame, alert, alertService);
            dialog.setVisible(true);
            refreshAlertTable();
        }
    }

    private void acknowledgeSelected() {
        int row = alertTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select an alert to acknowledge.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String id = (String) tableModel.getValueAt(row, 0);
        alertService.acknowledgeAlert(id, "admin");
        refreshAlertTable();
        JOptionPane.showMessageDialog(this, "Alert " + id + " marked as Acknowledged.", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void resolveSelected() {
        int row = alertTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select an alert to resolve.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String id = (String) tableModel.getValueAt(row, 0);
        alertService.resolveAlert(id);
        refreshAlertTable();
        JOptionPane.showMessageDialog(this, "Alert " + id + " marked as Resolved. Incident closed.", "Resolved", JOptionPane.INFORMATION_MESSAGE);
    }

    private void escalateSelected() {
        int row = alertTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select an alert to escalate.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String id = (String) tableModel.getValueAt(row, 0);
        SecurityAlert alert = alertService.getAlertById(id);
        if (alert != null) {
            alert.setSeverity(RiskLevel.CRITICAL);
            alert.setStatus(AlertStatus.INVESTIGATING);
            alertService.updateStatus(id, AlertStatus.INVESTIGATING);
            refreshAlertTable();
            JOptionPane.showMessageDialog(this, "Alert " + id + " ESCALATED TO CRITICAL! Lead response team notified.",
                    "Alert Escalated", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void refreshAlertTable() {
        tableModel.setRowCount(0);
        List<SecurityAlert> alerts = alertService.getAllAlerts();
        for (SecurityAlert a : alerts) {
            tableModel.addRow(new Object[]{
                    a.getAlertId(),
                    a.getFormattedTime(),
                    a.getSeverity().getDisplayName(),
                    a.getUsername(),
                    a.getCategory().getCategoryName(),
                    a.getDescription(),
                    a.getStatus().getLabel()
            });
        }
        lblAlertCount.setText("(" + alertService.getActiveAlertsCount() + " Active Alerts)");
    }

    private void registerListeners() {
        alertService.addChangeListener(() -> SwingUtilities.invokeLater(this::refreshAlertTable));
    }
}
