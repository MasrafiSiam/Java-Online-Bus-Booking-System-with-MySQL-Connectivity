package view;

import util.Theme;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/** Base dark-background JFrame used by every screen. */
public class BaseFrame extends JFrame {

    protected JPanel root;

    public BaseFrame(String title, int w, int h) {
        setTitle("BTRS — " + title);
        setSize(w, h);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setUndecorated(false);

        root = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // subtle gradient background
                GradientPaint gp = new GradientPaint(
                        0, 0,          new Color(8, 12, 22),
                        getWidth(), getHeight(), new Color(15, 22, 40));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // faint grid dots for texture
                g2.setColor(new Color(255,255,255,8));
                for (int x = 0; x < getWidth(); x += 30)
                    for (int y = 0; y < getHeight(); y += 30)
                        g2.fillOval(x, y, 2, 2);
                g2.dispose();
            }
        };
        root.setLayout(null);
        setContentPane(root);
        Theme.applyGlobals();
    }

    /** Add a decorative horizontal rule under a section header. */
    protected void addRule(int x, int y, int w) {
        JPanel rule = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(0, 0, Theme.ACCENT,
                        getWidth(), 0, new Color(Theme.ACCENT.getRed(), Theme.ACCENT.getGreen(), Theme.ACCENT.getBlue(), 0));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        rule.setOpaque(false);
        rule.setBounds(x, y, w, 2);
        root.add(rule);
    }

    /** Branded logo / title block. */
    protected void addBrandHeader(int y) {
        JLabel brand = new JLabel("🚌  BTRS");
        brand.setForeground(Theme.ACCENT);
        brand.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
        brand.setBounds(30, y, 200, 30);
        root.add(brand);

        JLabel tagline = new JLabel("Bus Ticket Reservation System");
        tagline.setForeground(Theme.TEXT_MUTED);
        tagline.setFont(Theme.FONT_SMALL);
        tagline.setBounds(30, y + 22, 250, 20);
        root.add(tagline);
    }

    /** Convenience: place a label. */
    protected JLabel addLabel(String text, int x, int y, int w, int h,
                              Color color, java.awt.Font font) {
        JLabel l = new JLabel(text);
        l.setForeground(color);
        l.setFont(font);
        l.setBounds(x, y, w, h);
        root.add(l);
        return l;
    }
}
