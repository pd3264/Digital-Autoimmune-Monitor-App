package digitalautomonitor.ui.dialogs;

import digitalautomonitor.enums.RiskLevel;
import digitalautomonitor.model.User;
import digitalautomonitor.service.BehaviourMonitorService;
import digitalautomonitor.ui.components.CustomUITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDateTime;

/**
 * Modal dialog for registering a new user identity into the digital immune system.
 */
public class AddUserDialog extends JDialog {

    private final BehaviourMonitorService monitorService;
    private JTextField txtUsername;
    private JComboBox<String> comboRole;
    private JComboBox<String> comboDepartment;
    private JCheckBox chkMonitoring;
    private JSpinner spinnerThreshold;

    public AddUserDialog(Frame owner, BehaviourMonitorService monitorService) {
        super(owner, "Register New Monitored Entity", true);
        this.monitorService = monitorService;

        setSize(480, 400);
        setLocationRelativeTo(owner);
        getContentPane().setBackground(CustomUITheme.BG_DARK);
        setLayout(new BorderLayout(10, 10));

        initUI();
    }

    private void initUI() {
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(CustomUITheme.PANEL_BG);
        headerPanel.setBorder(new EmptyBorder(12, 16, 12, 16));

        JLabel title = new JLabel("REGISTER USER PROFILE & BASELINE");
        title.setFont(CustomUITheme.FONT_TITLE);
        title.setForeground(CustomUITheme.ACCENT_CYAN);
        headerPanel.add(title, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(CustomUITheme.BG_DARK);
        formPanel.setBorder(new EmptyBorder(16, 20, 16, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Username
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.35;
        JLabel lblUser = new JLabel("Username:");
        lblUser.setFont(CustomUITheme.FONT_BODY_BOLD);
        lblUser.setForeground(CustomUITheme.TEXT_PRIMARY);
        formPanel.add(lblUser, gbc);

        gbc.gridx = 1; gbc.weightx = 0.65;
        txtUsername = new JTextField(15);
        txtUsername.setBackground(CustomUITheme.PANEL_BG);
        txtUsername.setForeground(CustomUITheme.TEXT_PRIMARY);
        txtUsername.setCaretColor(CustomUITheme.ACCENT_CYAN);
        txtUsername.setFont(CustomUITheme.FONT_BODY);
        formPanel.add(txtUsername, gbc);
        row++;

        // Role
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lblRole = new JLabel("User Role:");
        lblRole.setFont(CustomUITheme.FONT_BODY_BOLD);
        lblRole.setForeground(CustomUITheme.TEXT_PRIMARY);
        formPanel.add(lblRole, gbc);

        gbc.gridx = 1;
        String[] roles = {"SecOps Analyst", "Software Engineer", "DevOps Engineer", "Database Admin", "Product Manager", "Data Scientist", "Executive", "External Contractor"};
        comboRole = new JComboBox<>(roles);
        comboRole.setBackground(CustomUITheme.PANEL_BG);
        comboRole.setForeground(CustomUITheme.TEXT_PRIMARY);
        formPanel.add(comboRole, gbc);
        row++;

        // Department
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lblDept = new JLabel("Department:");
        lblDept.setFont(CustomUITheme.FONT_BODY_BOLD);
        lblDept.setForeground(CustomUITheme.TEXT_PRIMARY);
        formPanel.add(lblDept, gbc);

        gbc.gridx = 1;
        String[] depts = {"Security Operations", "Infrastructure", "Engineering", "Data Science", "Corporate Finance", "Human Resources", "External Partner"};
        comboDepartment = new JComboBox<>(depts);
        comboDepartment.setBackground(CustomUITheme.PANEL_BG);
        comboDepartment.setForeground(CustomUITheme.TEXT_PRIMARY);
        formPanel.add(comboDepartment, gbc);
        row++;

        // Risk threshold
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lblThresh = new JLabel("Risk Threshold (1-100):");
        lblThresh.setFont(CustomUITheme.FONT_BODY_BOLD);
        lblThresh.setForeground(CustomUITheme.TEXT_PRIMARY);
        formPanel.add(lblThresh, gbc);

        gbc.gridx = 1;
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(65, 10, 95, 5);
        spinnerThreshold = new JSpinner(spinnerModel);
        spinnerThreshold.setFont(CustomUITheme.FONT_BODY);
        formPanel.add(spinnerThreshold, gbc);
        row++;

        // Monitoring enabled
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lblMon = new JLabel("Auto-Monitoring:");
        lblMon.setFont(CustomUITheme.FONT_BODY_BOLD);
        lblMon.setForeground(CustomUITheme.TEXT_PRIMARY);
        formPanel.add(lblMon, gbc);

        gbc.gridx = 1;
        chkMonitoring = new JCheckBox("Arm Behavioural Watchdog", true);
        chkMonitoring.setBackground(CustomUITheme.BG_DARK);
        chkMonitoring.setForeground(CustomUITheme.HEALTHY_GREEN);
        chkMonitoring.setFont(CustomUITheme.FONT_BODY_BOLD);
        formPanel.add(chkMonitoring, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        buttonPanel.setBackground(CustomUITheme.PANEL_BG);

        JButton btnSave = CustomUITheme.createStyledButton("Register Entity", CustomUITheme.HEALTHY_GREEN);
        JButton btnCancel = CustomUITheme.createStyledButton("Cancel", CustomUITheme.BORDER_COLOR);

        btnSave.addActionListener(e -> saveUser());
        btnCancel.addActionListener(e -> dispose());

        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void saveUser() {
        String username = txtUsername.getText().trim();
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username cannot be empty.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (monitorService.getUserByUsername(username) != null) {
            JOptionPane.showMessageDialog(this, "A monitored entity with username '" + username + "' already exists.",
                    "Duplicate User", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String role = (String) comboRole.getSelectedItem();
        String dept = (String) comboDepartment.getSelectedItem();
        int threshold = (Integer) spinnerThreshold.getValue();
        boolean monitoring = chkMonitoring.isSelected();

        String userId = "USR-" + (monitorService.getAllUsers().size() + 1);
        User newUser = new User(userId, username, role, dept, 90, RiskLevel.NORMAL,
                LocalDateTime.now(), monitoring, threshold);

        monitorService.addUser(newUser);
        JOptionPane.showMessageDialog(this, "User " + username + " successfully enrolled in immune surveillance.",
                "Entity Enrolled", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }
}
