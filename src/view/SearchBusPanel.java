package view;

import model.DBConnection;
import util.Theme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.sql.*;

public class SearchBusPanel extends JPanel {

    private DefaultTableModel tableModel;
    private JTable table;
    private JComboBox<String> fromBox, toBox;
    private JTextField dateField;
    private JLabel resultLabel;

    private static final String[] CITIES = {
            "Dhaka", "Chittagong", "Sylhet", "Khulna", "Rajshahi",
            "Barisal", "Cox Bazar", "Mymensingh", "Comilla", "Jessore"
    };

    public SearchBusPanel() {
        setLayout(null);
        setOpaque(false);
        buildUI();
    }

    private void buildUI() {
        int px = 24, py = 18;

        // Filter card
        JPanel filterCard = Theme.card(12);
        filterCard.setLayout(null);
        filterCard.setBounds(px, py, 830, 110);
        add(filterCard);

        // FROM
        JLabel fromL = Theme.label("FROM", Theme.TEXT_MUTED, new Font("Segoe UI", Font.BOLD, 10));
        fromL.setBounds(16, 12, 60, 16);
        filterCard.add(fromL);
        fromBox = Theme.styledCombo(CITIES);
        fromBox.setBounds(16, 30, 180, 36);
        filterCard.add(fromBox);

        // Swap icon
        JLabel swap = new JLabel("⇄");
        swap.setForeground(Theme.ACCENT);
        swap.setFont(new Font("Segoe UI", Font.BOLD, 20));
        swap.setBounds(204, 34, 30, 28);
        swap.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        filterCard.add(swap);
        swap.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                int fi = fromBox.getSelectedIndex();
                fromBox.setSelectedIndex(toBox.getSelectedIndex());
                toBox.setSelectedIndex(fi);
            }
        });

        // TO
        JLabel toL = Theme.label("TO", Theme.TEXT_MUTED, new Font("Segoe UI", Font.BOLD, 10));
        toL.setBounds(244, 12, 60, 16);
        filterCard.add(toL);
        toBox = Theme.styledCombo(CITIES);
        toBox.setSelectedIndex(1);
        toBox.setBounds(244, 30, 180, 36);
        filterCard.add(toBox);

        // DATE
        JLabel dateL = Theme.label("DATE (YYYY-MM-DD)", Theme.TEXT_MUTED,
                new Font("Segoe UI", Font.BOLD, 10));
        dateL.setBounds(442, 12, 180, 16);
        filterCard.add(dateL);
        dateField = Theme.styledField("Leave blank for all dates");
        dateField.setBounds(442, 30, 200, 36);
        filterCard.add(dateField);

        // Search button
        JButton searchBtn = Theme.primaryButton("🔍  Search");
        searchBtn.setBounds(662, 26, 150, 44);
        filterCard.add(searchBtn);

        py += 126;

        // Result count label
        resultLabel = Theme.label("Enter route above to search buses", Theme.TEXT_MUTED, Theme.FONT_SMALL);
        resultLabel.setBounds(px, py, 500, 22);
        add(resultLabel);

        py += 28;

        // Table
        tableModel = new DefaultTableModel(
                new String[]{"ID", "Bus Name", "From", "To", "Date", "Time", "Seats", "Available"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        Theme.styleTable(table);
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(7).setPreferredWidth(80);

        // Color "Available" column
        table.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, focus, row, col);
                setBackground(sel ? Theme.ACCENT_DARK : Theme.BG_CARD);
                if (v != null) {
                    int avail = Integer.parseInt(v.toString());
                    setForeground(avail > 10 ? Theme.SUCCESS : avail > 0 ? Theme.WARN : Theme.DANGER);
                    setText(avail > 0 ? avail + " seats" : "FULL");
                }
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                setHorizontalAlignment(CENTER);
                return this;
            }
        });

        JScrollPane sp = Theme.styledScroll(table);
        sp.setBounds(px, py, 830, 470);
        add(sp);

        // Hint label at bottom
        JLabel hint = Theme.label("Click a bus row to select seats and book", Theme.TEXT_MUTED, Theme.FONT_SMALL);
        hint.setBounds(px, py + 478, 500, 18);
        add(hint);

        // ── Events ────────────────────────────────────────────────────────
        searchBtn.addActionListener(e -> loadBuses());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    int busId   = (int) tableModel.getValueAt(row, 0);
                    String name = (String) tableModel.getValueAt(row, 1);
                    String from = (String) tableModel.getValueAt(row, 2);
                    String to   = (String) tableModel.getValueAt(row, 3);
                    new SeatBookingGUI(busId, name, from, to);
                    table.clearSelection();
                }
            }
        });
    }

    private void loadBuses() {
        tableModel.setRowCount(0);
        String from = fromBox.getSelectedItem().toString();
        String to   = toBox.getSelectedItem().toString();
        String date = dateField.getText().trim();

        if (from.equals(to)) {
            resultLabel.setText("Origin and destination cannot be the same.");
            resultLabel.setForeground(Theme.DANGER);
            return;
        }

        try {
            Connection con = DBConnection.getConnection();
            StringBuilder sql = new StringBuilder(
                "SELECT * FROM buses WHERE route_from=? AND route_to=?");
            if (!date.isBlank()) sql.append(" AND travel_date=?");
            sql.append(" ORDER BY travel_date, travel_time");

            PreparedStatement ps = con.prepareStatement(sql.toString());
            ps.setString(1, from);
            ps.setString(2, to);
            if (!date.isBlank()) ps.setString(3, date);

            ResultSet rs = ps.executeQuery();
            int count = 0;

            while (rs.next()) {
                int busId    = rs.getInt("id");
                int total    = rs.getInt("total_seats");
                int booked   = countBooked(con, busId);
                int avail    = total - booked;

                tableModel.addRow(new Object[]{
                    busId,
                    rs.getString("bus_name"),
                    rs.getString("route_from"),
                    rs.getString("route_to"),
                    rs.getString("travel_date"),
                    rs.getString("travel_time"),
                    total,
                    avail
                });
                count++;
            }

            resultLabel.setForeground(Theme.TEXT_MUTED);
            resultLabel.setText(count == 0
                ? "No buses found for this route."
                : count + " bus" + (count > 1 ? "es" : "") + " found — click a row to book");
        } catch (Exception ex) {
            resultLabel.setForeground(Theme.DANGER);
            resultLabel.setText("Error: " + ex.getMessage());
        }
    }

    private int countBooked(Connection con, int busId) throws SQLException {
        PreparedStatement ps = con.prepareStatement(
                "SELECT COUNT(*) FROM bookings WHERE bus_id=?");
        ps.setInt(1, busId);
        ResultSet rs = ps.executeQuery();
        return rs.next() ? rs.getInt(1) : 0;
    }
}
