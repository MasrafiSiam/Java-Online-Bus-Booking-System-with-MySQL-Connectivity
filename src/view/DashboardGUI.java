package view;

import util.Session;
import util.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class DashboardGUI extends BaseFrame {

    private JPanel contentArea;

    public DashboardGUI() {
        super("Dashboard", 1100, 720);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        buildUI();
        setVisible(true);
    }

    private void buildUI() {

        // ── SIDEBAR ────────────────────────────────────────────────────────
        JPanel sidebar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(12, 18, 32));
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Right border
                g2.setColor(Theme.BORDER);
                g2.fillRect(getWidth()-1, 0, 1, getHeight());
                g2.dispose();
            }
        };
        sidebar.setLayout(null);
        sidebar.setBounds(0, 0, 220, 720);
        sidebar.setOpaque(false);
        root.add(sidebar);

        // Brand
        JLabel brandIcon = new JLabel("🚌");
        brandIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        brandIcon.setBounds(20, 24, 40, 36);
        sidebar.add(brandIcon);

        JLabel brandName = new JLabel("BTRS");
        brandName.setForeground(Theme.ACCENT);
        brandName.setFont(new Font("Segoe UI", Font.BOLD, 20));
        brandName.setBounds(58, 30, 120, 26);
        sidebar.add(brandName);

        // Divider
        JPanel div = new JPanel(); div.setBackground(Theme.BORDER);
        div.setBounds(0, 74, 220, 1);
        sidebar.add(div);

        // User info
        JLabel userIcon = new JLabel("👤");
        userIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        userIcon.setBounds(18, 90, 40, 40);
        sidebar.add(userIcon);

        JLabel userName = new JLabel(Session.userName.isEmpty() ? "User" : Session.userName);
        userName.setForeground(Theme.TEXT_PRIMARY);
        userName.setFont(Theme.FONT_BOLD);
        userName.setBounds(58, 90, 150, 20);
        sidebar.add(userName);

        JLabel userEmail = new JLabel(Session.email);
        userEmail.setForeground(Theme.TEXT_MUTED);
        userEmail.setFont(Theme.FONT_SMALL);
        userEmail.setBounds(58, 110, 150, 18);
        sidebar.add(userEmail);

        JPanel div2 = new JPanel(); div2.setBackground(Theme.BORDER);
        div2.setBounds(0, 140, 220, 1);
        sidebar.add(div2);

        // Nav label
        JLabel navLabel = Theme.label("NAVIGATION", Theme.TEXT_MUTED,
                new Font("Segoe UI", Font.BOLD, 10));
        navLabel.setBounds(20, 158, 180, 20);
        sidebar.add(navLabel);

        // Nav items
        String[][] navItems = {
            {"🏠", "Dashboard"},
            {"🔍", "Search Buses"},
            {"🎟️", "My Bookings"},
        };

        int ny = 182;
        JButton[] navBtns = new JButton[navItems.length];
        for (int i = 0; i < navItems.length; i++) {
            final int idx = i;
            String emoji = navItems[i][0];
            String label = navItems[i][1];

            navBtns[i] = new JButton(emoji + "  " + label) {
                boolean active = (idx == 0);
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    if (active || getModel().isRollover()) {
                        g2.setColor(active ? new Color(99,179,237,30) : new Color(255,255,255,8));
                        g2.fillRoundRect(10, 2, getWidth()-20, getHeight()-4, 8, 8);
                        if (active) {
                            g2.setColor(Theme.ACCENT);
                            g2.fillRoundRect(0, 2, 3, getHeight()-4, 2, 2);
                        }
                    }
                    g2.setFont(getFont());
                    g2.setColor(active ? Theme.ACCENT : Theme.TEXT_MUTED);
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(getText(), 22, (getHeight()+fm.getAscent()-fm.getDescent())/2);
                    g2.dispose();
                }
                void setActive(boolean a) { active = a; repaint(); }
            };
            navBtns[i].setFont(Theme.FONT_BODY);
            navBtns[i].setContentAreaFilled(false);
            navBtns[i].setBorderPainted(false);
            navBtns[i].setFocusPainted(false);
            navBtns[i].setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            navBtns[i].setHorizontalAlignment(SwingConstants.LEFT);
            navBtns[i].setBounds(0, ny, 220, 44);
            sidebar.add(navBtns[i]);
            ny += 46;
        }

        // Logout at bottom
        JPanel div3 = new JPanel(); div3.setBackground(Theme.BORDER);
        div3.setBounds(0, 660, 220, 1);
        sidebar.add(div3);

        JButton logout = Theme.dangerButton("⏻  Logout");
        logout.setBounds(20, 670, 180, 36);
        logout.setHorizontalAlignment(SwingConstants.LEFT);
        sidebar.add(logout);

        // ── MAIN CONTENT AREA ──────────────────────────────────────────────
        contentArea = new JPanel(null) {
            @Override public boolean isOpaque() { return false; }
        };
        contentArea.setBounds(220, 0, 880, 720);
        root.add(contentArea);

        // Top bar
        JPanel topbar = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(12,18,32,200));
                g2.fillRect(0,0,getWidth(),getHeight());
                g2.setColor(Theme.BORDER);
                g2.fillRect(0, getHeight()-1, getWidth(), 1);
                g2.dispose();
            }
        };
        topbar.setOpaque(false);
        topbar.setBounds(0, 0, 880, 58);
        contentArea.add(topbar);

        JLabel pageTitle = Theme.label("Dashboard", Theme.TEXT_PRIMARY, Theme.FONT_HEADER);
        pageTitle.setBounds(24, 18, 300, 24);
        topbar.add(pageTitle);

        JLabel dateLabel = Theme.label(
                java.time.LocalDate.now().toString(), Theme.TEXT_MUTED, Theme.FONT_SMALL);
        dateLabel.setBounds(700, 20, 160, 20);
        topbar.add(dateLabel);

        // ── DEFAULT: show home panel ───────────────────────────────────────
        showHomePanel(pageTitle);

        // Nav actions
        navBtns[0].addActionListener(e -> {
            for (JButton b : navBtns) ((Object)b).getClass(); // just repaint
            showHomePanel(pageTitle);
            pageTitle.setText("Dashboard");
        });
        navBtns[1].addActionListener(e -> {
            showSearchPanel(pageTitle);
            pageTitle.setText("Search Buses");
        });
        navBtns[2].addActionListener(e -> {
            showMyBookingsPanel(pageTitle);
            pageTitle.setText("My Bookings");
        });

        logout.addActionListener(e -> {
            if (Theme.confirm(this, "Are you sure you want to logout?")) {
                Session.clear();
                new LoginGUI();
                dispose();
            }
        });
    }

    private void clearContent() {
        // Remove everything below top bar (y >= 58)
        for (Component c : contentArea.getComponents()) {
            if (c.getY() >= 58) contentArea.remove(c);
        }
        contentArea.repaint();
    }

    // ── HOME PANEL ────────────────────────────────────────────────────────
    private void showHomePanel(JLabel pageTitle) {
        pageTitle.setText("Dashboard");
        clearContent();

        int cx = 24, cy = 75;

        // Welcome banner
        JPanel banner = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(30,60,100),
                        getWidth(), 0, new Color(20, 40, 80));
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));
                g2.setColor(new Color(99,179,237,25));
                g2.fillOval(500, -40, 220, 220);
                g2.dispose();
            }
        };
        banner.setOpaque(false);
        banner.setBounds(cx, cy, 830, 110);
        contentArea.add(banner);

        JLabel hi = new JLabel("Good to see you, " + Session.userName + "! 👋");
        hi.setForeground(Theme.TEXT_PRIMARY);
        hi.setFont(new Font("Segoe UI", Font.BOLD, 20));
        hi.setBounds(24, 20, 600, 30);
        banner.add(hi);

        JLabel sub = new JLabel("Where would you like to travel today?");
        sub.setForeground(new Color(160, 200, 240));
        sub.setFont(Theme.FONT_BODY);
        sub.setBounds(24, 52, 400, 24);
        banner.add(sub);

        JButton quickSearch = Theme.primaryButton("Search Buses →");
        quickSearch.setBounds(24, 76, 180, 20);
        banner.add(quickSearch);

        cy += 126;

        // ── Quick action cards ───────────────────────────────────────────
        JLabel actionLabel = Theme.label("QUICK ACTIONS", Theme.TEXT_MUTED,
                new Font("Segoe UI", Font.BOLD, 10));
        actionLabel.setBounds(cx, cy, 200, 20);
        contentArea.add(actionLabel);
        cy += 26;

        String[][] cards = {
            {"🔍", "Search Buses", "Find available routes"},
            {"🎟️", "My Bookings", "View your tickets"},
            {"ℹ️", "Help & Info", "FAQ and support"},
        };

        int cardX = cx;
        for (String[] card : cards) {
            JPanel c = makeQuickCard(card[0], card[1], card[2]);
            c.setBounds(cardX, cy, 264, 100);
            contentArea.add(c);
            cardX += 278;
        }

        // Actions
        quickSearch.addActionListener(e -> showSearchPanel(new JLabel()));
        contentArea.revalidate();
        contentArea.repaint();
    }

    private JPanel makeQuickCard(String emoji, String title, String sub) {
        JPanel card = Theme.card(12);
        card.setLayout(null);

        JLabel e = new JLabel(emoji);
        e.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        e.setBounds(16, 16, 44, 36);
        card.add(e);

        JLabel t = Theme.label(title, Theme.TEXT_PRIMARY, Theme.FONT_BOLD);
        t.setBounds(66, 22, 190, 22);
        card.add(t);

        JLabel s = Theme.label(sub, Theme.TEXT_MUTED, Theme.FONT_SMALL);
        s.setBounds(66, 44, 190, 18);
        card.add(s);

        return card;
    }

    // ── SEARCH PANEL ─────────────────────────────────────────────────────
    private void showSearchPanel(JLabel pageTitle) {
        clearContent();

        // Embed SearchBusPanel directly
        SearchBusPanel panel = new SearchBusPanel();
        panel.setBounds(0, 58, 880, 662);
        contentArea.add(panel);
        contentArea.revalidate();
        contentArea.repaint();
    }

    // ── MY BOOKINGS PANEL ─────────────────────────────────────────────────
    private void showMyBookingsPanel(JLabel pageTitle) {
        clearContent();

        MyBookingsPanel panel = new MyBookingsPanel();
        panel.setBounds(0, 58, 880, 662);
        contentArea.add(panel);
        contentArea.revalidate();
        contentArea.repaint();
    }
}
