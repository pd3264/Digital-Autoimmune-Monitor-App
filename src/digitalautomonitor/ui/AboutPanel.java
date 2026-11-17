++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++package digitalautomonitor.ui;

import digitalautomonitor.ui.components.CustomUITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * About and Conceptual Architecture Panel highlighting the biological immune
 * metaphor and cybersecurity simulation model.
 */
public class AboutPanel extends JPanel {

    public AboutPanel() {
        setLayout(new BorderLayout(12, 12));
        setBackground(CustomUITheme.BG_DARK);
        setBorder(new EmptyBorder(14, 16, 14, 16));

        initUI();
    }

    private void initUI() {
        // TOP HEADER
        JPanel topCard = CustomUITheme.createCardPanel();
        topCard.setLayout(new BorderLayout());

        JLabel title = new JLabel("DIGITAL AUTOIMMUNE MONITOR | ARCHITECTURE & METAPHOR");
        title.setFont(CustomUITheme.FONT_TITLE);
        title.setForeground(CustomUITheme.TEXT_PRIMARY);

        JLabel version = new JLabel("v2.5 Enterprise SOC Edition | Java 17+ Pure Swing");
        version.setFont(CustomUITheme.FONT_SUBTITLE);
        version.setForeground(CustomUITheme.ACCENT_CYAN);

        topCard.add(title, BorderLayout.WEST);
        topCard.add(version, BorderLayout.EAST);
        add(topCard, BorderLayout.NORTH);

        // CENTER: JEditorPane showing styled HTML documentation & comparison table
        JPanel contentCard = CustomUITheme.createCardPanel("Biological Immune System vs Digital Security Mapping");
        contentCard.setLayout(new BorderLayout());

        JEditorPane editorPane = new JEditorPane();
        editorPane.setContentType("text/html");
        editorPane.setEditable(false);
        editorPane.setBackground(CustomUITheme.PANEL_BG_LIGHT);

        String html = "<html><body style='font-family: Segoe UI, sans-serif; background-color: #1E293B; color: #F8FAFC; padding: 20px; line-height: 1.5;'>" +
                "<h2 style='color: #00F0FF; margin-top: 0;'>Application Concept & Biological Philosophy</h2>" +
                "<p style='color: #94A3B8; font-size: 13px;'>" +
                "The <b>Digital Autoimmune Monitor</b> operates as an artificial biological defense mechanism for computer systems. " +
                "Rather than relying strictly on static blacklists, it maintains continuous surveillance over system and user behaviour, " +
                "contrasting real-time telemetry against a learned homeostatic baseline." +
                "</p>" +

                "<table style='width: 100%; border-collapse: collapse; margin-top: 15px; margin-bottom: 20px; font-size: 13px;'>" +
                "<tr style='background-color: #0F172A; color: #00F0FF;'>" +
                "<th style='padding: 10px; border: 1px solid #27334A; text-align: left;'>Human Immune System</th>" +
                "<th style='padding: 10px; border: 1px solid #27334A; text-align: left;'>Digital Autoimmune Monitor</th>" +
                "<th style='padding: 10px; border: 1px solid #27334A; text-align: left;'>Operational Role</th>" +
                "</tr>" +

                "<tr>" +
                "<td style='padding: 8px; border: 1px solid #27334A; color: #38BDF8;'><b>Immune system</b></td>" +
                "<td style='padding: 8px; border: 1px solid #27334A;'>Security monitoring engine</td>" +
                "<td style='padding: 8px; border: 1px solid #27334A; color: #94A3B8;'>Central autonomous orchestrator and telemetry watchdog</td>" +
                "</tr>" +

                "<tr style='background-color: #162032;'>" +
                "<td style='padding: 8px; border: 1px solid #27334A; color: #10B981;'><b>Normal cells</b></td>" +
                "<td style='padding: 8px; border: 1px solid #27334A;'>Normal system behaviour</td>" +
                "<td style='padding: 8px; border: 1px solid #27334A; color: #94A3B8;'>Healthy day-to-day operations conforming to baseline</td>" +
                "</tr>" +

                "<tr>" +
                "<td style='padding: 8px; border: 1px solid #27334A; color: #00F0FF;'><b>Antibodies</b></td>" +
                "<td style='padding: 8px; border: 1px solid #27334A;'>Detection rules</td>" +
                "<td style='padding: 8px; border: 1px solid #27334A; color: #94A3B8;'>Specialized heuristic evaluators inspecting specific vectors</td>" +
                "</tr>" +

                "<tr style='background-color: #162032;'>" +
                "<td style='padding: 8px; border: 1px solid #27334A; color: #F59E0B;'><b>Antigens</b></td>" +
                "<td style='padding: 8px; border: 1px solid #27334A;'>Suspicious events</td>" +
                "<td style='padding: 8px; border: 1px solid #27334A; color: #94A3B8;'>Observed behavioural telemetry flagged for inspection</td>" +
                "</tr>" +

                "<tr>" +
                "<td style='padding: 8px; border: 1px solid #27334A; color: #F97316;'><b>Inflammation</b></td>" +
                "<td style='padding: 8px; border: 1px solid #27334A;'>Security alert</td>" +
                "<td style='padding: 8px; border: 1px solid #27334A; color: #94A3B8;'>Early warning response attracting SOC analyst intervention</td>" +
                "</tr>" +

                "<tr style='background-color: #162032;'>" +
                "<td style='padding: 8px; border: 1px solid #27334A; color: #38BDF8;'><b>Immune memory</b></td>" +
                "<td style='padding: 8px; border: 1px solid #27334A;'>Historical behaviour profiles</td>" +
                "<td style='padding: 8px; border: 1px solid #27334A; color: #94A3B8;'>Long-term learned tolerances and baseline metrics repository</td>" +
                "</tr>" +

                "<tr>" +
                "<td style='padding: 8px; border: 1px solid #27334A; color: #EF4444;'><b>Pathogen</b></td>" +
                "<td style='padding: 8px; border: 1px solid #27334A;'>Anomalous activity</td>" +
                "<td style='padding: 8px; border: 1px solid #27334A; color: #94A3B8;'>Severe deviation, malware execution, or credential theft</td>" +
                "</tr>" +

                "<tr style='background-color: #162032;'>" +
                "<td style='padding: 8px; border: 1px solid #27334A; color: #F59E0B;'><b>Immune response</b></td>" +
                "<td style='padding: 8px; border: 1px solid #27334A;'>Security response</td>" +
                "<td style='padding: 8px; border: 1px solid #27334A; color: #94A3B8;'>Session isolation, process suspension, credential quarantine</td>" +
                "</tr>" +

                "<tr>" +
                "<td style='padding: 8px; border: 1px solid #27334A; color: #10B981;'><b>Recovery</b></td>" +
                "<td style='padding: 8px; border: 1px solid #27334A;'>Incident resolution</td>" +
                "<td style='padding: 8px; border: 1px solid #27334A; color: #94A3B8;'>Tissue healing, baseline updating, and return to homeostasis</td>" +
                "</tr>" +
                "</table>" +

                "<h3 style='color: #10B981;'>Academic Cybersecurity Simulation Safety Notice</h3>" +
                "<p style='color: #94A3B8; font-size: 12px;'>" +
                "This application is an educational and forensic research simulation designed to model behavioural anomaly detection. " +
                "All events, threats, and health metrics are generated safely within memory. The application does <b>not</b> perform " +
                "harmful actions, execute malicious payloads, exploit network services, steal credentials, or compromise operating system integrity." +
                "</p>" +

                "<h3 style='color: #00F0FF;'>Technology Stack & Architecture</h3>" +
                "<ul style='color: #94A3B8; font-size: 12px;'>" +
                "<li><b>Runtime:</b> Pure Java 17+ (Zero external UI or charting dependencies)</li>" +
                "<li><b>GUI Framework:</b> Java Swing (JFrame, JSplitPane, JDesktopPane, JInternalFrame, JTree, JTable, JProgressBar, JSlider, JSpinner, JTextPane, JEditorPane, JFileChooser, JColorChooser)</li>" +
                "<li><b>Rendering:</b> Native Java2D (Graphics2D) custom-painted charts, gauges, LED indicators, and timeline pipelines</li>" +
                "<li><b>Patterns:</b> Polymorphic Rule Strategy, Observer Listeners, Service Separation, MVC Event-Driven EDT Scheduling</li>" +
                "</ul>" +

                "</body></html>";

        editorPane.setText(html);
        editorPane.setCaretPosition(0);

        JScrollPane scroll = new JScrollPane(editorPane);
        scroll.setBorder(new LineBorder(CustomUITheme.BORDER_COLOR, 1));
        contentCard.add(scroll, BorderLayout.CENTER);

        add(contentCard, BorderLayout.CENTER);
    }
}
