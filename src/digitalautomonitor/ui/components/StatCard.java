package digitalautomonitor.ui.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Modern SOC Stat Card displaying metric KPIs with progress indicators.
 */
public class StatCard extends JPanel {

    private final JLabel titleLabel;
    private final JLabel valueLabel;
    private final JLabel subtitleLabel;
    private final JProgressBar progressBar;

    public StatCard(String title, String value, String subtitle, Color accentColor, Integer progressPercent) {
        setLayout(new BorderLayout(6, 6));
        setBackground(CustomUITheme.PANEL_BG);
        setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(CustomUITheme.BORDER_COLOR, 1, true),
                new EmptyBorder(12, 14, 12, 14)
        ));

        // Top title
        titleLabel = new JLabel(title.toUpperCase());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        titleLabel.setForeground(CustomUITheme.TEXT_MUTED);

        // Center value
        valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        valueLabel.setForeground(accentColor != null ? accentColor : CustomUITheme.TEXT_PRIMARY);

        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        centerPanel.setOpaque(false);
        centerPanel.add(titleLabel);
        centerPanel.add(valueLabel);
        add(centerPanel, BorderLayout.CENTER);

        // Bottom section with optional progress bar or subtitle
        JPanel bottomPanel = new JPanel(new BorderLayout(0, 4));
        bottomPanel.setOpaque(false);

        if (progressPercent != null) {
            progressBar = new JProgressBar(0, 100);
            progressBar.setValue(progressPercent);
            progressBar.setPreferredSize(new Dimension(100, 6));
            CustomUITheme.styleProgressBar(progressBar, accentColor != null ? accentColor : CustomUITheme.ACCENT_CYAN);
            progressBar.setStringPainted(false);
            bottomPanel.add(progressBar, BorderLayout.NORTH);
        } else {
            progressBar = null;
        }

        subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(CustomUITheme.FONT_SMALL);
        subtitleLabel.setForeground(CustomUITheme.TEXT_MUTED);
        bottomPanel.add(subtitleLabel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void setValue(String value) {
        valueLabel.setText(value);
    }

    public void setSubtitle(String subtitle) {
        subtitleLabel.setText(subtitle);
    }

    public void setProgress(int percent) {
        if (progressBar != null) {
            progressBar.setValue(percent);
        }
    }
}
