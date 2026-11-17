package digitalautomonitor.ui.components;

import javax.swing.*;
import java.awt.*;

/**
 * Custom-painted circular status indicator LED (Green / Yellow / Red / Cyan).
 */
public class StatusIndicator extends JPanel {

    private Color statusColor;
    private String statusText;
    private boolean pulsing = false;

    public StatusIndicator(Color color, String text) {
        this.statusColor = color;
        this.statusText = text;
        setOpaque(false);
        setLayout(new FlowLayout(FlowLayout.LEFT, 6, 2));
    }

    public void setStatus(Color color, String text) {
        this.statusColor = color;
        this.statusText = text;
        repaint();
    }

    public void setStatusColor(Color color) {
        this.statusColor = color;
        repaint();
    }

    public void setStatusText(String text) {
        this.statusText = text;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int dotSize = 10;
        int y = (getHeight() - dotSize) / 2;

        // Outer glow
        g2.setColor(new Color(statusColor.getRed(), statusColor.getGreen(), statusColor.getBlue(), 60));
        g2.fillOval(2, y - 2, dotSize + 4, dotSize + 4);

        // Core LED
        g2.setColor(statusColor);
        g2.fillOval(4, y, dotSize, dotSize);

        // Text
        if (statusText != null && !statusText.isEmpty()) {
            g2.setFont(CustomUITheme.FONT_BODY_BOLD);
            g2.setColor(CustomUITheme.TEXT_PRIMARY);
            FontMetrics fm = g2.getFontMetrics();
            int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(statusText, dotSize + 12, textY);
        }

        g2.dispose();
    }

    @Override
    public Dimension getPreferredSize() {
        FontMetrics fm = getFontMetrics(CustomUITheme.FONT_BODY_BOLD);
        int textWidth = statusText != null ? fm.stringWidth(statusText) : 0;
        return new Dimension(24 + textWidth + 10, 26);
    }
}
