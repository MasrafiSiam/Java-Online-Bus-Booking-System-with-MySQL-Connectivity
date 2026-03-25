package util;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class Theme {

    // ─── COLOUR PALETTE ────────────────────────────────────────────────────────
    public static final Color BG_DARK       = new Color(10, 14, 23);
    public static final Color BG_CARD       = new Color(18, 24, 38);
    public static final Color BG_CARD2      = new Color(24, 32, 50);
    public static final Color ACCENT        = new Color(99, 179, 237);   // sky blue
    public static final Color ACCENT_HOVER  = new Color(144, 205, 244);
    public static final Color ACCENT_DARK   = new Color(44, 82, 130);
    public static final Color SUCCESS       = new Color(72, 199, 142);
    public static final Color DANGER        = new Color(252, 100, 100);
    public static final Color WARN          = new Color(251, 211, 141);
    public static final Color TEXT_PRIMARY  = new Color(226, 232, 240);
    public static final Color TEXT_MUTED    = new Color(113, 128, 150);
    public static final Color BORDER        = new Color(45, 55, 72);

    // ─── FONTS ─────────────────────────────────────────────────────────────────
    public static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD,  28);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD,  16);
    public static final Font FONT_BODY   = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL  = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_BOLD   = new Font("Segoe UI", Font.BOLD,  13);
    public static final Font FONT_MONO   = new Font("Consolas", Font.BOLD,  13);

    // ─── BUTTON FACTORY ────────────────────────────────────────────────────────
    public static JButton primaryButton(String text) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(ACCENT_DARK);
                } else if (getModel().isRollover()) {
                    g2.setColor(ACCENT_HOVER);
                } else {
                    g2.setColor(ACCENT);
                }
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.setColor(BG_DARK);
                g2.setFont(FONT_BOLD);
                FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth() - fm.stringWidth(getText())) / 2;
                int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), tx, ty);
                g2.dispose();
            }
        };
        styleRawButton(b);
        return b;
    }

    public static JButton secondaryButton(String text) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? BG_CARD2 : BG_CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.setColor(BORDER);
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 10, 10));
                g2.setColor(TEXT_PRIMARY);
                g2.setFont(FONT_BOLD);
                FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth() - fm.stringWidth(getText())) / 2;
                int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), tx, ty);
                g2.dispose();
            }
        };
        styleRawButton(b);
        return b;
    }

    public static JButton dangerButton(String text) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(220,60,60) : DANGER);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.setColor(Color.WHITE);
                g2.setFont(FONT_BOLD);
                FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth() - fm.stringWidth(getText())) / 2;
                int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), tx, ty);
                g2.dispose();
            }
        };
        styleRawButton(b);
        return b;
    }

    private static void styleRawButton(JButton b) {
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setOpaque(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setFont(FONT_BOLD);
    }

    // ─── FIELD FACTORY ─────────────────────────────────────────────────────────
    public static JTextField styledField(String placeholder) {
        JTextField f = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD2);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.setColor(isFocusOwner() ? ACCENT : BORDER);
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 8, 8));
                super.paintComponent(g);
                g2.dispose();
            }
        };
        f.setOpaque(false);
        f.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        f.setForeground(TEXT_PRIMARY);
        f.setCaretColor(ACCENT);
        f.setFont(FONT_BODY);
        f.setBackground(BG_CARD2);
        return f;
    }

    public static JPasswordField styledPassField() {
        JPasswordField f = new JPasswordField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD2);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.setColor(isFocusOwner() ? ACCENT : BORDER);
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, 8, 8));
                super.paintComponent(g);
                g2.dispose();
            }
        };
        f.setOpaque(false);
        f.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        f.setForeground(TEXT_PRIMARY);
        f.setCaretColor(ACCENT);
        f.setFont(FONT_BODY);
        f.setBackground(BG_CARD2);
        return f;
    }

    public static JComboBox<String> styledCombo(String[] items) {
        JComboBox<String> c = new JComboBox<>(items);
        c.setBackground(BG_CARD2);
        c.setForeground(TEXT_PRIMARY);
        c.setFont(FONT_BODY);
        c.setBorder(BorderFactory.createLineBorder(BORDER));
        c.setFocusable(false);
        return c;
    }

    // ─── LABEL FACTORY ─────────────────────────────────────────────────────────
    public static JLabel label(String text, Color color, Font font) {
        JLabel l = new JLabel(text);
        l.setForeground(color);
        l.setFont(font);
        return l;
    }

    // ─── CARD PANEL ────────────────────────────────────────────────────────────
    public static JPanel card(int arc) {
        return new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), arc, arc));
                g2.setColor(BORDER);
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth()-1, getHeight()-1, arc, arc));
                g2.dispose();
            }
            @Override public boolean isOpaque() { return false; }
        };
    }

    // ─── TABLE STYLING ─────────────────────────────────────────────────────────
    public static void styleTable(JTable t) {
        t.setBackground(BG_CARD);
        t.setForeground(TEXT_PRIMARY);
        t.setGridColor(BORDER);
        t.setFont(FONT_BODY);
        t.setRowHeight(36);
        t.setSelectionBackground(ACCENT_DARK);
        t.setSelectionForeground(Color.WHITE);
        t.setShowHorizontalLines(true);
        t.setShowVerticalLines(false);
        t.setIntercellSpacing(new Dimension(0, 1));
        t.getTableHeader().setBackground(BG_CARD2);
        t.getTableHeader().setForeground(TEXT_MUTED);
        t.getTableHeader().setFont(FONT_BOLD);
        t.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));
        t.setFillsViewportHeight(true);
    }

    public static JScrollPane styledScroll(JTable t) {
        JScrollPane sp = new JScrollPane(t);
        sp.setBackground(BG_CARD);
        sp.getViewport().setBackground(BG_CARD);
        sp.setBorder(BorderFactory.createLineBorder(BORDER));
        return sp;
    }

    // ─── DIALOG ────────────────────────────────────────────────────────────────
    public static void showInfo(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Information",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showError(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    public static boolean confirm(Component parent, String msg) {
        return JOptionPane.showConfirmDialog(parent, msg, "Confirm",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    // ─── GLOBAL DEFAULTS ───────────────────────────────────────────────────────
    public static void applyGlobals() {
        UIManager.put("OptionPane.background",           BG_CARD);
        UIManager.put("Panel.background",               BG_CARD);
        UIManager.put("OptionPane.messageForeground",   TEXT_PRIMARY);
        UIManager.put("Button.background",              BG_CARD2);
        UIManager.put("Button.foreground",              TEXT_PRIMARY);
    }
}
