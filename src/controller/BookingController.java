package controller;

import dao.BookingDAO;

public class BookingController {
    private final BookingDAO dao = new BookingDAO();

    public boolean book(int userId, int busId, int seat) {
        return dao.book(userId, busId, seat);
    }

    public boolean cancel(int bookingId, int userId) {
        return dao.cancel(bookingId, userId);
    }

    public int countBooked(int busId) {
        return dao.countBooked(busId);
    }
}
