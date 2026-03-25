package view;

import model.DBConnection;
import util.Session;
import util.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.*;

public class SeatBookingGUI extends BaseFrame {

    private final int busId;
    private final JButton[] seatBtns = new JButton[40];
    private int selectedSeat = -1;
    private JLabel selectedLabel;
    private JButton bookBtn;

    public SeatBookingGUI(int busId, String busName, String from, String to) {
        super("Seat Booking", 860, 720);
        this.busId = busId;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        buildUI(busName, from, to);
        setVisible(true);
    }

    private void buildUI(String busName, String from, String to) {
        int px = 30;

        // ── Header ────────────────────────────────────────────────────────
        JLabel title = Theme.label("SELECT YOUR SEAT", Theme.ACCENT, Theme.FONT_TITLE);
        title.setBounds(px, 28, 500, 36);
        root.add(title);

        JPanel infoCard = Theme.card(10);
        infoCard.setLayout(null);
        infoCard.setBounds(px, 76, 795, 58);
        root.add(infoCard);

        JLabel busL = Theme.label("🚌  " + busName, Theme.TEXT_PRIMARY, Theme.FONT_BOLD);
        busL.setBounds(16, 10, 280, 22);
        infoCard.add(busL);

        JLabel routeL = Theme.label("📍  " + from + "  →  " + to, Theme.TEXT_MUTED, Theme.FONT_BODY);
        routeL.setBounds(16, 30, 400, 20);
        infoCard.add(routeL);

        // Legend
        int lx = 500;
        addLegend(infoCard, lx,   14, Theme.BG_CARD2, Theme.BORDER, "Available");
        addLegend(infoCard, lx+110, 14, Theme.ACCENT,  Theme.ACCENT,  "Selected");
        addLegend(infoCard, lx+220, 14, Theme.DANGER,  Theme.DANGER,  "Booked");

        // ── BUS FRONT ILLUSTRATION ────────────────────────────────────────
        JPanel busFront = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.BG_CARD2);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(Theme.ACCENT);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                g2.setColor(Theme.TEXT_MUTED);
                FontMetrics fm = g2.getFontMetrics();
                String s = "▶  DRIVER'S CABIN  ▶";
                g2.drawString(s, (getWidth()-fm.stringWidth(s))/2, 18);
                g2.dispose();
            }
        };
        busFront.setOpaque(false);
        busFront.setBounds(px + 100, 148, 595, 26);
        root.add(busFront);

        // ── SEAT GRID ─────────────────────────────────────────────────────
        // 2+2 layout: A, B | aisle | C, D
        JPanel seatPanel = new JPanel(null);
        seatPanel.setOpaque(false);
        seatPanel.setBounds(px, 185, 795, 420);
        root.add(seatPanel);

        int seatW = 56, seatH = 44, gapX = 8, gapY = 10;
        int startX = 100;
        int startY = 10;

        // Column headers
        String[] cols = {"A", "B", "", "C", "D"};
        int[] colOffsets = {0, seatW+gapX, 0, seatW*2+gapX*2+30, seatW*3+gapX*3+30};
        for (int c = 0; c < cols.length; c++) {
            if (cols[c].isEmpty()) continue;
            JLabel ch = Theme.label(cols[c], Theme.TEXT_MUTED, Theme.FONT_SMALL);
            ch.setBounds(startX + colOffsets[c] + 18, 0, 30, 20);
            seatPanel.add(ch);
        }

        for (int row = 0; row < 10; row++) {
            // Row number label
            JLabel rowLabel = Theme.label(String.valueOf(row+1), Theme.TEXT_MUTED, Theme.FONT_SMALL);
            rowLabel.setBounds(60, startY + row*(seatH+gapY) + 14, 30, 20);
            seatPanel.add(rowLabel);

            for (int col = 0; col < 4; col++) {
                int seatNo = row * 4 + col + 1;
                final int sn = seatNo;

                int xOffset;
                if (col == 0) xOffset = 0;
                else if (col == 1) xOffset = seatW + gapX;
                else if (col == 2) xOffset = seatW*2 + gapX*2 + 30; // aisle gap
                else               xOffset = seatW*3 + gapX*3 + 30;

                seatBtns[seatNo-1] = new JButton(String.valueOf(seatNo)) {
                    boolean selected = false;
                    @Override protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                        if (!isEnabled()) {
                            // Booked
                            g2.setColor(new Color(180, 60, 60, 180));
                            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                            g2.setColor(Theme.DANGER);
                            g2.setStroke(new BasicStroke(1.5f));
                            g2.draw(new RoundRectangle2D.Float(1, 1, getWidth()-2, getHeight()-2, 8, 8));
                        } else if (selected) {
                            g2.setColor(new Color(99,179,237,200));
                            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                            g2.setColor(Theme.ACCENT);
                            g2.setStroke(new BasicStroke(2f));
                            g2.draw(new RoundRectangle2D.Float(1, 1, getWidth()-2, getHeight()-2, 8, 8));
                        } else {
                            g2.setColor(getModel().isRollover() ? new Color(40,60,90) : Theme.BG_CARD2);
                            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                            g2.setColor(Theme.BORDER);
                            g2.setStroke(new BasicStroke(1f));
                            g2.draw(new RoundRectangle2D.Float(1, 1, getWidth()-2, getHeight()-2, 8, 8));
                        }

                        // Seat back (decorative)
                        g2.setColor(isEnabled() ? (selected ? new Color(60,120,180) : new Color(30,40,60))
                                                : new Color(120,30,30));
                        g2.fillRoundRect(6, 4, getWidth()-12, 8, 4, 4);

                        // Seat number
                        g2.setFont(Theme.FONT_SMALL);
                        g2.setColor(isEnabled() ? (selected ? Color.WHITE : Theme.TEXT_MUTED) : new Color(255,150,150));
                        FontMetrics fm = g2.getFontMetrics();
                        g2.drawString(getText(),
                                (getWidth()-fm.stringWidth(getText()))/2,
                                getHeight()-8);
                        g2.dispose();
                    }
                    void setSelectedState(boolean s) { selected = s; repaint(); }
                };
                seatBtns[seatNo-1].setContentAreaFilled(false);
                seatBtns[seatNo-1].setBorderPainted(false);
                seatBtns[seatNo-1].setFocusPainted(false);
                seatBtns[seatNo-1].setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                seatBtns[seatNo-1].setBounds(startX + xOffset,
                        startY + row * (seatH + gapY), seatW, seatH);
                seatPanel.add(seatBtns[seatNo-1]);

                seatBtns[seatNo-1].addActionListener(e -> selectSeat(sn));
            }
        }

        loadBookedSeats();

        // ── BOOKING FOOTER ────────────────────────────────────────────────
        JPanel footer = Theme.card(12);
        footer.setLayout(null);
        footer.setBounds(px, 618, 795, 72);
        root.add(footer);

        selectedLabel = Theme.label("No seat selected", Theme.TEXT_MUTED, Theme.FONT_BODY);
        selectedLabel.setBounds(20, 22, 400, 28);
        footer.add(selectedLabel);

        bookBtn = Theme.primaryButton("Confirm Booking");
        bookBtn.setBounds(610, 16, 170, 42);
        bookBtn.setEnabled(false);
        footer.add(bookBtn);

        JButton cancelBtn = Theme.secondaryButton("Cancel");
        cancelBtn.setBounds(430, 16, 170, 42);
        footer.add(cancelBtn);

        bookBtn.addActionListener(e -> bookSeat());
        cancelBtn.addActionListener(e -> dispose());
    }

    private void addLegend(JPanel parent, int x, int y, Color bg, Color border, String text) {
        JPanel box = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, 14, 14, 4, 4);
                g2.setColor(border);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, 13, 13, 4, 4);
                g2.dispose();
            }
        };
        box.setOpaque(false);
        box.setBounds(x, y, 14, 14);
        parent.add(box);

        JLabel l = Theme.label(text, Theme.TEXT_MUTED, Theme.FONT_SMALL);
        l.setBounds(x + 18, y - 1, 90, 16);
        parent.add(l);
    }

    private void selectSeat(int seatNo) {
        // Deselect previous
        if (selectedSeat != -1) {
            JButton prev = seatBtns[selectedSeat - 1];
            if (prev instanceof JButton b)
                callSetSelected(b, false);
        }
        selectedSeat = seatNo;
        callSetSelected(seatBtns[seatNo - 1], true);
        selectedLabel.setText("Selected: Seat " + seatNo + "  ✓");
        selectedLabel.setForeground(Theme.ACCENT);
        bookBtn.setEnabled(true);
    }

    @SuppressWarnings("unchecked")
    private void callSetSelected(JButton b, boolean val) {
        try {
            b.getClass().getMethod("setSelectedState", boolean.class).invoke(b, val);
        } catch (Exception e) { b.repaint(); }
    }

    private void loadBookedSeats() {
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "SELECT seat_number FROM bookings WHERE bus_id=?");
            ps.setInt(1, busId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int s = rs.getInt("seat_number");
                if (s >= 1 && s <= 40) {
                    seatBtns[s-1].setEnabled(false);
                }
            }
        } catch (Exception e) {
            Theme.showError(this, "Error loading seats: " + e.getMessage());
        }
    }

    private void bookSeat() {
        if (selectedSeat == -1) {
            Theme.showError(this, "Please select a seat first.");
            return;
        }
        if (!Theme.confirm(this, "Confirm booking for Seat " + selectedSeat + "?")) return;

        try {
            Connection con = DBConnection.getConnection();
            // Double-check
            PreparedStatement check = con.prepareStatement(
                    "SELECT id FROM bookings WHERE bus_id=? AND seat_number=?");
            check.setInt(1, busId);
            check.setInt(2, selectedSeat);
            if (check.executeQuery().next()) {
                Theme.showError(this, "Seat already taken. Please choose another.");
                seatBtns[selectedSeat-1].setEnabled(false);
                selectedSeat = -1;
                selectedLabel.setText("No seat selected");
                selectedLabel.setForeground(Theme.TEXT_MUTED);
                bookBtn.setEnabled(false);
                return;
            }

            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO bookings(user_id, bus_id, seat_number) VALUES(?,?,?)");
            ps.setInt(1, Session.userId);
            ps.setInt(2, busId);
            ps.setInt(3, selectedSeat);
            ps.executeUpdate();

            // Show success dialog
            showSuccessDialog();
        } catch (Exception e) {
            Theme.showError(this, "Booking failed: " + e.getMessage());
        }
    }

    private void showSuccessDialog() {
        JDialog dlg = new JDialog(this, "Booking Confirmed", true);
        dlg.setSize(380, 260);
        dlg.setLocationRelativeTo(this);
        dlg.setUndecorated(false);

        JPanel p = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(Theme.BG_CARD);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        dlg.setContentPane(p);

        JLabel checkIcon = new JLabel("✅");
        checkIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        checkIcon.setBounds(150, 24, 80, 60);
        p.add(checkIcon);

        JLabel title = Theme.label("Booking Confirmed!", Theme.SUCCESS, Theme.FONT_HEADER);
        title.setBounds(80, 88, 220, 28);
        p.add(title);

        JLabel detail = Theme.label("Seat " + selectedSeat + " has been reserved for you.",
                Theme.TEXT_MUTED, Theme.FONT_BODY);
        detail.setBounds(40, 118, 300, 22);
        p.add(detail);

        JButton close = Theme.primaryButton("Great, Thanks!");
        close.setBounds(90, 165, 200, 44);
        p.add(close);

        close.addActionListener(e -> { dlg.dispose(); dispose(); });
        dlg.setVisible(true);
    }
}
