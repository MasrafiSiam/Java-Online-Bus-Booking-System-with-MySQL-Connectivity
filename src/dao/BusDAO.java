package dao;

import model.DBConnection;
import java.sql.*;

public class BusDAO {

    public ResultSet getAllBuses() {
        try {
            String sql = "SELECT * FROM buses ORDER BY travel_date, travel_time";
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
            return ps.executeQuery();
        } catch (Exception e) { return null; }
    }

    public ResultSet searchBuses(String from, String to, String date) {
        try {
            StringBuilder sb = new StringBuilder(
                "SELECT * FROM buses WHERE route_from=? AND route_to=?");
            if (date != null && !date.isBlank()) sb.append(" AND travel_date=?");
            sb.append(" ORDER BY travel_date, travel_time");

            PreparedStatement ps = DBConnection.getConnection().prepareStatement(sb.toString());
            ps.setString(1, from);
            ps.setString(2, to);
            if (date != null && !date.isBlank()) ps.setString(3, date);
            return ps.executeQuery();
        } catch (Exception e) { return null; }
    }
}
