package digitalautomonitor.ui;

import digitalautomonitor.enums.RiskLevel;
import digitalautomonitor.model.BehaviourProfile;
import digitalautomonitor.model.User;
import digitalautomonitor.service.BehaviourMonitorService;
import digitalautomonitor.ui.components.CustomUITheme;
import digitalautomonitor.ui.dialogs.AddUserDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * User Behaviour Profiles Panel managing digital entity baselines and immune memory.
 */
public class UserProfilesPanel extends JPanel {

    private final BehaviourMonitorService monitorService;
    private final Frame parentFrame;

    private DefaultTableModel tableModel;
    private JTable userTable;
    private JLabel lblUserCount;

    public UserProfilesPanel(BehaviourMonitorService monitorService, Frame parentFrame) {
        this.monitorService = monitorService;
        this.parentFrame = parentFrame;

        setLayout(new BorderLayout(12, 12));
        setBackground(CustomUITheme.BG_DARK);
        setBorder(new EmptyBorder(14, 16, 14, 16));

        initUI();
        registerListeners();
    }

    private void initUI() {
        // TOP ACTION BAR
        JPanel topBar = CustomUITheme.createCardPanel();
        topBar.setLayout(new BorderLayout());

        JPanel leftTitle = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftTitle.setOpaque(false);
        JLabel title = new JLabel("MONITORED USER BEHAVIOUR PROFILES");
        title.setFont(CustomUITheme.FONT_TITLE);
        title.setForeground(CustomUITheme.TEXT_PRIMARY);

        lblUserCount = new JLabel("(" + monitorService.getAllUsers().size() + " Enrolled)");
        lblUserCount.setFont(CustomUITheme.FONT_SUBTITLE);
        lblUserCount.setForeground(CustomUITheme.ACCENT_CYAN);

        leftTitle.add(title);
        leftTitle.add(lblUserCount);
        topBar.add(leftTitle, BorderLayout.WEST);

        // Buttons: Add User, Edit User, Delete User, View Profile, Reset Baseline
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setOpaque(false);

        JButton btnAdd = CustomUITheme.createStyledButton("Add User", CustomUITheme.HEALTHY_GREEN);
        JButton btnEdit = CustomUITheme.createStyledButton("Edit User", CustomUITheme.ACCENT_CYAN);
        JButton btnDelete = CustomUITheme.createStyledButton("Delete User", CustomUITheme.CRITICAL_RED);
        JButton btnView = CustomUITheme.createStyledButton("View Profile", CustomUITheme.ACCENT_BLUE);
        JButton btnReset = CustomUITheme.createStyledButton("Reset Baseline", CustomUITheme.BORDER_COLOR);

        btnAdd.addActionListener(e -> openAddUserDialog());
        btnEdit.addActionListener(e -> editSelectedUser());
        btnDelete.addActionListener(e -> deleteSelectedUser());
        btnView.addActionListener(e -> viewSelectedProfile());
        btnReset.addActionListener(e -> resetSelectedBaseline());

        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDelete);
        btnPanel.add(btnView);
        btnPanel.add(btnReset);

        topBar.add(btnPanel, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // CENTER TABLE
        JPanel tableContainer = CustomUITheme.createCardPanel();
        tableContainer.setLayout(new BorderLayout(0, 8));

        String[] cols = {"User ID", "Username", "Role", "Baseline Score", "Risk Level", "Last Active", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        userTable = new JTable(tableModel);
        CustomUITheme.styleTable(userTable);

        // Renderer for Risk Level
        userTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
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
                else l.setForeground(CustomUITheme.HEALTHY_GREEN);
                return l;
            }
        });

        // Renderer for Status
        userTable.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                l.setFont(CustomUITheme.FONT_BODY_BOLD);
                String val = String.valueOf(value);
                if ("ACTIVE".equalsIgnoreCase(val) || "MONITORED".equalsIgnoreCase(val)) {
                    l.setForeground(CustomUITheme.HEALTHY_GREEN);
                } else {
                    l.setForeground(CustomUITheme.TEXT_MUTED);
                }
                return l;
            }
        });

        JScrollPane scroll = new JScrollPane(userTable);
        scroll.getViewport().setBackground(CustomUITheme.PANEL_BG);
        scroll.setBorder(BorderFactory.createLineBorder(CustomUITheme.BORDER_COLOR));
        tableContainer.add(scroll, BorderLayout.CENTER);

        add(tableContainer, BorderLayout.CENTER);

        refreshTable();
    }

    private void openAddUserDialog() {
        AddUserDialog dialog = new AddUserDialog(parentFrame, monitorService);
        dialog.setVisible(true);
    }

    private void editSelectedUser() {
        int row = userTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a user profile to edit.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String username = (String) tableModel.getValueAt(row, 1);
        User user = monitorService.getUserByUsername(username);
        if (user != null) {
            String newRole = JOptionPane.showInputDialog(this, "Update Role for " + username + ":", user.getRole());
            if (newRole != null && !newRole.trim().isEmpty()) {
                user.setRole(newRole.trim());
                refreshTable();
                JOptionPane.showMessageDialog(this, "User profile updated.", "Saved", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void deleteSelectedUser() {
        int row = userTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a user to remove.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String userId = (String) tableModel.getValueAt(row, 0);
        String username = (String) tableModel.getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to remove '" + username + "' from immune surveillance?",
                "Confirm Removal", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            monitorService.removeUser(userId);
            refreshTable();
            JOptionPane.showMessageDialog(this, "User " + username + " removed.", "Entity Deprovisioned", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void viewSelectedProfile() {
        int row = userTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a user to inspect profile.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String username = (String) tableModel.getValueAt(row, 1);
        User u = monitorService.getUserByUsername(username);
        BehaviourProfile p = monitorService.getProfile(username);

        if (u != null && p != null) {
            String info = String.format(
                    "ENTITY PROFILE: %s\n" +
                    "User ID:           %s\n" +
                    "Department:        %s\n" +
                    "Role:              %s\n" +
                    "Homeostasis Score: %d/100\n" +
                    "Risk Level:        %s\n\n" +
                    "LEARNED BASELINES:\n" +
                    "• CPU Baseline:      %d%%\n" +
                    "• Memory Baseline:   %d%%\n" +
                    "• File Access Rate:  %d files/min\n" +
                    "• Login Frequency:   %d / day\n" +
                    "• Network Egress:    %d MB/hr\n" +
                    "• Monitoring Status: %s",
                    u.getUsername(), u.getUserId(), u.getDepartment(), u.getRole(),
                    u.getBaselineScore(), u.getRiskLevel(),
                    p.getBaselineCpuPercent(), p.getBaselineMemoryPercent(),
                    p.getBaselineFileAccessCount(), p.getBaselineLoginFrequency(),
                    p.getBaselineNetworkMB(),
                    u.isMonitoringEnabled() ? "ACTIVE (Watchdog armed)" : "PAUSED"
            );
            JOptionPane.showMessageDialog(this, info, "Profile Inspection | " + username, JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void resetSelectedBaseline() {
        int row = userTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a user to reset baseline.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String username = (String) tableModel.getValueAt(row, 1);
        monitorService.resetBaseline(username);
        refreshTable();
        JOptionPane.showMessageDialog(this, "Immune baseline reset for " + username + ".", "Baseline Synchronized", JOptionPane.INFORMATION_MESSAGE);
    }

    public void refreshTable() {
        tableModel.setRowCount(0);
        List<User> users = monitorService.getAllUsers();
        for (User u : users) {
            tableModel.addRow(new Object[]{
                    u.getUserId(),
                    u.getUsername(),
                    u.getRole(),
                    u.getBaselineScore() + "/100",
                    u.getRiskLevel().getDisplayName(),
                    u.getFormattedLastActive(),
                    u.isMonitoringEnabled() ? "MONITORED" : "PAUSED"
            });
        }
        lblUserCount.setText("(" + users.size() + " Enrolled)");
    }

    private void registerListeners() {
        monitorService.addChangeListener(() -> SwingUtilities.invokeLater(this::refreshTable));
    }
}
