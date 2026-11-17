package digitalautomonitor.ui.dialogs;

import digitalautomonitor.model.BehaviourEvent;
import digitalautomonitor.service.EventLogService;
import digitalautomonitor.ui.components.CustomUITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Floating internal window displaying live system telemetry events inside JDesktopPane.
 */
public class InternalEventMonitorFrame extends JInternalFrame {

    private final EventLogService eventLogService;
    private final DefaultTableModel tableModel;

    public InternalEventMonitorFrame(EventLogService eventLogService) {
        super("Live Event Monitor (Floating Window)", true, true, true, true);
        this.eventLogService = eventLogService;

        setSize(480, 320);
        setLocation(30, 30);
        getContentPane().setBackground(CustomUITheme.BG_DARK);
        setLayout(new BorderLayout());

        String[] cols = {"Time", "User", "Type", "Risk", "Action"};
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

        // Refresh button bar
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 6));
        btnPanel.setBackground(CustomUITheme.PANEL_BG);
        JButton btnRefresh = CustomUITheme.createStyledButton("Refresh Stream", CustomUITheme.ACCENT_CYAN);
        btnRefresh.addActionListener(e -> refreshData());
        btnPanel.add(btnRefresh);
        add(btnPanel, BorderLayout.SOUTH);

        refreshData();
        eventLogService.addChangeListener(this::refreshData);
    }

    private void refreshData() {
        SwingUtilities.invokeLater(() -> {
            tableModel.setRowCount(0);
            List<BehaviourEvent> events = eventLogService.getRecentEvents(20);
            for (BehaviourEvent e : events) {
                tableModel.addRow(new Object[]{
                        e.getFormattedTime(),
                        e.getUsername(),
                        e.getEventType().getCategoryName(),
                        e.getRiskLevel().getDisplayName(),
                        e.getActionTaken()
                });
            }
        });
    }
}
