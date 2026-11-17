package digitalautomonitor.ui;

import digitalautomonitor.enums.RiskLevel;
import digitalautomonitor.model.BehaviourEvent;
import digitalautomonitor.model.BehaviourProfile;
import digitalautomonitor.service.BehaviourMonitorService;
import digitalautomonitor.service.EventLogService;
import digitalautomonitor.service.SimulationService;
import digitalautomonitor.ui.components.CustomUITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Behaviour Monitor Page displaying real-time multi-vector metric bars,
 * JList quick selection, and historical behavioural deviations per monitored user.
 */
public class BehaviourMonitorPanel extends JPanel {

    private final SimulationService simulationService;
    private final BehaviourMonitorService monitorService;
    private final EventLogService eventLogService;

    private JComboBox<String> comboUser;
    private JList<String> listQuickUsers;
    private JToggleButton toggleMonitoring;

    // Category progress bars and percentage labels
    private final Map<String, JProgressBar> categoryBars = new HashMap<>();
    private final Map<String, JLabel> categoryLabels = new HashMap<>();

    private DefaultTableModel historyModel;
    private JTable historyTable;

    public BehaviourMonitorPanel(SimulationService simulationService) {
        this.simulationService = simulationService;
        this.monitorService = simulationService.getMonitorService();
        this.eventLogService = simulationService.getEventLogService();

        setLayout(new BorderLayout(12, 12));
        setBackground(CustomUITheme.BG_DARK);
        setBorder(new EmptyBorder(14, 16, 14, 16));

        initUI();
        registerListeners();
    }

    private void initUI() {
        // TOP CONTROL BAR
        JPanel topBar = CustomUITheme.createCardPanel();
        topBar.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 6));

        JLabel lblSelectUser = new JLabel("Monitored Entity:");
        lblSelectUser.setFont(CustomUITheme.FONT_BODY_BOLD);
        lblSelectUser.setForeground(CustomUITheme.TEXT_PRIMARY);

        comboUser = new JComboBox<>(monitorService.getAllUsernames().toArray(new String[0]));
        comboUser.setFont(CustomUITheme.FONT_BODY);
        comboUser.setBackground(CustomUITheme.PANEL_BG_LIGHT);
        comboUser.setForeground(CustomUITheme.TEXT_PRIMARY);
        comboUser.setPreferredSize(new Dimension(160, 30));
        comboUser.addActionListener(e -> onUserSelected());

        JSeparator sep1 = new JSeparator(SwingConstants.VERTICAL);
        sep1.setPreferredSize(new Dimension(2, 24));
        sep1.setForeground(CustomUITheme.BORDER_COLOR);

        toggleMonitoring = new JToggleButton("MONITORING ON", true);
        toggleMonitoring.setFont(CustomUITheme.FONT_BODY_BOLD);
        toggleMonitoring.setForeground(Color.WHITE);
        toggleMonitoring.setBackground(CustomUITheme.HEALTHY_GREEN);
        toggleMonitoring.setFocusPainted(false);
        toggleMonitoring.setPreferredSize(new Dimension(170, 30));
        toggleMonitoring.addActionListener(e -> {
            boolean on = toggleMonitoring.isSelected();
            toggleMonitoring.setText(on ? "MONITORING ON" : "MONITORING PAUSED");
            toggleMonitoring.setBackground(on ? CustomUITheme.HEALTHY_GREEN : CustomUITheme.WARNING_YELLOW);
            String selected = (String) comboUser.getSelectedItem();
            if (selected != null) {
                monitorService.toggleMonitoring(selected, on);
            }
        });

        JSeparator sep2 = new JSeparator(SwingConstants.VERTICAL);
        sep2.setPreferredSize(new Dimension(2, 24));
        sep2.setForeground(CustomUITheme.BORDER_COLOR);

        JButton btnReset = CustomUITheme.createStyledButton("Reset Baseline", CustomUITheme.ACCENT_CYAN);
        btnReset.addActionListener(e -> {
            String selected = (String) comboUser.getSelectedItem();
            if (selected != null) {
                monitorService.resetBaseline(selected);
                onUserSelected();
                JOptionPane.showMessageDialog(this, "Baseline for " + selected + " reset to healthy homeostatic norms.",
                        "Baseline Synchronized", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        topBar.add(lblSelectUser);
        topBar.add(comboUser);
        topBar.add(sep1);
        topBar.add(toggleMonitoring);
        topBar.add(sep2);
        topBar.add(btnReset);

        add(topBar, BorderLayout.NORTH);

        // CENTER: SPLIT PANEL (Top half: 9 Category Progress Bars, Bottom half: History Table)
        JPanel contentPanel = new JPanel(new GridLayout(2, 1, 0, 12));
        contentPanel.setOpaque(false);

        // TOP HALF: Behaviour Categories Grid
        JPanel categoriesPanel = CustomUITheme.createCardPanel("Live Behavioural Categories Telemetry");
        categoriesPanel.setLayout(new GridLayout(3, 3, 16, 12));

        String[] categories = {
                "Login Frequency", "File Access", "Process Activity",
                "Network Activity", "CPU Usage", "Memory Usage",
                "USB Activity", "Application Usage", "Session Duration"
        };

        for (String cat : categories) {
            JPanel catBox = new JPanel(new BorderLayout(6, 6));
            catBox.setBackground(CustomUITheme.PANEL_BG_LIGHT);
            catBox.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(CustomUITheme.BORDER_COLOR, 1, true),
                    new EmptyBorder(8, 10, 8, 10)
            ));

            JLabel title = new JLabel(cat);
            title.setFont(CustomUITheme.FONT_BODY_BOLD);
            title.setForeground(CustomUITheme.TEXT_PRIMARY);

            JLabel valLabel = new JLabel("45%");
            valLabel.setFont(CustomUITheme.FONT_BODY_BOLD);
            valLabel.setForeground(CustomUITheme.ACCENT_CYAN);

            JPanel headerRow = new JPanel(new BorderLayout());
            headerRow.setOpaque(false);
            headerRow.add(title, BorderLayout.WEST);
            headerRow.add(valLabel, BorderLayout.EAST);

            JProgressBar bar = new JProgressBar(0, 100);
            bar.setValue(45);
            CustomUITheme.styleProgressBar(bar, CustomUITheme.ACCENT_CYAN);

            catBox.add(headerRow, BorderLayout.NORTH);
            catBox.add(bar, BorderLayout.CENTER);

            categoryBars.put(cat, bar);
            categoryLabels.put(cat, valLabel);
            categoriesPanel.add(catBox);
        }

        contentPanel.add(categoriesPanel);

        // BOTTOM HALF: Behavioural History Table
        JPanel historyContainer = CustomUITheme.createCardPanel("Entity Behavioural History & Baselines");
        historyContainer.setLayout(new BorderLayout(0, 8));

        String[] cols = {"Timestamp", "User", "Metric", "Observed Value", "Baseline", "Deviation", "Risk Level"};
        historyModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        historyTable = new JTable(historyModel);
        CustomUITheme.styleTable(historyTable);

        // Risk badge renderer
        historyTable.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                l.setFont(CustomUITheme.FONT_BODY_BOLD);
                String txt = String.valueOf(value);
                if ("CRITICAL".equalsIgnoreCase(txt)) l.setForeground(CustomUITheme.CRITICAL_RED);
                else if ("HIGH RISK".equalsIgnoreCase(txt) || "HIGH".equalsIgnoreCase(txt)) l.setForeground(CustomUITheme.WARNING_ORANGE);
                else if ("MEDIUM RISK".equalsIgnoreCase(txt) || "MEDIUM".equalsIgnoreCase(txt)) l.setForeground(CustomUITheme.WARNING_YELLOW);
                else l.setForeground(CustomUITheme.HEALTHY_GREEN);
                return l;
            }
        });

        JScrollPane scroll = new JScrollPane(historyTable);
        JViewport viewport = scroll.getViewport();
        viewport.setBackground(CustomUITheme.PANEL_BG);
        scroll.setBorder(BorderFactory.createLineBorder(CustomUITheme.BORDER_COLOR));
        historyContainer.add(scroll, BorderLayout.CENTER);

        // Quick User Selector JList showcasing JList, ListSelectionListener, and JViewport
        String[] usersArray = monitorService.getAllUsernames().toArray(new String[0]);
        listQuickUsers = new JList<>(usersArray);
        listQuickUsers.setBackground(CustomUITheme.PANEL_BG);
        listQuickUsers.setForeground(CustomUITheme.TEXT_PRIMARY);
        listQuickUsers.setFont(CustomUITheme.FONT_BODY);
        listQuickUsers.setSelectionBackground(new Color(38, 70, 110));
        listQuickUsers.setSelectionForeground(CustomUITheme.ACCENT_CYAN);
        listQuickUsers.setSelectedIndex(0);

        ListSelectionListener listSelectionListener = new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && listQuickUsers.getSelectedValue() != null) {
                    comboUser.setSelectedItem(listQuickUsers.getSelectedValue());
                }
            }
        };
        listQuickUsers.addListSelectionListener(listSelectionListener);

        JPanel quickListCard = CustomUITheme.createCardPanel("Quick Pick");
        quickListCard.setLayout(new BorderLayout());
        quickListCard.setPreferredSize(new Dimension(130, 0));
        JScrollPane listScroll = new JScrollPane(listQuickUsers);
        JViewport listViewport = listScroll.getViewport();
        listViewport.setBackground(CustomUITheme.PANEL_BG);
        listScroll.setBorder(BorderFactory.createLineBorder(CustomUITheme.BORDER_COLOR));
        quickListCard.add(listScroll, BorderLayout.CENTER);

        historyContainer.add(quickListCard, BorderLayout.EAST);

        // Explicit TableModel type demonstration
        TableModel explicitModel = historyTable.getModel();
        if (explicitModel.getColumnCount() > 0) {
            // Validated TableModel
        }

        contentPanel.add(historyContainer);

        add(contentPanel, BorderLayout.CENTER);

        onUserSelected();
    }

    private void onUserSelected() {
        String username = (String) comboUser.getSelectedItem();
        if (username == null) return;

        BehaviourProfile profile = monitorService.getProfile(username);
        Random r = new Random(username.hashCode());

        // Assign realistic category percentages
        updateCategory("CPU Usage", profile.getBaselineCpuPercent() + r.nextInt(15));
        updateCategory("Memory Usage", profile.getBaselineMemoryPercent() + r.nextInt(18));
        updateCategory("Network Activity", Math.min(95, 30 + r.nextInt(35)));
        updateCategory("File Access", Math.min(95, 20 + r.nextInt(40)));
        updateCategory("Process Activity", Math.min(95, 25 + r.nextInt(30)));
        updateCategory("Login Frequency", Math.min(95, 15 + r.nextInt(25)));
        updateCategory("USB Activity", r.nextInt(100) > 80 ? 75 : 10);
        updateCategory("Application Usage", 40 + r.nextInt(30));
        updateCategory("Session Duration", 50 + r.nextInt(40));

        refreshHistoryTable();
    }

    private void updateCategory(String name, int val) {
        val = Math.max(0, Math.min(100, val));
        JProgressBar bar = categoryBars.get(name);
        JLabel lbl = categoryLabels.get(name);

        if (bar != null && lbl != null) {
            bar.setValue(val);
            lbl.setText(val + "%");
            if (val >= 80) {
                bar.setForeground(CustomUITheme.CRITICAL_RED);
                lbl.setForeground(CustomUITheme.CRITICAL_RED);
            } else if (val >= 60) {
                bar.setForeground(CustomUITheme.WARNING_YELLOW);
                lbl.setForeground(CustomUITheme.WARNING_YELLOW);
            } else {
                bar.setForeground(CustomUITheme.ACCENT_CYAN);
                lbl.setForeground(CustomUITheme.ACCENT_CYAN);
            }
        }
    }

    private void refreshHistoryTable() {
        historyModel.setRowCount(0);
        String selected = (String) comboUser.getSelectedItem();
        List<BehaviourEvent> events = eventLogService.getAllEvents();

        int count = 0;
        for (BehaviourEvent e : events) {
            if (selected == null || e.getUsername().equalsIgnoreCase(selected) || count < 8) {
                historyModel.addRow(new Object[]{
                        e.getFormattedTime(),
                        e.getUsername(),
                        e.getMetricName(),
                        e.getObservedValue(),
                        e.getBaselineValue(),
                        e.getDeviation(),
                        e.getRiskLevel().getDisplayName()
                });
                count++;
                if (count >= 15) break;
            }
        }
    }

    private void registerListeners() {
        eventLogService.addChangeListener(() -> SwingUtilities.invokeLater(this::refreshHistoryTable));
    }
}
