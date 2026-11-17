package digitalautomonitor.ui.components;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

/**
 * Central design tokens and helper styling methods for the dark cybersecurity SOC theme.
 */
public class CustomUITheme {

    public static final Color BG_DARK = new Color(11, 19, 43);         // #0B132B Dark Navy/Black
    public static final Color BG_HEADER = new Color(15, 23, 42);       // #0F172A
    public static final Color PANEL_BG = new Color(22, 32, 50);        // #162032 Slate Navy
    public static final Color PANEL_BG_LIGHT = new Color(30, 41, 59);  // #1E293B
    public static final Color ACCENT_CYAN = new Color(0, 240, 255);    // #00F0FF Neon Cyan
    public static final Color ACCENT_BLUE = new Color(56, 189, 248);   // #38BDF8 Light Blue
    public static final Color HEALTHY_GREEN = new Color(16, 185, 129); // #10B981 Emerald
    public static final Color WARNING_YELLOW = new Color(245, 158, 11);// #F59E0B Amber
    public static final Color WARNING_ORANGE = new Color(249, 115, 22);// #F97316 Orange
    public static final Color CRITICAL_RED = new Color(239, 68, 68);   // #EF4444 Crimson
    public static final Color TEXT_PRIMARY = new Color(248, 250, 252); // #F8FAFC Pure Light
    public static final Color TEXT_MUTED = new Color(148, 163, 184);   // #94A3B8 Muted Slate
    public static final Color BORDER_COLOR = new Color(39, 51, 74);    // Subtle border
    public static final Color HOVER_COLOR = new Color(40, 56, 84);

    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_BODY_BOLD = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_METRIC = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font FONT_MONO = new Font("Consolas", Font.PLAIN, 12);

    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(PANEL_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(12, 12, 12, 12)
        ));
        return panel;
    }

    public static JPanel createCardPanel(String title) {
        JPanel panel = new JPanel();
        panel.setBackground(PANEL_BG);
        TitledBorder titledBorder = BorderFactory.createTitledBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                " " + title + " ",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                FONT_SUBTITLE,
                ACCENT_CYAN
        );
        panel.setBorder(BorderFactory.createCompoundBorder(
                titledBorder,
                new EmptyBorder(8, 10, 10, 10)
        ));
        return panel;
    }

    public static JButton createStyledButton(String text, Color accentColor) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BODY_BOLD);
        btn.setForeground(TEXT_PRIMARY);
        btn.setBackground(PANEL_BG_LIGHT);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(accentColor != null ? accentColor : BORDER_COLOR, 1, true),
                new EmptyBorder(6, 14, 6, 14)
        ));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(HOVER_COLOR);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(PANEL_BG_LIGHT);
            }
        });
        return btn;
    }

    public static void styleTable(JTable table) {
        table.setBackground(PANEL_BG);
        table.setForeground(TEXT_PRIMARY);
        table.setFont(FONT_BODY);
        table.setRowHeight(28);
        table.setGridColor(BORDER_COLOR);
        table.setSelectionBackground(new Color(38, 70, 110));
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);

        JTableHeader header = table.getTableHeader();
        header.setBackground(BG_HEADER);
        header.setForeground(ACCENT_CYAN);
        header.setFont(FONT_SUBTITLE);
        header.setBorder(new LineBorder(BORDER_COLOR, 1));
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        centerRenderer.setBackground(PANEL_BG);
        centerRenderer.setForeground(TEXT_PRIMARY);
    }

    public static void styleProgressBar(JProgressBar bar, Color fg) {
        bar.setBackground(PANEL_BG_LIGHT);
        bar.setForeground(fg);
        bar.setBorder(new LineBorder(BORDER_COLOR, 1, true));
        bar.setStringPainted(true);
        bar.setFont(FONT_SMALL);
    }
}
