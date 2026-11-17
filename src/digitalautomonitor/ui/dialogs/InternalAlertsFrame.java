package digitalautomonitor.ui.dialogs;

import digitalautomonitor.model.SecurityAlert;
import digitalautomonitor.service.AlertService;
import digitalautomonitor.ui.components.CustomUITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Floating internal window displaying active security alerts inside JDesktopPane.
 */
public class InternalAlertsFrame extends JInternalFrame {

    private final AlertService alertService;
    private final DefaultTableModel tableModel;

    public InternalAlertsFrame(AlertService alertService) {
        super("Alert Quarantine & Triage", true, true, true, true);
        this.alertService = alertService;

        setSize(500, 320);
        setLocation(120, 360);
        getContentPane().setBackground(CustomUITheme.BG_DARK);
        setLayout(new BorderLayout());

        String[] cols = {"ID", "Severity", "User", "Description", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(tableModel);
        CustomUITheme.styleTable(table);

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(CustomUITheme.PANEL_BG);
        scroll.setBorder(BorderFactory.createLineBorder(CustomUITheme.BORDER_COLOR));
        add(scroll, BorderLayout.CENTER);

        // Action button bar
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 6));
        btnPanel.setBackground(CustomUITheme.PANEL_BG);
        JButton btnResolve = CustomUITheme.createStyledButton("Resolve Selected", CustomUITheme.HEALTHY_GREEN);
        btnResolve.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                String id = (String) tableModel.getValueAt(row, 0);
                alertService.resolveAlert(id);
                refreshData();
            } else {
                JOptionPane.showMessageDialog(this, "Select an alert first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
        btnPanel.add(btnResolve);
        add(btnPanel, BorderLayout.SOUTH);

        refreshData();
        alertService.addChangeListener(this::refreshData);
    }

    private void refreshData() {
        SwingUtilities.invokeLater(() -> {
            tableModel.setRowCount(0);
            List<SecurityAlert> alerts = alertService.getAllAlerts();
            for (SecurityAlert a : alerts) {
                tableModel.addRow(new Object[]{
                        a.getAlertId(),
                        a.getSeverity().getDisplayName(),
                        a.getUsername(),
                        a.getDescription(),
                        a.getStatus().getLabel()
                });
            }
        });
    }
}
