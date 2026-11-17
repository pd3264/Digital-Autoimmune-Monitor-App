package digitalautomonitor.ui;

import digitalautomonitor.service.AlertService;
import digitalautomonitor.service.EventLogService;
import digitalautomonitor.service.ReportService;
import digitalautomonitor.ui.components.CustomUITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Security Report Generation and Export Console.
 *
 * Allows the user to:
 *   1. Select a report type.
 *   2. Set a date scope.
 *   3. Generate and preview the report as plain text.
 *   4. Export the report to a .txt file.
 */
public class ReportsPanel extends JPanel {

    private final ReportService reportService;
    private final EventLogService eventLogService;
    private final AlertService alertService;

    private JComboBox<String> comboReportType;
    private JFormattedTextField txtDateRange;
    private JTextArea txtReportPreview;

    // Stores the last generated report text so it can be exported
    private String currentReportText;

    public ReportsPanel(ReportService reportService, EventLogService eventLogService,
                        AlertService alertService) {
        this.reportService  = reportService;
        this.eventLogService = eventLogService;
        this.alertService   = alertService;

        setLayout(new BorderLayout(12, 12));
        setBackground(CustomUITheme.BG_DARK);
        setBorder(new EmptyBorder(14, 16, 14, 16));

        initUI();
        generateReport(); // Auto-generate a default report on open
    }

    private void initUI() {
        // ---------------------------------------------------------------
        //  TOP: Control bar — report type, date, and action buttons
        // ---------------------------------------------------------------
        JPanel topCard = CustomUITheme.createCardPanel();
        topCard.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 6));

        JLabel lblType = new JLabel("Report Type:");
        lblType.setFont(CustomUITheme.FONT_BODY_BOLD);
        lblType.setForeground(CustomUITheme.TEXT_PRIMARY);

        String[] types = {
                "Daily Security Report", "Behaviour Analysis Report",
                "Anomaly Summary", "Incident Report", "User Risk Report"
        };
        comboReportType = new JComboBox<>(types);
        comboReportType.setBackground(CustomUITheme.PANEL_BG_LIGHT);
        comboReportType.setForeground(CustomUITheme.TEXT_PRIMARY);
        comboReportType.setPreferredSize(new Dimension(200, 30));

        JLabel lblDate = new JLabel("Date Scope:");
        lblDate.setFont(CustomUITheme.FONT_BODY_BOLD);
        lblDate.setForeground(CustomUITheme.TEXT_PRIMARY);

        // Pre-fill with today's date in yyyy-MM-dd format
        txtDateRange = new JFormattedTextField(
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        );
        txtDateRange.setBackground(CustomUITheme.PANEL_BG_LIGHT);
        txtDateRange.setForeground(CustomUITheme.TEXT_PRIMARY);
        txtDateRange.setCaretColor(CustomUITheme.ACCENT_CYAN);
        txtDateRange.setFont(CustomUITheme.FONT_BODY);
        txtDateRange.setPreferredSize(new Dimension(120, 30));

        JButton btnGenerate = CustomUITheme.createStyledButton("Generate Report", CustomUITheme.ACCENT_CYAN);
        JButton btnExport   = CustomUITheme.createStyledButton("Export TXT",      CustomUITheme.HEALTHY_GREEN);

        btnGenerate.addActionListener(e -> generateReport());
        btnExport.addActionListener(e -> exportReport());

        topCard.add(lblType);
        topCard.add(comboReportType);
        topCard.add(lblDate);
        topCard.add(txtDateRange);
        topCard.add(btnGenerate);
        topCard.add(btnExport);

        add(topCard, BorderLayout.NORTH);

        // ---------------------------------------------------------------
        //  CENTER: Report preview in a scrollable JTextArea
        // ---------------------------------------------------------------
        JPanel previewContainer = CustomUITheme.createCardPanel("Security Audit Report Preview");
        previewContainer.setLayout(new BorderLayout());

        txtReportPreview = new JTextArea();
        txtReportPreview.setEditable(false);
        txtReportPreview.setBackground(CustomUITheme.PANEL_BG_LIGHT);
        txtReportPreview.setForeground(CustomUITheme.TEXT_PRIMARY);
        txtReportPreview.setFont(CustomUITheme.FONT_MONO);
        txtReportPreview.setBorder(new EmptyBorder(14, 16, 14, 16));

        JScrollPane scroll = new JScrollPane(txtReportPreview);
        scroll.setBorder(new LineBorder(CustomUITheme.BORDER_COLOR, 1));
        previewContainer.add(scroll, BorderLayout.CENTER);

        add(previewContainer, BorderLayout.CENTER);
    }

    /** Generates a new report and displays it in the preview area. */
    private void generateReport() {
        String type = (String) comboReportType.getSelectedItem();
        LocalDate date = LocalDate.now();
        try {
            date = LocalDate.parse(txtDateRange.getText().trim());
        } catch (Exception ignored) {
            // If the user typed an invalid date, just use today
        }

        // Generate the report text using the service
        currentReportText = reportService.generateReport(
                type, date,
                eventLogService.getAllEvents(),
                alertService.getAllAlerts()
        );

        txtReportPreview.setText(currentReportText);
        txtReportPreview.setCaretPosition(0); // Scroll back to top
    }

    /** Opens a save dialog and exports the report to a .txt file. */
    private void exportReport() {
        if (currentReportText == null || currentReportText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please generate a report first.",
                    "No Report", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Export Security Report as Text");

        // Suggest a sensible default filename
        String type = (String) comboReportType.getSelectedItem();
        String filename = (type != null ? type.toLowerCase().replace(" ", "_") : "report")
                + "_" + System.currentTimeMillis() + ".txt";
        chooser.setSelectedFile(new File(filename));

        int res = chooser.showSaveDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            File target = chooser.getSelectedFile();
            try {
                reportService.exportReport(currentReportText, target);
                JOptionPane.showMessageDialog(this,
                        "Report successfully saved to:\n" + target.getAbsolutePath(),
                        "Export Complete", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Failed to export report: " + ex.getMessage(),
                        "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
