package controller;

import dao.UserDAO;

public class AuthController {

    private final UserDAO dao = new UserDAO();

    public boolean register(String name, String email, String password) {
        if (name.isBlank() || email.isBlank() || password.isBlank()) return false;
        if (!email.contains("@")) return false;
        if (password.length() < 4) return false;
        return dao.register(name, email, password);
    }

    public boolean login(String email, String password) {
        if (email.isBlank() || password.isBlank()) return false;
        return dao.login(email, password);
    }

    public int getUserId(String email) {
        return dao.getUserId(email);
    }

    public String getName(String email) {
        return dao.getName(email);
    }
}
