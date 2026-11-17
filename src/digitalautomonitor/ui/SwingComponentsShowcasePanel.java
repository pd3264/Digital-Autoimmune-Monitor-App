package digitalautomonitor.ui;

import digitalautomonitor.enums.RiskLevel;
import digitalautomonitor.service.SimulationService;
import digitalautomonitor.ui.components.CustomUITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Comprehensive Java Swing Components Showcase and Interactive Gallery.
 * Demonstrates the full range of standard javax.swing.* widgets, AWT graphics,
 * layout managers, containers, dialog pickers, and asynchronous SwingWorker concurrency.
 */
public class SwingComponentsShowcasePanel extends JPanel {

    private final SimulationService simulationService;

    // Live Event Inspector
    private JTextArea txtEventInspector;
    private int eventCount = 0;
    private JLabel lblEventCount;

    // Background Concurrency
    // Uses an inline SwingWorker (replaces the deleted AsyncScanWorker component)
    private SwingWorker<Void, String> activeWorker;
    private JProgressBar workerProgress;
    private JLabel lblWorkerStatus;
    private JButton btnStartWorker;
    private JButton btnCancelWorker;
    private DefaultListModel<String> workerListModel;

    public SwingComponentsShowcasePanel(SimulationService simulationService) {
        this.simulationService = simulationService;

        setLayout(new BorderLayout(10, 10));
        setBackground(CustomUITheme.BG_DARK);
        setBorder(new EmptyBorder(12, 14, 12, 14));

        initUI();
    }

    private void initUI() {
        // TOP HEADER
        JPanel topHeader = CustomUITheme.createCardPanel();
        topHeader.setLayout(new BorderLayout(10, 0));

        JPanel leftTitle = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftTitle.setOpaque(false);

        JLabel icon = new JLabel("⚡");
        icon.setFont(new Font("Segoe UI", Font.BOLD, 22));
        icon.setForeground(CustomUITheme.ACCENT_CYAN);

        JLabel title = new JLabel("JAVA SWING COMPONENT SHOWCASE & INTERACTIVE GALLERY");
        title.setFont(CustomUITheme.FONT_TITLE);
        title.setForeground(CustomUITheme.TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Live javax.swing.* controls, AWT Java2D renderers, dialog utilities, and concurrency");
        subtitle.setFont(CustomUITheme.FONT_SMALL);
        subtitle.setForeground(CustomUITheme.TEXT_MUTED);

        JPanel titleTextPanel = new JPanel(new GridLayout(2, 1));
        titleTextPanel.setOpaque(false);
        titleTextPanel.add(title);
        titleTextPanel.add(subtitle);

        leftTitle.add(icon);
        leftTitle.add(titleTextPanel);
        topHeader.add(leftTitle, BorderLayout.WEST);

        // Header Action: Quick Dialog Triggers
        JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightActions.setOpaque(false);

        JButton btnQuickOption = CustomUITheme.createStyledButton("JOptionPane Demo", CustomUITheme.ACCENT_CYAN);
        JButton btnQuickColor = CustomUITheme.createStyledButton("JColorChooser", CustomUITheme.HEALTHY_GREEN);
        JButton btnQuickFile = CustomUITheme.createStyledButton("JFileChooser", CustomUITheme.WARNING_YELLOW);

        btnQuickOption.addActionListener(e -> {
            logEvent("JOptionPane", "Triggered showConfirmDialog modal");
            int res = JOptionPane.showConfirmDialog(this,
                    "Verify Java Swing component catalog integrity?\nAll widgets are rendered using pure Java Foundation Classes (JFC).",
                    "Swing Component Verification", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            logEvent("JOptionPane", "User response code: " + res);
        });

        btnQuickColor.addActionListener(e -> {
            logEvent("JColorChooser", "Opened color picker");
            Color chosen = JColorChooser.showDialog(this, "Select Cyber Accent Color", CustomUITheme.ACCENT_CYAN);
            if (chosen != null) {
                logEvent("JColorChooser", String.format("Color chosen: RGB(%d, %d, %d)", chosen.getRed(), chosen.getGreen(), chosen.getBlue()));
            }
        });

        btnQuickFile.addActionListener(e -> {
            logEvent("JFileChooser", "Opened file dialog");
            JFileChooser fc = new JFileChooser();
            int res = fc.showOpenDialog(this);
            if (res == JFileChooser.APPROVE_OPTION) {
                logEvent("JFileChooser", "Selected file: " + fc.getSelectedFile().getName());
            }
        });

        rightActions.add(btnQuickOption);
        rightActions.add(btnQuickColor);
        rightActions.add(btnQuickFile);
        topHeader.add(rightActions, BorderLayout.EAST);

        add(topHeader, BorderLayout.NORTH);

        // CENTER: JTabbedPane showcasing all component families
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setBackground(CustomUITheme.PANEL_BG);
        tabbedPane.setForeground(CustomUITheme.ACCENT_CYAN);
        tabbedPane.setFont(CustomUITheme.FONT_BODY_BOLD);

        tabbedPane.addTab("Buttons & Selectors", createButtonsTab());
        tabbedPane.addTab("Sliders & Spinners", createSlidersSpinnersTab());
        tabbedPane.addTab("Text & Rich Documents", createTextComponentsTab());
        tabbedPane.addTab("Tables, Trees & Lists", createDataComponentsTab());
        tabbedPane.addTab("Containers & Menus", createContainersTab());
        tabbedPane.addTab("Dialogs & Choosers", createDialogsTab());
        tabbedPane.addTab("SwingWorker Concurrency", createConcurrencyTab());
        tabbedPane.addTab("Custom Java2D Widgets", createCustomWidgetsTab());

        tabbedPane.addChangeListener(e -> {
            int idx = tabbedPane.getSelectedIndex();
            logEvent("JTabbedPane", "Switched to tab [" + idx + "]: " + tabbedPane.getTitleAt(idx));
        });

        // Split center between tabbed pane and bottom Event Inspector
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(tabbedPane);
        splitPane.setBottomComponent(createEventInspectorPanel());
        splitPane.setDividerLocation(520);
        splitPane.setResizeWeight(0.78);
        splitPane.setBorder(null);

        add(splitPane, BorderLayout.CENTER);
    }

    // =========================================================================
    // TAB 1: BUTTONS & SELECTORS
    // =========================================================================
    private JPanel createButtonsTab() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 12, 12));
        panel.setBackground(CustomUITheme.BG_DARK);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Card 1: JButtons
        JPanel cardButtons = CustomUITheme.createCardPanel("JButton (Standard, Neon, Danger, Disabled)");
        cardButtons.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));

        JButton btnPrimary = CustomUITheme.createStyledButton("Primary Action", CustomUITheme.ACCENT_CYAN);
        JButton btnSuccess = CustomUITheme.createStyledButton("Success State", CustomUITheme.HEALTHY_GREEN);
        JButton btnWarning = CustomUITheme.createStyledButton("Warning Trigger", CustomUITheme.WARNING_YELLOW);
        JButton btnDanger = CustomUITheme.createStyledButton("Critical Isolate", CustomUITheme.CRITICAL_RED);
        JButton btnDisabled = new JButton("Disabled Button");
        btnDisabled.setEnabled(false);

        btnPrimary.addActionListener(e -> logEvent("JButton", "Primary Action clicked"));
        btnSuccess.addActionListener(e -> logEvent("JButton", "Success State clicked"));
        btnWarning.addActionListener(e -> logEvent("JButton", "Warning Trigger clicked"));
        btnDanger.addActionListener(e -> logEvent("JButton", "Critical Isolate clicked"));

        cardButtons.add(btnPrimary);
        cardButtons.add(btnSuccess);
        cardButtons.add(btnWarning);
        cardButtons.add(btnDanger);
        cardButtons.add(btnDisabled);
        panel.add(cardButtons);

        // Card 2: JToggleButtons
        JPanel cardToggle = CustomUITheme.createCardPanel("JToggleButton (Stateful Binary Switches)");
        cardToggle.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));

        JToggleButton toggleShield = new JToggleButton("Shield: ACTIVE", true);
        styleToggle(toggleShield, CustomUITheme.HEALTHY_GREEN);
        toggleShield.addActionListener(e -> {
            boolean sel = toggleShield.isSelected();
            toggleShield.setText(sel ? "Shield: ACTIVE" : "Shield: DISABLED");
            toggleShield.setBackground(sel ? CustomUITheme.HEALTHY_GREEN : CustomUITheme.PANEL_BG_LIGHT);
            logEvent("JToggleButton", "Shield toggled: " + sel);
        });

        JToggleButton toggleStealth = new JToggleButton("Stealth Mode: OFF", false);
        styleToggle(toggleStealth, CustomUITheme.ACCENT_CYAN);
        toggleStealth.addActionListener(e -> {
            boolean sel = toggleStealth.isSelected();
            toggleStealth.setText(sel ? "Stealth Mode: ON" : "Stealth Mode: OFF");
            toggleStealth.setBackground(sel ? CustomUITheme.ACCENT_CYAN : CustomUITheme.PANEL_BG_LIGHT);
            logEvent("JToggleButton", "Stealth Mode toggled: " + sel);
        });

        cardToggle.add(toggleShield);
        cardToggle.add(toggleStealth);
        panel.add(cardToggle);

        // Card 3: JCheckBoxes & JRadioButtons
        JPanel cardChecks = CustomUITheme.createCardPanel("JCheckBox & JRadioButton (ButtonGroup)");
        cardChecks.setLayout(new GridLayout(2, 2, 6, 6));

        JCheckBox chkDeep = new JCheckBox("Deep Packet Inspection", true);
        JCheckBox chkHeur = new JCheckBox("Real-time Heuristics", true);
        styleCheckBox(chkDeep);
        styleCheckBox(chkHeur);

        chkDeep.addActionListener(e -> logEvent("JCheckBox", "Deep Packet Inspection: " + chkDeep.isSelected()));
        chkHeur.addActionListener(e -> logEvent("JCheckBox", "Real-time Heuristics: " + chkHeur.isSelected()));

        JRadioButton rb1 = new JRadioButton("SOC Tier 1 (Observer)", false);
        JRadioButton rb2 = new JRadioButton("SOC Tier 2 (Analyst)", true);
        JRadioButton rb3 = new JRadioButton("SOC Tier 3 (Full Admin)", false);
        styleRadioButton(rb1);
        styleRadioButton(rb2);
        styleRadioButton(rb3);

        ButtonGroup bg = new ButtonGroup();
        bg.add(rb1); bg.add(rb2); bg.add(rb3);

        rb1.addActionListener(e -> logEvent("JRadioButton", "Selected SOC Tier 1"));
        rb2.addActionListener(e -> logEvent("JRadioButton", "Selected SOC Tier 2"));
        rb3.addActionListener(e -> logEvent("JRadioButton", "Selected SOC Tier 3"));

        JPanel pLeftChecks = new JPanel(new GridLayout(2, 1));
        pLeftChecks.setOpaque(false);
        pLeftChecks.add(chkDeep);
        pLeftChecks.add(chkHeur);

        JPanel pRightRadio = new JPanel(new GridLayout(3, 1));
        pRightRadio.setOpaque(false);
        pRightRadio.add(rb1);
        pRightRadio.add(rb2);
        pRightRadio.add(rb3);

        cardChecks.add(pLeftChecks);
        cardChecks.add(pRightRadio);
        panel.add(cardChecks);

        // Card 4: JComboBox (Standard & Editable)
        JPanel cardCombo = CustomUITheme.createCardPanel("JComboBox (Standard Dropdown & Editable)");
        cardCombo.setLayout(new GridLayout(2, 2, 8, 8));

        JLabel lblCombo1 = new JLabel("Standard Dropdown:");
        lblCombo1.setForeground(CustomUITheme.TEXT_PRIMARY);
        JComboBox<String> combo1 = new JComboBox<>(new String[]{"FileAccessRule", "LoginAnomalyRule", "NetworkRule", "ProcessRule", "ResourceRule"});
        combo1.setBackground(CustomUITheme.PANEL_BG_LIGHT);
        combo1.setForeground(CustomUITheme.TEXT_PRIMARY);
        combo1.addActionListener(e -> logEvent("JComboBox", "Standard selected: " + combo1.getSelectedItem()));

        JLabel lblCombo2 = new JLabel("Editable JComboBox:");
        lblCombo2.setForeground(CustomUITheme.TEXT_PRIMARY);
        JComboBox<String> combo2 = new JComboBox<>(new String[]{"192.168.1.1", "10.0.0.4", "172.16.0.1"});
        combo2.setEditable(true);
        combo2.setBackground(CustomUITheme.PANEL_BG_LIGHT);
        combo2.setForeground(CustomUITheme.TEXT_PRIMARY);
        combo2.addActionListener(e -> logEvent("JComboBox (Editable)", "Value chosen/entered: " + combo2.getSelectedItem()));

        cardCombo.add(lblCombo1);
        cardCombo.add(combo1);
        cardCombo.add(lblCombo2);
        cardCombo.add(combo2);
        panel.add(cardCombo);

        return panel;
    }

    // =========================================================================
    // TAB 2: SLIDERS, SPINNERS & PROGRESS BARS
    // =========================================================================
    private JPanel createSlidersSpinnersTab() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 12, 12));
        panel.setBackground(CustomUITheme.BG_DARK);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // 1. JSlider (Horizontal & Vertical)
        JPanel cardSlider = CustomUITheme.createCardPanel("JSlider (Horizontal & Vertical with Tick Labels)");
        cardSlider.setLayout(new BorderLayout(8, 8));

        JPanel pHoriz = new JPanel(new BorderLayout(6, 0));
        pHoriz.setOpaque(false);
        JLabel lblHVal = new JLabel("Sensitivity: 65%");
        lblHVal.setFont(CustomUITheme.FONT_BODY_BOLD);
        lblHVal.setForeground(CustomUITheme.ACCENT_CYAN);

        JSlider sliderH = new JSlider(0, 100, 65);
        sliderH.setBackground(CustomUITheme.PANEL_BG);
        sliderH.setForeground(CustomUITheme.TEXT_MUTED);
        sliderH.setMajorTickSpacing(25);
        sliderH.setMinorTickSpacing(5);
        sliderH.setPaintTicks(true);
        sliderH.setPaintLabels(true);
        sliderH.addChangeListener(e -> {
            lblHVal.setText("Sensitivity: " + sliderH.getValue() + "%");
            if (!sliderH.getValueIsAdjusting()) {
                logEvent("JSlider", "Horizontal value adjusted: " + sliderH.getValue());
            }
        });
        pHoriz.add(lblHVal, BorderLayout.NORTH);
        pHoriz.add(sliderH, BorderLayout.CENTER);

        // Vertical slider
        JPanel pVert = new JPanel(new BorderLayout(0, 4));
        pVert.setOpaque(false);
        JLabel lblV = new JLabel("Throttle", SwingConstants.CENTER);
        lblV.setFont(CustomUITheme.FONT_SMALL);
        lblV.setForeground(CustomUITheme.TEXT_MUTED);
        JSlider sliderV = new JSlider(JSlider.VERTICAL, 0, 100, 40);
        sliderV.setBackground(CustomUITheme.PANEL_BG);
        sliderV.setForeground(CustomUITheme.TEXT_MUTED);
        sliderV.setMajorTickSpacing(50);
        sliderV.setPaintTicks(true);
        sliderV.setPaintLabels(true);
        sliderV.addChangeListener(e -> {
            if (!sliderV.getValueIsAdjusting()) {
                logEvent("JSlider (Vertical)", "Throttle value: " + sliderV.getValue());
            }
        });
        pVert.add(lblV, BorderLayout.NORTH);
        pVert.add(sliderV, BorderLayout.CENTER);

        cardSlider.add(pHoriz, BorderLayout.CENTER);
        cardSlider.add(pVert, BorderLayout.EAST);
        panel.add(cardSlider);

        // 2. JSpinner (Number, Date, List)
        JPanel cardSpinner = CustomUITheme.createCardPanel("JSpinner (NumberModel, DateModel, ListModel)");
        cardSpinner.setLayout(new GridLayout(3, 2, 8, 8));

        JLabel lblSpinNum = new JLabel("Numeric (Threshold 10-100):");
        lblSpinNum.setForeground(CustomUITheme.TEXT_PRIMARY);
        JSpinner spinNum = new JSpinner(new SpinnerNumberModel(50, 10, 100, 5));
        spinNum.addChangeListener(e -> logEvent("JSpinner (Number)", "Threshold: " + spinNum.getValue()));

        JLabel lblSpinDate = new JLabel("Date Scope (SpinnerDateModel):");
        lblSpinDate.setForeground(CustomUITheme.TEXT_PRIMARY);
        JSpinner spinDate = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spinDate, "yyyy-MM-dd HH:mm");
        spinDate.setEditor(dateEditor);
        spinDate.addChangeListener(e -> logEvent("JSpinner (Date)", "Date changed: " + spinDate.getValue()));

        JLabel lblSpinList = new JLabel("List (SpinnerListModel):");
        lblSpinList.setForeground(CustomUITheme.TEXT_PRIMARY);
        String[] protocolList = {"TCP/443 (HTTPS)", "TCP/80 (HTTP)", "UDP/53 (DNS)", "TCP/22 (SSH)", "TCP/3389 (RDP)"};
        JSpinner spinList = new JSpinner(new SpinnerListModel(protocolList));
        spinList.addChangeListener(e -> logEvent("JSpinner (List)", "Protocol: " + spinList.getValue()));

        cardSpinner.add(lblSpinNum);
        cardSpinner.add(spinNum);
        cardSpinner.add(lblSpinDate);
        cardSpinner.add(spinDate);
        cardSpinner.add(lblSpinList);
        cardSpinner.add(spinList);
        panel.add(cardSpinner);

        // 3. JProgressBar & JSeparator
        JPanel cardProgress = CustomUITheme.createCardPanel("JProgressBar & JSeparator");
        cardProgress.setLayout(new GridLayout(5, 1, 0, 6));

        JLabel lblProg1 = new JLabel("Determinate Progress Bar (78%):");
        lblProg1.setForeground(CustomUITheme.TEXT_PRIMARY);
        JProgressBar prog1 = new JProgressBar(0, 100);
        prog1.setValue(78);
        CustomUITheme.styleProgressBar(prog1, CustomUITheme.HEALTHY_GREEN);

        JLabel lblProg2 = new JLabel("Indeterminate Pulsing Scanner Bar:");
        lblProg2.setForeground(CustomUITheme.TEXT_PRIMARY);
        JProgressBar prog2 = new JProgressBar();
        prog2.setIndeterminate(true);
        CustomUITheme.styleProgressBar(prog2, CustomUITheme.ACCENT_CYAN);

        JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
        sep.setForeground(CustomUITheme.BORDER_COLOR);

        cardProgress.add(lblProg1);
        cardProgress.add(prog1);
        cardProgress.add(lblProg2);
        cardProgress.add(prog2);
        cardProgress.add(sep);
        panel.add(cardProgress);

        // 4. JScrollBar (Standalone)
        JPanel cardScroll = CustomUITheme.createCardPanel("JScrollBar (Standalone Adjustment Listener)");
        cardScroll.setLayout(new BorderLayout(8, 8));

        JLabel lblScrollVal = new JLabel("Scroll Offset: 50 px");
        lblScrollVal.setFont(CustomUITheme.FONT_BODY_BOLD);
        lblScrollVal.setForeground(CustomUITheme.ACCENT_CYAN);

        JScrollBar standaloneScroll = new JScrollBar(JScrollBar.HORIZONTAL, 50, 10, 0, 200);
        standaloneScroll.setBackground(CustomUITheme.PANEL_BG_LIGHT);
        standaloneScroll.addAdjustmentListener(e -> {
            lblScrollVal.setText("Scroll Offset: " + e.getValue() + " px");
            logEvent("JScrollBar", "Offset adjusted: " + e.getValue());
        });

        cardScroll.add(lblScrollVal, BorderLayout.NORTH);
        cardScroll.add(standaloneScroll, BorderLayout.CENTER);
        panel.add(cardScroll);

        return panel;
    }

    // =========================================================================
    // TAB 3: TEXT & RICH DOCUMENTS
    // =========================================================================
    private JPanel createTextComponentsTab() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 12, 12));
        panel.setBackground(CustomUITheme.BG_DARK);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Left Column: JTextField, JPasswordField, JFormattedTextField, JTextArea
        JPanel left = CustomUITheme.createCardPanel("Standard Text Inputs (JTextField, JPasswordField, JTextArea)");
        left.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 5, 5, 5);
        g.fill = GridBagConstraints.HORIZONTAL;

        g.gridx = 0; g.gridy = 0; g.weightx = 0.3;
        JLabel lblUser = new JLabel("JTextField:");
        lblUser.setForeground(CustomUITheme.TEXT_PRIMARY);
        left.add(lblUser, g);

        g.gridx = 1; g.weightx = 0.7;
        JTextField txtUser = new JTextField("admin_analyst");
        styleTextField(txtUser);
        txtUser.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { logEvent("JTextField", "Text: " + txtUser.getText()); }
            public void removeUpdate(DocumentEvent e) { logEvent("JTextField", "Text: " + txtUser.getText()); }
            public void changedUpdate(DocumentEvent e) { logEvent("JTextField", "Text: " + txtUser.getText()); }
        });
        left.add(txtUser, g);

        g.gridx = 0; g.gridy = 1;
        JLabel lblPass = new JLabel("JPasswordField:");
        lblPass.setForeground(CustomUITheme.TEXT_PRIMARY);
        left.add(lblPass, g);

        g.gridx = 1;
        JPasswordField txtPass = new JPasswordField("secureKey123");
        styleTextField(txtPass);
        left.add(txtPass, g);

        g.gridx = 0; g.gridy = 2;
        JLabel lblFormat = new JLabel("JFormattedTextField:");
        lblFormat.setForeground(CustomUITheme.TEXT_PRIMARY);
        left.add(lblFormat, g);

        g.gridx = 1;
        JFormattedTextField txtFormat = new JFormattedTextField(new SimpleDateFormat("yyyy-MM-dd"));
        txtFormat.setValue(new Date());
        styleTextField(txtFormat);
        left.add(txtFormat, g);

        g.gridx = 0; g.gridy = 3; g.gridwidth = 2;
        JLabel lblArea = new JLabel("JTextArea (Inside JScrollPane):");
        lblArea.setForeground(CustomUITheme.TEXT_PRIMARY);
        left.add(lblArea, g);

        g.gridy = 4; g.weighty = 1.0; g.fill = GridBagConstraints.BOTH;
        JTextArea txtArea = new JTextArea("RAW EVENT LOG:\n[10:14:02] Probe received on Port 443\n[10:14:05] Host authenticated successfully\n[10:14:10] Baseline divergence: 0.04 (Normal)");
        txtArea.setBackground(CustomUITheme.PANEL_BG_LIGHT);
        txtArea.setForeground(CustomUITheme.TEXT_PRIMARY);
        txtArea.setFont(CustomUITheme.FONT_MONO);
        JScrollPane scrollArea = new JScrollPane(txtArea);
        scrollArea.setBorder(new LineBorder(CustomUITheme.BORDER_COLOR, 1));
        left.add(scrollArea, g);

        panel.add(left);

        // Right Column: JTextPane & JEditorPane
        JPanel right = CustomUITheme.createCardPanel("Rich Formatted Documents (JTextPane & JEditorPane)");
        right.setLayout(new GridLayout(2, 1, 8, 8));

        // JTextPane
        JPanel pTextPane = new JPanel(new BorderLayout());
        pTextPane.setOpaque(false);
        JLabel lblStyled = new JLabel("JTextPane (StyledDocument with Colored Attributes):");
        lblStyled.setForeground(CustomUITheme.TEXT_PRIMARY);
        pTextPane.add(lblStyled, BorderLayout.NORTH);

        JTextPane styledPane = new JTextPane();
        styledPane.setBackground(CustomUITheme.PANEL_BG_LIGHT);
        styledPane.setEditable(false);
        populateStyledPane(styledPane);
        pTextPane.add(new JScrollPane(styledPane), BorderLayout.CENTER);
        right.add(pTextPane);

        // JEditorPane (HTML)
        JPanel pEditorPane = new JPanel(new BorderLayout());
        pEditorPane.setOpaque(false);
        JLabel lblHtml = new JLabel("JEditorPane (HTML / CSS Renderer):");
        lblHtml.setForeground(CustomUITheme.TEXT_PRIMARY);
        pEditorPane.add(lblHtml, BorderLayout.NORTH);

        JEditorPane htmlPane = new JEditorPane();
        htmlPane.setContentType("text/html");
        htmlPane.setEditable(false);
        htmlPane.setBackground(CustomUITheme.PANEL_BG_LIGHT);
        htmlPane.setText("<html><body style='font-family:Segoe UI; color:#F8FAFC; padding:8px;'>"
                + "<h3 style='color:#00F0FF; margin-top:0;'>HTML SECURITY ADVISORY</h3>"
                + "<p>Status: <span style='color:#10B981; font-weight:bold;'>DEFENSE HOMEOSTASIS OPTIMAL</span></p>"
                + "<p>Rendering live rich content via <code>javax.swing.JEditorPane</code> with zero external dependencies.</p>"
                + "</body></html>");
        pEditorPane.add(new JScrollPane(htmlPane), BorderLayout.CENTER);
        right.add(pEditorPane);

        panel.add(right);
        return panel;
    }

    private void populateStyledPane(JTextPane pane) {
        StyledDocument doc = pane.getStyledDocument();
        Style def = doc.addStyle("def", null);
        StyleConstants.setForeground(def, CustomUITheme.TEXT_PRIMARY);
        StyleConstants.setFontFamily(def, "Consolas");

        Style cyan = doc.addStyle("cyan", def);
        StyleConstants.setForeground(cyan, CustomUITheme.ACCENT_CYAN);
        StyleConstants.setBold(cyan, true);

        Style green = doc.addStyle("green", def);
        StyleConstants.setForeground(green, CustomUITheme.HEALTHY_GREEN);

        Style red = doc.addStyle("red", def);
        StyleConstants.setForeground(red, CustomUITheme.CRITICAL_RED);
        StyleConstants.setBold(red, true);

        try {
            doc.insertString(doc.getLength(), "HEURISTIC ANOMALY EXPLANATION\n", cyan);
            doc.insertString(doc.getLength(), "[STATUS] Cellular tolerance active.\n", green);
            doc.insertString(doc.getLength(), "[FLAG] Token entropy divergence flagged at 84%.\n", red);
            doc.insertString(doc.getLength(), "Antibody rule match: ProcessMasqueradingRule -> Lysis.\n", def);
        } catch (BadLocationException ignored) {
        }
    }

    // =========================================================================
    // TAB 4: DATA COMPONENTS (JTABLE, JTREE, JLIST)
    // =========================================================================
    private JPanel createDataComponentsTab() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 10, 10));
        panel.setBackground(CustomUITheme.BG_DARK);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // 1. JTable
        JPanel cardTable = CustomUITheme.createCardPanel("JTable (Sortable Model & Custom Cell Renderer)");
        cardTable.setLayout(new BorderLayout(0, 6));

        String[] cols = {"Vector", "Entropy", "Severity"};
        Object[][] data = {
                {"Auth Burst", "84%", "CRITICAL"},
                {"File Re-read", "12%", "NORMAL"},
                {"Memory Injection", "95%", "CRITICAL"},
                {"DNS Tunneling", "68%", "HIGH"},
                {"Registry Query", "25%", "NORMAL"}
        };
        DefaultTableModel model = new DefaultTableModel(data, cols) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        CustomUITheme.styleTable(table);
        table.setRowSorter(new TableRowSorter<>(model));

        // Severity renderer
        table.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                String v = String.valueOf(value);
                if ("CRITICAL".equals(v)) l.setForeground(CustomUITheme.CRITICAL_RED);
                else if ("HIGH".equals(v)) l.setForeground(CustomUITheme.WARNING_ORANGE);
                else l.setForeground(CustomUITheme.HEALTHY_GREEN);
                return l;
            }
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                int r = table.getSelectedRow();
                logEvent("JTable", "Row " + r + " selected: " + table.getValueAt(r, 0));
            }
        });

        cardTable.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(cardTable);

        // 2. JTree
        JPanel cardTree = CustomUITheme.createCardPanel("JTree (DefaultMutableTreeNode Hierarchy)");
        cardTree.setLayout(new BorderLayout(0, 6));

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Digital Autoimmune Domain");
        DefaultMutableTreeNode n1 = new DefaultMutableTreeNode("Subnet 10.0.0.0/24 (Production)");
        n1.add(new DefaultMutableTreeNode("Host: web-cluster-01 [Healthy]"));
        n1.add(new DefaultMutableTreeNode("Host: db-primary-01 [Healthy]"));
        DefaultMutableTreeNode n2 = new DefaultMutableTreeNode("Subnet 192.168.1.0/24 (Workstations)");
        n2.add(new DefaultMutableTreeNode("Endpoint: WS-FINANCE-09 [Anomalous]"));
        n2.add(new DefaultMutableTreeNode("Endpoint: WS-DEVOPS-14 [Healthy]"));
        DefaultMutableTreeNode n3 = new DefaultMutableTreeNode("Biological Memory Bank");
        n3.add(new DefaultMutableTreeNode("Learned Baselines: 12 Identities"));

        root.add(n1); root.add(n2); root.add(n3);
        DefaultTreeModel treeModel = new DefaultTreeModel(root);
        JTree tree = new JTree(treeModel);
        tree.setBackground(CustomUITheme.PANEL_BG);

        DefaultTreeCellRenderer treeRenderer = new DefaultTreeCellRenderer();
        treeRenderer.setBackgroundNonSelectionColor(CustomUITheme.PANEL_BG);
        treeRenderer.setTextNonSelectionColor(CustomUITheme.TEXT_PRIMARY);
        treeRenderer.setTextSelectionColor(CustomUITheme.ACCENT_CYAN);
        treeRenderer.setBackgroundSelectionColor(new Color(38, 70, 110));
        tree.setCellRenderer(treeRenderer);

        tree.addTreeSelectionListener(e -> {
            if (tree.getLastSelectedPathComponent() != null) {
                logEvent("JTree", "Selected Node: " + tree.getLastSelectedPathComponent().toString());
            }
        });

        cardTree.add(new JScrollPane(tree), BorderLayout.CENTER);
        panel.add(cardTree);

        // 3. JList
        JPanel cardList = CustomUITheme.createCardPanel("JList (DefaultListModel & Cyber Cell Renderer)");
        cardList.setLayout(new BorderLayout(0, 6));

        DefaultListModel<String> listModel = new DefaultListModel<>();
        listModel.addElement("[IOC-01] svch0st.exe Trojan");
        listModel.addElement("[IOC-02] CobaltStrike Beacon");
        listModel.addElement("[IOC-03] Shadow Copy Deletion");
        listModel.addElement("[IOC-04] Brute Force Sweep");
        listModel.addElement("[IOC-05] High-Entropy Write");

        JList<String> list = new JList<>(listModel);
        list.setBackground(CustomUITheme.PANEL_BG);
        list.setForeground(CustomUITheme.TEXT_PRIMARY);
        list.setFont(CustomUITheme.FONT_BODY);
        list.setSelectionBackground(new Color(38, 70, 110));
        list.setSelectionForeground(CustomUITheme.ACCENT_CYAN);

        list.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && list.getSelectedValue() != null) {
                logEvent("JList", "Selected: " + list.getSelectedValue());
            }
        });

        cardList.add(new JScrollPane(list), BorderLayout.CENTER);
        panel.add(cardList);

        return panel;
    }

    // =========================================================================
    // TAB 5: CONTAINERS & MENUS
    // =========================================================================
    private JPanel createContainersTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(CustomUITheme.BG_DARK);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // JToolBar Demo
        JToolBar toolBar = new JToolBar("Cyber SOC Toolbar");
        toolBar.setBackground(CustomUITheme.PANEL_BG_LIGHT);
        toolBar.setBorder(new LineBorder(CustomUITheme.BORDER_COLOR, 1));
        toolBar.setFloatable(true);

        JButton tbBtn1 = new JButton("Run Scan");
        JButton tbBtn2 = new JButton("Pause");
        JButton tbBtn3 = new JButton("Export");
        tbBtn1.setBackground(CustomUITheme.PANEL_BG);
        tbBtn2.setBackground(CustomUITheme.PANEL_BG);
        tbBtn3.setBackground(CustomUITheme.PANEL_BG);
        tbBtn1.setForeground(CustomUITheme.ACCENT_CYAN);
        tbBtn2.setForeground(CustomUITheme.TEXT_PRIMARY);
        tbBtn3.setForeground(CustomUITheme.TEXT_PRIMARY);

        tbBtn1.addActionListener(e -> logEvent("JToolBar", "Clicked 'Run Scan'"));
        tbBtn2.addActionListener(e -> logEvent("JToolBar", "Clicked 'Pause'"));
        tbBtn3.addActionListener(e -> logEvent("JToolBar", "Clicked 'Export'"));

        toolBar.add(tbBtn1);
        toolBar.add(tbBtn2);
        toolBar.addSeparator();
        toolBar.add(tbBtn3);

        panel.add(toolBar, BorderLayout.NORTH);

        // Center: JSplitPane demo (Horizontal Split with Left & Right Panels)
        JSplitPane innerSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        innerSplit.setDividerLocation(380);
        innerSplit.setBorder(null);

        // Left of split: JLayeredPane Demo
        JPanel leftLayerCard = CustomUITheme.createCardPanel("JLayeredPane (Overlapping Z-Ordered Layers)");
        leftLayerCard.setLayout(new BorderLayout());

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(340, 220));

        JPanel layer1 = new JPanel();
        layer1.setBackground(new Color(22, 32, 50));
        layer1.setBorder(new LineBorder(CustomUITheme.ACCENT_BLUE, 2));
        layer1.setBounds(20, 20, 240, 140);
        JLabel l1 = new JLabel("Layer 0 (Base Container)");
        l1.setForeground(Color.WHITE);
        layer1.add(l1);

        JPanel layer2 = new JPanel();
        layer2.setBackground(new Color(15, 23, 42, 230));
        layer2.setBorder(new LineBorder(CustomUITheme.ACCENT_CYAN, 2));
        layer2.setBounds(60, 60, 240, 140);
        JLabel l2 = new JLabel("Layer 1 (Overlay HUD Badge)");
        l2.setForeground(CustomUITheme.ACCENT_CYAN);
        layer2.add(l2);

        layeredPane.add(layer1, Integer.valueOf(0));
        layeredPane.add(layer2, Integer.valueOf(1));

        leftLayerCard.add(layeredPane, BorderLayout.CENTER);
        innerSplit.setLeftComponent(leftLayerCard);

        // Right of split: JPopupMenu Trigger Card
        JPanel rightPopupCard = CustomUITheme.createCardPanel("JPopupMenu (Context Menu Demonstration)");
        rightPopupCard.setLayout(new GridBagLayout());

        JLabel lblRightClick = new JLabel(">>> RIGHT-CLICK ANYWHERE HERE TO OPEN JPopupMenu <<<");
        lblRightClick.setFont(CustomUITheme.FONT_BODY_BOLD);
        lblRightClick.setForeground(CustomUITheme.ACCENT_CYAN);

        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem pi1 = new JMenuItem("Triage Security Event");
        JMenuItem pi2 = new JMenuItem("Copy Actor IP");
        JMenuItem pi3 = new JMenuItem("Quarantine Process");
        pi1.addActionListener(e -> logEvent("JPopupMenu", "Action: Triage Security Event"));
        pi2.addActionListener(e -> logEvent("JPopupMenu", "Action: Copy Actor IP"));
        pi3.addActionListener(e -> logEvent("JPopupMenu", "Action: Quarantine Process"));
        popupMenu.add(pi1);
        popupMenu.add(pi2);
        popupMenu.addSeparator();
        popupMenu.add(pi3);

        rightPopupCard.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) { maybeShowPopup(e); }
            @Override
            public void mouseReleased(MouseEvent e) { maybeShowPopup(e); }
            private void maybeShowPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                    logEvent("JPopupMenu", "Context popup displayed at (" + e.getX() + ", " + e.getY() + ")");
                }
            }
        });

        rightPopupCard.add(lblRightClick);
        innerSplit.setRightComponent(rightPopupCard);

        panel.add(innerSplit, BorderLayout.CENTER);
        return panel;
    }

    // =========================================================================
    // TAB 6: DIALOGS & PICKERS
    // =========================================================================
    private JPanel createDialogsTab() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 12, 12));
        panel.setBackground(CustomUITheme.BG_DARK);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // 1. JOptionPane Gallery
        JPanel cardOpt = CustomUITheme.createCardPanel("JOptionPane Dialog Suite");
        cardOpt.setLayout(new GridLayout(4, 1, 6, 6));

        JButton btnInfo = CustomUITheme.createStyledButton("Information Dialog", CustomUITheme.ACCENT_CYAN);
        JButton btnWarn = CustomUITheme.createStyledButton("Warning Dialog", CustomUITheme.WARNING_YELLOW);
        JButton btnErr = CustomUITheme.createStyledButton("Error Dialog", CustomUITheme.CRITICAL_RED);
        JButton btnInput = CustomUITheme.createStyledButton("Input Dialog", CustomUITheme.HEALTHY_GREEN);

        btnInfo.addActionListener(e -> JOptionPane.showMessageDialog(this, "Cellular memory synchronized.", "Information", JOptionPane.INFORMATION_MESSAGE));
        btnWarn.addActionListener(e -> JOptionPane.showMessageDialog(this, "Baseline deviation tolerance reached.", "Warning", JOptionPane.WARNING_MESSAGE));
        btnErr.addActionListener(e -> JOptionPane.showMessageDialog(this, "Critical acute pathogen detected!", "Error", JOptionPane.ERROR_MESSAGE));
        btnInput.addActionListener(e -> {
            String val = JOptionPane.showInputDialog(this, "Enter IP or Process Name to Quarantine:", "Input Required", JOptionPane.QUESTION_MESSAGE);
            if (val != null) logEvent("JOptionPane", "User input string: " + val);
        });

        cardOpt.add(btnInfo); cardOpt.add(btnWarn); cardOpt.add(btnErr); cardOpt.add(btnInput);
        panel.add(cardOpt);

        // 2. JColorChooser
        JPanel cardColor = CustomUITheme.createCardPanel("JColorChooser Demonstration");
        cardColor.setLayout(new BorderLayout(8, 8));

        JPanel previewBox = new JPanel();
        previewBox.setBackground(CustomUITheme.ACCENT_CYAN);
        previewBox.setBorder(new LineBorder(CustomUITheme.BORDER_COLOR, 1));
        JLabel lblPreview = new JLabel("Preview Color Accent", SwingConstants.CENTER);
        lblPreview.setFont(CustomUITheme.FONT_BODY_BOLD);
        lblPreview.setForeground(Color.BLACK);
        previewBox.add(lblPreview);

        JButton btnPickColor = CustomUITheme.createStyledButton("Launch JColorChooser Dialog...", CustomUITheme.ACCENT_CYAN);
        btnPickColor.addActionListener(e -> {
            Color c = JColorChooser.showDialog(this, "Choose Cyber Theme Accent", previewBox.getBackground());
            if (c != null) {
                previewBox.setBackground(c);
                logEvent("JColorChooser", String.format("New accent RGB(%d, %d, %d)", c.getRed(), c.getGreen(), c.getBlue()));
            }
        });

        cardColor.add(previewBox, BorderLayout.CENTER);
        cardColor.add(btnPickColor, BorderLayout.SOUTH);
        panel.add(cardColor);

        // 3. JFileChooser
        JPanel cardFile = CustomUITheme.createCardPanel("JFileChooser Demonstration");
        cardFile.setLayout(new GridLayout(3, 1, 6, 6));

        JButton btnOpen = CustomUITheme.createStyledButton("Open File (showOpenDialog)", CustomUITheme.ACCENT_CYAN);
        JButton btnSave = CustomUITheme.createStyledButton("Save Report (showSaveDialog)", CustomUITheme.HEALTHY_GREEN);

        btnOpen.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            int res = fc.showOpenDialog(this);
            if (res == JFileChooser.APPROVE_OPTION) {
                logEvent("JFileChooser", "Selected file: " + fc.getSelectedFile().getAbsolutePath());
            }
        });

        btnSave.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setSelectedFile(new File("cyber_intelligence_report.txt"));
            int res = fc.showSaveDialog(this);
            if (res == JFileChooser.APPROVE_OPTION) {
                logEvent("JFileChooser", "Save target: " + fc.getSelectedFile().getAbsolutePath());
            }
        });

        cardFile.add(btnOpen);
        cardFile.add(btnSave);
        panel.add(cardFile);

        // 4. Custom JDialog
        JPanel cardDialog = CustomUITheme.createCardPanel("Custom Modal JDialog");
        cardDialog.setLayout(new BorderLayout(8, 8));

        JButton btnCustomDlg = CustomUITheme.createStyledButton("Open Custom Modal JDialog", CustomUITheme.ACCENT_BLUE);
        btnCustomDlg.addActionListener(e -> {
            Window parent = SwingUtilities.getWindowAncestor(this);
            JDialog dlg = new JDialog(parent, "Custom JDialog Modal", Dialog.ModalityType.APPLICATION_MODAL);
            dlg.setSize(380, 220);
            dlg.setLocationRelativeTo(this);
            dlg.getContentPane().setBackground(CustomUITheme.BG_DARK);
            dlg.setLayout(new BorderLayout(10, 10));

            JPanel p = CustomUITheme.createCardPanel();
            p.setLayout(new GridLayout(3, 1));
            JLabel l = new JLabel("Autonomous Defense Directive Modal", SwingConstants.CENTER);
            l.setFont(CustomUITheme.FONT_BODY_BOLD);
            l.setForeground(CustomUITheme.ACCENT_CYAN);
            JButton bClose = CustomUITheme.createStyledButton("Close Modal Dialog", CustomUITheme.BORDER_COLOR);
            bClose.addActionListener(ev -> dlg.dispose());

            p.add(l);
            p.add(new JLabel("Modal dialog blocks parent interaction safely on EDT.", SwingConstants.CENTER));
            p.add(bClose);

            dlg.add(p, BorderLayout.CENTER);
            logEvent("JDialog", "Opened custom application modal");
            dlg.setVisible(true);
        });

        cardDialog.add(btnCustomDlg, BorderLayout.CENTER);
        panel.add(cardDialog);

        return panel;
    }

    // =========================================================================
    // TAB 7: SWINGWORKER BACKGROUND CONCURRENCY
    // =========================================================================
    private JPanel createConcurrencyTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(CustomUITheme.BG_DARK);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Control Panel
        JPanel topCtrl = CustomUITheme.createCardPanel("Asynchronous Background Thread (javax.swing.SwingWorker)");
        topCtrl.setLayout(new BorderLayout(10, 0));

        JPanel leftButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftButtons.setOpaque(false);

        btnStartWorker = CustomUITheme.createStyledButton("Start Async Scan Worker", CustomUITheme.HEALTHY_GREEN);
        btnCancelWorker = CustomUITheme.createStyledButton("Cancel Worker Thread", CustomUITheme.CRITICAL_RED);
        btnCancelWorker.setEnabled(false);

        btnStartWorker.addActionListener(e -> startWorkerTask());
        btnCancelWorker.addActionListener(e -> cancelWorkerTask());

        lblWorkerStatus = new JLabel("Worker Thread: IDLE");
        lblWorkerStatus.setFont(CustomUITheme.FONT_BODY_BOLD);
        lblWorkerStatus.setForeground(CustomUITheme.TEXT_MUTED);

        leftButtons.add(btnStartWorker);
        leftButtons.add(btnCancelWorker);
        leftButtons.add(lblWorkerStatus);
        topCtrl.add(leftButtons, BorderLayout.WEST);

        panel.add(topCtrl, BorderLayout.NORTH);

        // Center: Progress and Real-time Stream Output
        JPanel centerCard = CustomUITheme.createCardPanel("EDT Safe Event Stream (publish() / process())");
        centerCard.setLayout(new BorderLayout(0, 10));

        workerProgress = new JProgressBar(0, 100);
        workerProgress.setValue(0);
        workerProgress.setStringPainted(true);
        CustomUITheme.styleProgressBar(workerProgress, CustomUITheme.ACCENT_CYAN);
        workerProgress.setPreferredSize(new Dimension(0, 24));
        centerCard.add(workerProgress, BorderLayout.NORTH);

        workerListModel = new DefaultListModel<>();
        workerListModel.addElement("[READY] Click 'Start Async Scan Worker' to begin background task.");
        JList<String> streamList = new JList<>(workerListModel);
        streamList.setBackground(CustomUITheme.PANEL_BG_LIGHT);
        streamList.setForeground(CustomUITheme.TEXT_PRIMARY);
        streamList.setFont(CustomUITheme.FONT_MONO);

        centerCard.add(new JScrollPane(streamList), BorderLayout.CENTER);
        panel.add(centerCard, BorderLayout.CENTER);

        return panel;
    }

    private void startWorkerTask() {
        btnStartWorker.setEnabled(false);
        btnCancelWorker.setEnabled(true);
        lblWorkerStatus.setText("Worker Thread: RUNNING IN BACKGROUND");
        lblWorkerStatus.setForeground(CustomUITheme.ACCENT_CYAN);
        workerProgress.setValue(0);
        workerListModel.clear();

        logEvent("SwingWorker", "Spawned background worker task");

        // Inline SwingWorker — simulates a background security scan with 20 steps.
        // doInBackground() runs on a worker thread; publish() sends messages to the EDT;
        // process() updates the UI safely; done() runs on the EDT when finished.
        activeWorker = new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                String[] tasks = {
                    "Initialising immune scan engine...",
                    "Loading 5 detection rule signatures...",
                    "Scanning login activity for user01...",
                    "Analysing CPU usage patterns...",
                    "Checking memory allocation baselines...",
                    "Inspecting file access logs...",
                    "Monitoring network egress streams...",
                    "Correlating behaviour against baseline profiles...",
                    "Running anomaly scoring algorithm...",
                    "Score computed: 45/100 — LOW RISK",
                    "Generating alert recommendation...",
                    "Scan complete. No critical threats detected."
                };
                for (int i = 0; i < tasks.length; i++) {
                    if (isCancelled()) break;
                    Thread.sleep(400); // Simulate work
                    publish(tasks[i]);
                    setProgress((int)((i + 1) * 100.0 / tasks.length));
                }
                return null;
            }

            @Override
            protected void process(java.util.List<String> chunks) {
                // Called on EDT with batched messages from publish()
                for (String msg : chunks) {
                    workerListModel.addElement(msg);
                    logEvent("SwingWorker", msg);
                    workerProgress.setValue(getProgress());
                }
            }

            @Override
            protected void done() {
                // Called on EDT after doInBackground() finishes or is cancelled
                btnStartWorker.setEnabled(true);
                btnCancelWorker.setEnabled(false);
                if (isCancelled()) {
                    lblWorkerStatus.setText("Worker Thread: CANCELLED");
                    lblWorkerStatus.setForeground(CustomUITheme.WARNING_YELLOW);
                } else {
                    lblWorkerStatus.setText("Worker Thread: COMPLETED");
                    lblWorkerStatus.setForeground(CustomUITheme.HEALTHY_GREEN);
                    logEvent("SwingWorker", "Background scan finished on EDT via done()");
                }
            }
        };

        activeWorker.execute();
    }

    private void cancelWorkerTask() {
        if (activeWorker != null && !activeWorker.isDone()) {
            activeWorker.cancel(true);
            btnStartWorker.setEnabled(true);
            btnCancelWorker.setEnabled(false);
            lblWorkerStatus.setText("Worker Thread: CANCELLED");
            lblWorkerStatus.setForeground(CustomUITheme.WARNING_YELLOW);
            logEvent("SwingWorker", "Worker cancellation requested");
        }
    }

    // =========================================================================
    // TAB 8: CUSTOM JAVA2D DRAWING
    // Demonstrates custom graphics by drawing directly in paintComponent().
    // This is simpler and equally educational compared to custom components.
    // =========================================================================
    private JPanel createCustomWidgetsTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(CustomUITheme.BG_DARK);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JTabbedPane customTabs = new JTabbedPane();
        customTabs.setBackground(CustomUITheme.PANEL_BG);
        customTabs.setForeground(CustomUITheme.ACCENT_CYAN);

        // Subtab 1: Java2D Radar Drawing — demonstrates Graphics2D, arcs, ovals, colours
        JPanel pRadar = new JPanel(new BorderLayout(10, 10));
        pRadar.setBackground(CustomUITheme.BG_DARK);
        pRadar.setBorder(new EmptyBorder(10, 10, 10, 10));

        // A custom JPanel that draws a radar-style circle using Graphics2D
        java.util.List<double[]> blips = new java.util.ArrayList<>();
        JPanel radarCanvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int cx = getWidth() / 2;
                int cy = getHeight() / 2;
                int maxR = Math.min(cx, cy) - 10;

                // Draw concentric range rings
                g2.setColor(new Color(0, 240, 255, 40));
                for (int ring = 1; ring <= 4; ring++) {
                    int r = maxR * ring / 4;
                    g2.drawOval(cx - r, cy - r, r * 2, r * 2);
                }
                // Draw crosshairs
                g2.setColor(new Color(0, 240, 255, 60));
                g2.drawLine(cx - maxR, cy, cx + maxR, cy);
                g2.drawLine(cx, cy - maxR, cx, cy + maxR);

                // Draw threat blips
                for (double[] b : blips) {
                    int bx = cx + (int)(b[0] * maxR * Math.cos(b[1]));
                    int by = cy - (int)(b[0] * maxR * Math.sin(b[1]));
                    g2.setColor(new Color(239, 68, 68));
                    g2.fillOval(bx - 5, by - 5, 10, 10);
                }

                // Centre dot
                g2.setColor(CustomUITheme.ACCENT_CYAN);
                g2.fillOval(cx - 4, cy - 4, 8, 8);

                g2.setColor(CustomUITheme.TEXT_MUTED);
                g2.setFont(CustomUITheme.FONT_SMALL);
                g2.drawString("Java2D Radar Canvas (Graphics2D)", 8, 18);
            }
        };
        radarCanvas.setBackground(CustomUITheme.PANEL_BG);

        JPanel radarControls = CustomUITheme.createCardPanel("Java2D Radar Controls");
        radarControls.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 6));

        JButton btnAddBlip = CustomUITheme.createStyledButton("+ Add Threat Blip", CustomUITheme.CRITICAL_RED);
        btnAddBlip.addActionListener(e -> {
            double angle = Math.random() * 2 * Math.PI;
            double dist  = 0.2 + Math.random() * 0.7;
            blips.add(new double[]{dist, angle});
            radarCanvas.repaint();
            logEvent("Java2D Radar", "Added random threat blip at angle=" + String.format("%.2f", angle));
        });
        JButton btnClearBlips = CustomUITheme.createStyledButton("Clear Blips", CustomUITheme.BORDER_COLOR);
        btnClearBlips.addActionListener(e -> { blips.clear(); radarCanvas.repaint(); });

        radarControls.add(btnAddBlip);
        radarControls.add(btnClearBlips);
        pRadar.add(radarCanvas, BorderLayout.CENTER);
        pRadar.add(radarControls, BorderLayout.SOUTH);
        customTabs.addTab("Java2D Radar", pRadar);

        // Subtab 2: Hex Dump Viewer — simple JTextArea showing hex + ASCII
        JPanel pHex = CustomUITheme.createCardPanel("Hex Memory Dump Viewer (JTextArea)");
        pHex.setLayout(new BorderLayout(0, 8));
        JTextArea hexArea = new JTextArea();
        hexArea.setEditable(false);
        hexArea.setBackground(CustomUITheme.PANEL_BG_LIGHT);
        hexArea.setForeground(new Color(0, 240, 255));
        hexArea.setFont(CustomUITheme.FONT_MONO);
        hexArea.setBorder(new EmptyBorder(8, 10, 8, 10));

        // Generate a sample hex dump
        StringBuilder hexSb = new StringBuilder();
        hexSb.append("OFFSET    00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F   ASCII\n");
        hexSb.append("-------   -----------------------------------------------   ----------------\n");
        byte[] sample = "Digital Autoimmune Monitor | Behaviour Security Engine".getBytes();
        for (int row = 0; row < 4; row++) {
            hexSb.append(String.format("%08X  ", row * 16));
            StringBuilder ascii = new StringBuilder();
            for (int col = 0; col < 16; col++) {
                int idx = row * 16 + col;
                if (idx < sample.length) {
                    hexSb.append(String.format("%02X ", sample[idx]));
                    ascii.append(sample[idx] >= 32 && sample[idx] < 127 ? (char) sample[idx] : '.');
                } else {
                    hexSb.append("   ");
                    ascii.append(' ');
                }
            }
            hexSb.append("  ").append(ascii).append("\n");
        }
        hexArea.setText(hexSb.toString());
        pHex.add(new JScrollPane(hexArea), BorderLayout.CENTER);
        customTabs.addTab("Hex Dump Viewer", pHex);

        // Subtab 3: Live Terminal — a styled JTextArea acting as a simulated terminal
        JPanel pTerminal = CustomUITheme.createCardPanel("Live Terminal Component (JTextArea)");
        pTerminal.setLayout(new BorderLayout(0, 8));
        JTextArea termArea = new JTextArea();
        termArea.setEditable(false);
        termArea.setBackground(new Color(5, 10, 20));
        termArea.setForeground(new Color(0, 240, 0));
        termArea.setFont(CustomUITheme.FONT_MONO);
        termArea.setBorder(new EmptyBorder(10, 14, 10, 14));
        termArea.setText("root@immunity-core:~$ initiate-immune-scan\n> Loading antibody profiles...\n> Scanning behaviour baselines...\n> No anomalies detected.\nroot@immunity-core:~$ _");
        JTextField termInput = new JTextField();
        termInput.setBackground(new Color(5, 10, 20));
        termInput.setForeground(new Color(0, 240, 0));
        termInput.setCaretColor(new Color(0, 240, 0));
        termInput.setFont(CustomUITheme.FONT_MONO);
        termInput.addActionListener(e -> {
            String cmd = termInput.getText().trim();
            termArea.append("\nroot@immunity-core:~$ " + cmd + "\n> [Simulated] Command executed.");
            termInput.setText("");
            logEvent("Terminal", "Command entered: " + cmd);
        });
        pTerminal.add(new JScrollPane(termArea), BorderLayout.CENTER);
        pTerminal.add(termInput, BorderLayout.SOUTH);
        customTabs.addTab("Live Terminal", pTerminal);

        // Subtab 4: Sparkline chart + collapsible section (using simple painting)
        JPanel pMisc = new JPanel(new GridLayout(2, 1, 10, 10));
        pMisc.setBackground(CustomUITheme.BG_DARK);
        pMisc.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Sparkline: custom paintComponent drawing a mini line chart
        java.util.List<Double> sparkData1 = new java.util.ArrayList<>();
        java.util.List<Double> sparkData2 = new java.util.ArrayList<>();
        for (int i = 0; i < 20; i++) {
            sparkData1.add(20 + Math.random() * 60);
            sparkData2.add(10 + Math.random() * 80);
        }
        JPanel sparkCard = CustomUITheme.createCardPanel("MetricSparkline Charts (Java2D)");
        sparkCard.setLayout(new GridLayout(2, 1, 6, 6));

        JPanel spark1 = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawSparkline((Graphics2D) g, sparkData1, CustomUITheme.ACCENT_CYAN, "CPU Usage Trend");
            }
        };
        spark1.setBackground(CustomUITheme.PANEL_BG_LIGHT);

        JPanel spark2 = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawSparkline((Graphics2D) g, sparkData2, CustomUITheme.CRITICAL_RED, "Threat Entropy");
            }
        };
        spark2.setBackground(CustomUITheme.PANEL_BG_LIGHT);

        JButton btnPushVal = CustomUITheme.createStyledButton("Push Random Value", CustomUITheme.ACCENT_CYAN);
        btnPushVal.addActionListener(e -> {
            sparkData1.add(20 + Math.random() * 60); if (sparkData1.size() > 30) sparkData1.remove(0);
            sparkData2.add(10 + Math.random() * 80); if (sparkData2.size() > 30) sparkData2.remove(0);
            spark1.repaint(); spark2.repaint();
            logEvent("Sparkline", "Pushed random telemetry data point");
        });
        sparkCard.add(spark1);
        sparkCard.add(spark2);
        pMisc.add(sparkCard);

        // Collapsible section: simple toggle-show/hide
        JPanel collCard = CustomUITheme.createCardPanel("Collapsible Section Panel");
        collCard.setLayout(new BorderLayout(0, 6));
        JPanel innerContent = new JPanel(new GridLayout(2, 1, 4, 4));
        innerContent.setOpaque(false);
        JLabel l1 = new JLabel("This section can collapse or expand dynamically.");
        JLabel l2 = new JLabel("Allows clean SOC operational density without cluttering viewports.");
        l1.setFont(CustomUITheme.FONT_BODY); l1.setForeground(CustomUITheme.TEXT_PRIMARY);
        l2.setFont(CustomUITheme.FONT_BODY); l2.setForeground(CustomUITheme.TEXT_MUTED);
        innerContent.add(l1); innerContent.add(l2);
        JButton btnToggle = CustomUITheme.createStyledButton("▼  Collapse / Expand", CustomUITheme.ACCENT_BLUE);
        btnToggle.addActionListener(e -> {
            innerContent.setVisible(!innerContent.isVisible());
            btnToggle.setText(innerContent.isVisible() ? "▼  Collapse / Expand" : "▶  Collapse / Expand");
        });
        collCard.add(btnToggle, BorderLayout.NORTH);
        collCard.add(innerContent, BorderLayout.CENTER);
        pMisc.add(collCard);

        customTabs.addTab("Sparkline & Collapsible", pMisc);

        panel.add(customTabs, BorderLayout.CENTER);
        return panel;
    }

    /** Helper: draws a simple sparkline line chart using Java2D. */
    private void drawSparkline(Graphics2D g2, java.util.List<Double> data, Color color, String label) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Use component size via clip bounds
        java.awt.Rectangle clip = g2.getClipBounds();
        if (clip == null || data.size() < 2) return;
        int cw = clip.width; int ch = clip.height;
        int pad = 4;
        g2.setColor(CustomUITheme.TEXT_MUTED);
        g2.setFont(CustomUITheme.FONT_SMALL);
        g2.drawString(label, pad + 4, 14);
        g2.setColor(color);
        g2.setStroke(new BasicStroke(1.5f));
        for (int i = 1; i < data.size(); i++) {
            int x1 = pad + (int)((double)(i - 1) / (data.size() - 1) * (cw - 2 * pad));
            int y1 = pad + ch - (int)(data.get(i - 1) / 100.0 * (ch - 2 * pad));
            int x2 = pad + (int)((double) i       / (data.size() - 1) * (cw - 2 * pad));
            int y2 = pad + ch - (int)(data.get(i)     / 100.0 * (ch - 2 * pad));
            g2.drawLine(x1, y1, x2, y2);
        }
    }

    // =========================================================================
    // BOTTOM: LIVE COMPONENT EVENT INSPECTOR
    // =========================================================================
    private JPanel createEventInspectorPanel() {
        JPanel panel = CustomUITheme.createCardPanel("Live Component Event Inspector (Real-time Event Dispatch Monitor)");
        panel.setLayout(new BorderLayout(0, 6));

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);

        lblEventCount = new JLabel("Events Captured: 0");
        lblEventCount.setFont(CustomUITheme.FONT_BODY_BOLD);
        lblEventCount.setForeground(CustomUITheme.ACCENT_CYAN);

        JButton btnClearLog = CustomUITheme.createStyledButton("Clear Event Log", CustomUITheme.BORDER_COLOR);
        btnClearLog.setFont(CustomUITheme.FONT_SMALL);
        btnClearLog.addActionListener(e -> {
            txtEventInspector.setText("");
            eventCount = 0;
            lblEventCount.setText("Events Captured: 0");
        });

        topBar.add(lblEventCount, BorderLayout.WEST);
        topBar.add(btnClearLog, BorderLayout.EAST);
        panel.add(topBar, BorderLayout.NORTH);

        txtEventInspector = new JTextArea();
        txtEventInspector.setEditable(false);
        txtEventInspector.setBackground(CustomUITheme.PANEL_BG_LIGHT);
        txtEventInspector.setForeground(CustomUITheme.HEALTHY_GREEN);
        txtEventInspector.setFont(CustomUITheme.FONT_MONO);
        txtEventInspector.setRows(6);

        JScrollPane scroll = new JScrollPane(txtEventInspector);
        scroll.setBorder(new LineBorder(CustomUITheme.BORDER_COLOR, 1));
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private void logEvent(String componentType, String details) {
        eventCount++;
        lblEventCount.setText("Events Captured: " + eventCount);
        String timestamp = new SimpleDateFormat("HH:mm:ss.SSS").format(new Date());
        String line = String.format("[%s] <%s> %s\n", timestamp, componentType, details);
        txtEventInspector.append(line);
        txtEventInspector.setCaretPosition(txtEventInspector.getDocument().getLength());
    }

    // Styling Helpers
    private void styleToggle(JToggleButton btn, Color activeColor) {
        btn.setFont(CustomUITheme.FONT_BODY_BOLD);
        btn.setForeground(Color.WHITE);
        btn.setBackground(activeColor);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(CustomUITheme.BORDER_COLOR, 1, true),
                new EmptyBorder(8, 14, 8, 14)
        ));
    }

    private void styleCheckBox(JCheckBox chk) {
        chk.setFont(CustomUITheme.FONT_BODY);
        chk.setForeground(CustomUITheme.TEXT_PRIMARY);
        chk.setBackground(CustomUITheme.PANEL_BG);
        chk.setFocusPainted(false);
    }

    private void styleRadioButton(JRadioButton rb) {
        rb.setFont(CustomUITheme.FONT_BODY);
        rb.setForeground(CustomUITheme.TEXT_PRIMARY);
        rb.setBackground(CustomUITheme.PANEL_BG);
        rb.setFocusPainted(false);
    }

    private void styleTextField(JTextField txt) {
        txt.setBackground(CustomUITheme.PANEL_BG_LIGHT);
        txt.setForeground(CustomUITheme.TEXT_PRIMARY);
        txt.setCaretColor(CustomUITheme.ACCENT_CYAN);
        txt.setFont(CustomUITheme.FONT_BODY);
        txt.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(CustomUITheme.BORDER_COLOR, 1, true),
                new EmptyBorder(4, 6, 4, 6)
        ));
    }
}
