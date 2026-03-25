package view;

import controller.BookingController;
import model.DBConnection;
import util.Session;
import util.Theme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.sql.*;

public class MyBookingsPanel extends JPanel {

    private DefaultTableModel tableModel;
    private JTable table;
    private JLabel countLabel;

    public MyBookingsPanel() {
        setLayout(null);
        setOpaque(false);
        buildUI();
        loadBookings();
    }

    private void buildUI() {
        int px = 24, py = 18;

        JLabel title = Theme.label("My Bookings", Theme.TEXT_PRIMARY, Theme.FONT_HEADER);
        title.setBounds(px, py, 300, 28);
        add(title);

        countLabel = Theme.label("Loading...", Theme.TEXT_MUTED, Theme.FONT_SMALL);
        countLabel.setBounds(px, py + 30, 400, 20);
        add(countLabel);

        JButton refreshBtn = Theme.secondaryButton("⟳ Refresh");
        refreshBtn.setBounds(700, py, 110, 36);
        add(refreshBtn);

        py += 62;

        // Table
        tableModel = new DefaultTableModel(
            new String[]{"Booking ID", "Bus Name", "From", "To", "Date", "Time", "Seat"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        Theme.styleTable(table);
        table.setRowHeight(40);

        // Seat column styling
        table.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, focus, row, col);
                setBackground(sel ? Theme.ACCENT_DARK : Theme.BG_CARD);
                setForeground(Theme.ACCENT);
                setFont(Theme.FONT_BOLD);
                setHorizontalAlignment(CENTER);
                if (v != null) setText("  Seat " + v + "  ");
                return this;
            }
        });

        JScrollPane sp = Theme.styledScroll(table);
        sp.setBounds(px, py, 830, 450);
        add(sp);

        py += 460;

        // Cancel button
        JButton cancelBtn = Theme.dangerButton("Cancel Selected Booking");
        cancelBtn.setBounds(px, py, 240, 42);
        add(cancelBtn);

        JLabel hint = Theme.label("Select a booking row, then click cancel to remove it.",
                Theme.TEXT_MUTED, Theme.FONT_SMALL);
        hint.setBounds(px + 260, py + 12, 400, 20);
        add(hint);

        // ── Events ────────────────────────────────────────────────────────
        refreshBtn.addActionListener(e -> loadBookings());

        cancelBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                Theme.showError(null, "Please select a booking to cancel.");
                return;
            }
            int bookingId = (int) tableModel.getValueAt(row, 0);
            if (!Theme.confirm(null, "Cancel booking #" + bookingId + "?")) return;

            BookingController bc = new BookingController();
            if (bc.cancel(bookingId, Session.userId)) {
                tableModel.removeRow(row);
                countLabel.setText("Cancellation successful.");
                countLabel.setForeground(Theme.SUCCESS);
            } else {
                Theme.showError(null, "Could not cancel booking.");
            }
        });
    }

    private void loadBookings() {
        tableModel.setRowCount(0);
        try {
            Connection con = DBConnection.getConnection();
            String sql = """
                SELECT b.id, bu.bus_name, bu.route_from, bu.route_to,
                       bu.travel_date, bu.travel_time, b.seat_number
                FROM bookings b
                JOIN buses bu ON b.bus_id = bu.id
                WHERE b.user_id = ?
                ORDER BY b.id DESC
            """;
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, Session.userId);
            ResultSet rs = ps.executeQuery();
            int count = 0;
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("bus_name"),
                    rs.getString("route_from"),
                    rs.getString("route_to"),
                    rs.getString("travel_date"),
                    rs.getString("travel_time"),
                    rs.getInt("seat_number")
                });
                count++;
            }
            countLabel.setForeground(Theme.TEXT_MUTED);
            countLabel.setText(count == 0
                ? "You have no bookings yet."
                : "You have " + count + " active booking" + (count > 1 ? "s" : "") + ".");
        } catch (Exception e) {
            countLabel.setForeground(Theme.DANGER);
            countLabel.setText("Error loading bookings: " + e.getMessage());
        }
    }
}
