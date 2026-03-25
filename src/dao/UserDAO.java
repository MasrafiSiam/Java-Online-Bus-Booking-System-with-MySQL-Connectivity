package dao;

import model.DBConnection;
import java.sql.*;

public class UserDAO {

    public boolean register(String name, String email, String password) {
        try {
            // Check duplicate email
            String check = "SELECT id FROM users WHERE email=?";
            PreparedStatement ps0 = DBConnection.getConnection().prepareStatement(check);
            ps0.setString(1, email);
            if (ps0.executeQuery().next()) return false; // email already exists

            String sql = "INSERT INTO users(name,email,password) VALUES(?,?,?)";
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, password);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }

    public boolean login(String email, String password) {
        try {
            String sql = "SELECT * FROM users WHERE email=? AND password=?";
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            return false;
        }
    }

    /** Returns user id on success, -1 on failure. */
    public int getUserId(String email) {
        try {
            String sql = "SELECT id FROM users WHERE email=?";
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("id");
        } catch (Exception e) { /* ignore */ }
        return -1;
    }

    /** Returns display name for email. */
    public String getName(String email) {
        try {
            String sql = "SELECT name FROM users WHERE email=?";
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("name");
        } catch (Exception e) { /* ignore */ }
        return "User";
    }

    /** Returns all bookings for a user as ResultSet. */
    public ResultSet getMyBookings(int userId) {
        try {
            String sql = """
                SELECT b.id, bu.bus_name, bu.route_from, bu.route_to,
                       bu.travel_date, bu.travel_time, b.seat_number
                FROM bookings b
                JOIN buses bu ON b.bus_id = bu.id
                WHERE b.user_id = ?
                ORDER BY b.id DESC
            """;
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
            ps.setInt(1, userId);
            return ps.executeQuery();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean cancelBooking(int bookingId, int userId) {
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
}
