package controller;

import dao.BusDAO;
import java.sql.ResultSet;

public class BusController {
    private final BusDAO dao = new BusDAO();

    public ResultSet getBuses() {
        return dao.getAllBuses();
    }

    public ResultSet searchBuses(String from, String to, String date) {
        return dao.searchBuses(from, to, date);
    }
}
