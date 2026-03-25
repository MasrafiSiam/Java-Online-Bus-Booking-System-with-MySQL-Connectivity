package dao;

import model.DBConnection;
import java.sql.*;

public class BookingDAO {

    public boolean book(int userId, int busId, int seatNumber) {
        try {
            // double-check seat not taken
            String check = "SELECT id FROM bookings WHERE bus_id=? AND seat_number=?";
            PreparedStatement ps0 = DBConnection.getConnection().prepareStatement(check);
            ps0.setInt(1, busId);
            ps0.setInt(2, seatNumber);
            if (ps0.executeQuery().next()) return false;

            String sql = "INSERT INTO bookings(user_id,bus_id,seat_number) VALUES(?,?,?)";
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setInt(2, busId);
            ps.setInt(3, seatNumber);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }

    public boolean cancel(int bookingId, int userId) {
        try {
            String sql = "DELETE FROM bookings WHERE id=? AND user_id=?";
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
            ps.setInt(1, bookingId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public int countBooked(int busId) {
        try {
            String sql = "SELECT COUNT(*) FROM bookings WHERE bus_id=?";
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
            ps.setInt(1, busId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { /* ignore */ }
        return 0;
    }
}
