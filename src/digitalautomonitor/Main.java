package digitalautomonitor;

import digitalautomonitor.service.AlertService;
import digitalautomonitor.service.BehaviourMonitorService;
import digitalautomonitor.service.EventLogService;
import digitalautomonitor.service.ReportService;
import digitalautomonitor.service.SimulationService;
import digitalautomonitor.ui.LoginFrame;
import digitalautomonitor.ui.MainFrame;

import javax.swing.*;

/**
 * Application entry point.
 *
 * Responsibilities:
 *  1. Set up the cross-platform Swing look and feel.
 *  2. Create all service objects (the application's logic layer).
 *  3. Launch the login screen or dashboard on the Swing EDT.
 *
 * Supported command-line arguments:
 *   --version / -v        Print version and exit.
 *   --dashboard           Skip login and open dashboard directly (for testing).
 *   --auto-login          Same as --dashboard.
 */
public class Main {

    public static void main(String[] args) {
        // Use the cross-platform look and feel so the UI looks consistent on all OS
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        // Handle --version flag
        if (args.length > 0 && (args[0].equalsIgnoreCase("--version") || args[0].equalsIgnoreCase("-v"))) {
            System.out.println("Digital Autoimmune Monitor v1.0.0");
            System.out.println("Behaviour-Based Anomaly Detection & Security Monitoring Platform");
            return;
        }

        // Decide whether to skip the login screen
        boolean bypassLogin = args.length > 0 && (
                args[0].equalsIgnoreCase("--dashboard") ||
                args[0].equalsIgnoreCase("--auto-login")
        );

        // ---------------------------------------------------------------
        //  Create the service layer (Logic Layer)
        //  All business logic and data live here — no database required.
        // ---------------------------------------------------------------
        EventLogService        eventLogService = new EventLogService();
        AlertService           alertService    = new AlertService();
        BehaviourMonitorService monitorService  = new BehaviourMonitorService();
        ReportService          reportService   = new ReportService();

        // SimulationService ties all services together and drives the
        // background event generation timer (fires every 4 seconds).
        SimulationService simulationService = new SimulationService(
                eventLogService, alertService, monitorService
        );

        // ---------------------------------------------------------------
        //  Launch the UI on the Swing Event Dispatch Thread (EDT).
        //  All Swing components MUST be created and updated on the EDT.
        // ---------------------------------------------------------------
        SwingUtilities.invokeLater(() -> {
            try {
                if (bypassLogin) {
                    // Open the dashboard directly (useful for quick testing)
                    new MainFrame(simulationService, reportService).setVisible(true);
                } else {
                    // Show the login screen first
                    new LoginFrame(simulationService, reportService).setVisible(true);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Fatal startup error: " + ex.getMessage(),
                        "System Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
