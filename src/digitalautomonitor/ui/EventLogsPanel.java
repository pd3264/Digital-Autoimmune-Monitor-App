package digitalautomonitor.ui;

import digitalautomonitor.enums.EventType;
import digitalautomonitor.enums.RiskLevel;
import digitalautomonitor.model.BehaviourEvent;
import digitalautomonitor.service.AlertService;
import digitalautomonitor.service.EventLogService;
import digitalautomonitor.ui.components.CustomUITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Forensic Event Log Repository featuring JTree category filters,
 * dynamic RowFilter/TableRowSorter search, JPopupMenu actions, and JFileChooser exports.
 */
public class EventLogsPanel extends JPanel {

    private final EventLogService eventLogService;
    private final AlertService alertService;

    private DefaultTableModel tableModel;
    private JTable eventTable;
    private TableRowSorter<DefaultTableModel> rowSorter;

    private JTextField txtSearch;
    private JComboBox<String> comboTypeFilter;
    private JComboBox<String> comboRiskFilter;
    private JTree categoryTree;
    private JLabel lblFilteredCount;

    public EventLogsPanel(EventLogService eventLogService, AlertService alertService) {
        this.eventLogService = eventLogService;
        this.alertService = alertService;

        setLayout(new BorderLayout(10, 10));
        setBackground(CustomUITheme.BG_DARK);
        setBorder(new EmptyBorder(14, 16, 14, 16));

        initUI();
        registerListeners();
    }

    private void initUI() {
        // TOP FILTER BAR
        JPanel topFilterCard = CustomUITheme.createCardPanel();
        topFilterCard.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 6));

        JLabel lblSearch = new JLabel("Search:");
        lblSearch.setFont(CustomUITheme.FONT_BODY_BOLD);
        lblSearch.setForeground(CustomUITheme.TEXT_PRIMARY);

        txtSearch = new JTextField(12);
        txtSearch.setBackground(CustomUITheme.PANEL_BG_LIGHT);
        txtSearch.setForeground(CustomUITheme.TEXT_PRIMARY);
        txtSearch.setCaretColor(CustomUITheme.ACCENT_CYAN);
        txtSearch.setFont(CustomUITheme.FONT_BODY);

        JLabel lblType = new JLabel("Type:");
        lblType.setFont(CustomUITheme.FONT_BODY_BOLD);
        lblType.setForeground(CustomUITheme.TEXT_PRIMARY);

        comboTypeFilter = new JComboBox<>(new String[]{
                "All Categories", "Authentication", "File Access", "Process Activity",
                "Network Activity", "USB Activity", "Application Usage", "System Event"
        });
        comboTypeFilter.setBackground(CustomUITheme.PANEL_BG_LIGHT);
        comboTypeFilter.setForeground(CustomUITheme.TEXT_PRIMARY);

        JLabel lblRisk = new JLabel("Risk:");
        lblRisk.setFont(CustomUITheme.FONT_BODY_BOLD);
        lblRisk.setForeground(CustomUITheme.TEXT_PRIMARY);

        comboRiskFilter = new JComboBox<>(new String[]{
                "All Severities", "Normal", "Low Risk", "Medium Risk", "High Risk", "Critical"
        });
        comboRiskFilter.setBackground(CustomUITheme.PANEL_BG_LIGHT);
        comboRiskFilter.setForeground(CustomUITheme.TEXT_PRIMARY);

        JButton btnClear = CustomUITheme.createStyledButton("Clear Filters", CustomUITheme.BORDER_COLOR);
        JButton btnExport = CustomUITheme.createStyledButton("Export Logs", CustomUITheme.ACCENT_CYAN);
        JButton btnRefresh = CustomUITheme.createStyledButton("Refresh", CustomUITheme.BORDER_COLOR);

        lblFilteredCount = new JLabel();
        lblFilteredCount.setFont(CustomUITheme.FONT_SMALL);
        lblFilteredCount.setForeground(CustomUITheme.ACCENT_CYAN);

        // Filter event triggers
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { applyFilters(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { applyFilters(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { applyFilters(); }
        });

        comboTypeFilter.addActionListener(e -> applyFilters());
        comboRiskFilter.addActionListener(e -> applyFilters());

        btnClear.addActionListener(e -> {
            txtSearch.setText("");
            comboTypeFilter.setSelectedIndex(0);
            comboRiskFilter.setSelectedIndex(0);
            categoryTree.clearSelection();
            applyFilters();
        });

        btnExport.addActionListener(e -> exportLogsToFile());
        btnRefresh.addActionListener(e -> refreshTableData());

        topFilterCard.add(lblSearch);
        topFilterCard.add(txtSearch);
        topFilterCard.add(lblType);
        topFilterCard.add(comboTypeFilter);
        topFilterCard.add(lblRisk);
        topFilterCard.add(comboRiskFilter);
        topFilterCard.add(btnClear);
        topFilterCard.add(btnExport);
        topFilterCard.add(btnRefresh);
        topFilterCard.add(lblFilteredCount);

        add(topFilterCard, BorderLayout.NORTH);

        // CENTER: JSplitPane (Left: JTree Categories, Right: JTable)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(230);
        splitPane.setBorder(null);
        splitPane.setOpaque(false);

        // LEFT: JTree for Security Event Categories (Section 22)
        JPanel treeContainer = CustomUITheme.createCardPanel("Event Categories");
        treeContainer.setLayout(new BorderLayout());

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Security Events");
        root.add(new DefaultMutableTreeNode("Authentication"));
        root.add(new DefaultMutableTreeNode("File Access"));
        root.add(new DefaultMutableTreeNode("Process Activity"));
        root.add(new DefaultMutableTreeNode("Network Activity"));
        root.add(new DefaultMutableTreeNode("USB Activity"));
        root.add(new DefaultMutableTreeNode("Application Usage"));
        root.add(new DefaultMutableTreeNode("System Event"));

        categoryTree = new JTree(new DefaultTreeModel(root));
        categoryTree.setBackground(CustomUITheme.PANEL_BG);
        categoryTree.setForeground(CustomUITheme.TEXT_PRIMARY);
        categoryTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        DefaultTreeCellRenderer treeRenderer = new DefaultTreeCellRenderer();
        treeRenderer.setBackgroundNonSelectionColor(CustomUITheme.PANEL_BG);
        treeRenderer.setBackgroundSelectionColor(CustomUITheme.PANEL_BG_LIGHT);
        treeRenderer.setTextNonSelectionColor(CustomUITheme.TEXT_PRIMARY);
        treeRenderer.setTextSelectionColor(CustomUITheme.ACCENT_CYAN);
        categoryTree.setCellRenderer(treeRenderer);

        categoryTree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode selected = (DefaultMutableTreeNode) categoryTree.getLastSelectedPathComponent();
            if (selected != null && !selected.isRoot()) {
                String cat = selected.getUserObject().toString();
                comboTypeFilter.setSelectedItem(cat);
            } else {
                comboTypeFilter.setSelectedIndex(0);
            }
        });

        JScrollPane treeScroll = new JScrollPane(categoryTree);
        treeScroll.setBorder(new LineBorder(CustomUITheme.BORDER_COLOR, 1));
        treeScroll.getViewport().setBackground(CustomUITheme.PANEL_BG);
        treeContainer.add(treeScroll, BorderLayout.CENTER);

        splitPane.setLeftComponent(treeContainer);

        // RIGHT: Large Events JTable (Section 13)
        JPanel tableContainer = CustomUITheme.createCardPanel("Forensic Antigen Telemetry Log");
        tableContainer.setLayout(new BorderLayout(0, 6));

        String[] cols = {"Event ID", "Timestamp", "User", "IP Address", "Event Type", "Description", "Risk Level", "Action Taken"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        rowSorter = new TableRowSorter<>(tableModel);
        eventTable = new JTable(tableModel);
        eventTable.setRowSorter(rowSorter);
        CustomUITheme.styleTable(eventTable);

        // Severity renderer
        eventTable.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                l.setFont(CustomUITheme.FONT_BODY_BOLD);
                String val = String.valueOf(value);
                if ("CRITICAL".equalsIgnoreCase(val)) l.setForeground(CustomUITheme.CRITICAL_RED);
                else if ("HIGH RISK".equalsIgnoreCase(val) || "HIGH".equalsIgnoreCase(val)) l.setForeground(CustomUITheme.WARNING_ORANGE);
                else if ("MEDIUM RISK".equalsIgnoreCase(val) || "MEDIUM".equalsIgnoreCase(val)) l.setForeground(CustomUITheme.WARNING_YELLOW);
                else if ("LOW RISK".equalsIgnoreCase(val) || "LOW".equalsIgnoreCase(val)) l.setForeground(CustomUITheme.ACCENT_BLUE);
                else l.setForeground(CustomUITheme.HEALTHY_GREEN);
                return l;
            }
        });

        // JPopupMenu on right click (Section 21)
        setupContextMenu();

        JScrollPane tableScroll = new JScrollPane(eventTable);
        tableScroll.setBorder(new LineBorder(CustomUITheme.BORDER_COLOR, 1));
        tableScroll.getViewport().setBackground(CustomUITheme.PANEL_BG);
        tableContainer.add(tableScroll, BorderLayout.CENTER);

        splitPane.setRightComponent(tableContainer);
        add(splitPane, BorderLayout.CENTER);

        refreshTableData();
    }

    private void setupContextMenu() {
        JPopupMenu popup = new JPopupMenu();
        popup.setBackground(CustomUITheme.PANEL_BG);
        popup.setBorder(new LineBorder(CustomUITheme.BORDER_COLOR, 1));

        JMenuItem itemDetails = new JMenuItem("View Details");
        JMenuItem itemInvestigate = new JMenuItem("Investigate Anomaly");
        JMenuItem itemAck = new JMenuItem("Acknowledge Antigen");
        JMenuItem itemResolved = new JMenuItem("Mark Resolved");
        JMenuItem itemCopyId = new JMenuItem("Copy Event ID");

        styleMenuItem(itemDetails);
        styleMenuItem(itemInvestigate);
        styleMenuItem(itemAck);
        styleMenuItem(itemResolved);
        styleMenuItem(itemCopyId);

        itemDetails.addActionListener(e -> showSelectedEventDetails());
        itemInvestigate.addActionListener(e -> investigateSelectedEvent());
        itemAck.addActionListener(e -> acknowledgeSelectedEvent());
        itemResolved.addActionListener(e -> markSelectedResolved());
        itemCopyId.addActionListener(e -> copySelectedEventId());

        popup.add(itemDetails);
        popup.add(itemInvestigate);
        popup.add(itemAck);
        popup.add(new JSeparator(SwingConstants.HORIZONTAL));
        popup.add(itemCopyId);

        java.awt.event.MouseListener mouseListener = new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopup(e);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopup(e);
                }
            }

            private void showPopup(MouseEvent e) {
                int row = eventTable.rowAtPoint(e.getPoint());
                if (row >= 0 && row < eventTable.getRowCount()) {
                    eventTable.setRowSelectionInterval(row, row);
                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        };
        eventTable.addMouseListener(mouseListener);
    }

    private void styleMenuItem(JMenuItem item) {
        item.setBackground(CustomUITheme.PANEL_BG);
        item.setForeground(CustomUITheme.TEXT_PRIMARY);
        item.setFont(CustomUITheme.FONT_BODY);
    }

    private void showSelectedEventDetails() {
        int row = eventTable.getSelectedRow();
        if (row < 0) return;
        int modelRow = eventTable.convertRowIndexToModel(row);
        String id = (String) tableModel.getValueAt(modelRow, 0);
        String time = (String) tableModel.getValueAt(modelRow, 1);
        String user = (String) tableModel.getValueAt(modelRow, 2);
        String ip = (String) tableModel.getValueAt(modelRow, 3);
        String type = (String) tableModel.getValueAt(modelRow, 4);
        String desc = (String) tableModel.getValueAt(modelRow, 5);
        String risk = (String) tableModel.getValueAt(modelRow, 6);
        String action = (String) tableModel.getValueAt(modelRow, 7);

        String msg = String.format("EVENT AUDIT RECORD\n\n" +
                "ID:          %s\n" +
                "Timestamp:   %s\n" +
                "User:        %s\n" +
                "IP Address:  %s\n" +
                "Category:    %s\n" +
                "Description: %s\n" +
                "Risk Tier:   %s\n" +
                "Action:      %s", id, time, user, ip, type, desc, risk, action);

        JOptionPane.showMessageDialog(this, msg, "Event Diagnostic | " + id, JOptionPane.INFORMATION_MESSAGE);
    }

    private void investigateSelectedEvent() {
        int row = eventTable.getSelectedRow();
        if (row < 0) return;
        int modelRow = eventTable.convertRowIndexToModel(row);
        String id = (String) tableModel.getValueAt(modelRow, 0);
        String user = (String) tableModel.getValueAt(modelRow, 2);
        JOptionPane.showMessageDialog(this, "Session for entity '" + user + "' flagged for deep packet tracing.",
                "Investigation Spawned", JOptionPane.INFORMATION_MESSAGE);
    }

    private void acknowledgeSelectedEvent() {
        int row = eventTable.getSelectedRow();
        if (row < 0) return;
        int modelRow = eventTable.convertRowIndexToModel(row);
        String id = (String) tableModel.getValueAt(modelRow, 0);
        tableModel.setValueAt("Acknowledged", modelRow, 7);
        JOptionPane.showMessageDialog(this, "Event " + id + " acknowledged by SecOps analyst.", "Acknowledged", JOptionPane.INFORMATION_MESSAGE);
    }

    private void markSelectedResolved() {
        int row = eventTable.getSelectedRow();
        if (row < 0) return;
        int modelRow = eventTable.convertRowIndexToModel(row);
        String id = (String) tableModel.getValueAt(modelRow, 0);
        tableModel.setValueAt("Resolved", modelRow, 7);
        JOptionPane.showMessageDialog(this, "Event " + id + " marked as Resolved.", "Resolved", JOptionPane.INFORMATION_MESSAGE);
    }

    private void copySelectedEventId() {
        int row = eventTable.getSelectedRow();
        if (row < 0) return;
        int modelRow = eventTable.convertRowIndexToModel(row);
        String id = (String) tableModel.getValueAt(modelRow, 0);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(id), null);
        JOptionPane.showMessageDialog(this, "Event ID copied to clipboard: " + id, "Copied", JOptionPane.INFORMATION_MESSAGE);
    }

    private void applyFilters() {
        List<RowFilter<DefaultTableModel, Object>> filters = new ArrayList<>();

        // Text search
        String text = txtSearch.getText().trim();
        if (!text.isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + text));
        }

        // Category filter
        String selectedType = (String) comboTypeFilter.getSelectedItem();
        if (selectedType != null && !selectedType.equals("All Categories")) {
            filters.add(RowFilter.regexFilter("(?i)" + selectedType, 4));
        }

        // Risk filter
        String selectedRisk = (String) comboRiskFilter.getSelectedItem();
        if (selectedRisk != null && !selectedRisk.equals("All Severities")) {
            filters.add(RowFilter.regexFilter("(?i)" + selectedRisk, 6));
        }

        if (filters.isEmpty()) {
            rowSorter.setRowFilter(null);
        } else {
            rowSorter.setRowFilter(RowFilter.andFilter(filters));
        }

        lblFilteredCount.setText("Showing: " + eventTable.getRowCount() + " of " + tableModel.getRowCount());
    }

    private void exportLogsToFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Export Behaviour Event Audit Logs");
        chooser.setSelectedFile(new File("digital_autoimmune_events_" + System.currentTimeMillis() + ".csv"));

        int userSelection = chooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = chooser.getSelectedFile();
            try {
                if (fileToSave.getName().toLowerCase().endsWith(".txt")) {
                    eventLogService.exportToTxt(fileToSave);
                } else {
                    eventLogService.exportToCsv(fileToSave);
                }
                JOptionPane.showMessageDialog(this, "Successfully exported logs to:\n" + fileToSave.getAbsolutePath(),
                        "Export Successful", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error exporting logs: " + ex.getMessage(),
                        "Export Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void refreshTableData() {
        tableModel.setRowCount(0);
        List<BehaviourEvent> events = eventLogService.getAllEvents();
        for (BehaviourEvent e : events) {
            tableModel.addRow(new Object[]{
                    e.getEventId(),
                    e.getFormattedTime(),
                    e.getUsername(),
                    e.getIpAddress(),
                    e.getEventType().getCategoryName(),
                    e.getDescription(),
                    e.getRiskLevel().getDisplayName(),
                    e.getActionTaken()
            });
        }
        applyFilters();
    }

    private void registerListeners() {
        eventLogService.addChangeListener(() -> SwingUtilities.invokeLater(this::refreshTableData));
    }
}
