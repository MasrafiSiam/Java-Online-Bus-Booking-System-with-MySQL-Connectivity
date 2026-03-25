package util;

public class Session {

    public static int    userId   = 0;
    public static String email    = "";
    public static String userName = "";

    public static void clear() {
        userId   = 0;
        email    = "";
        userName = "";
    }

    public static boolean isLoggedIn() {
        return userId > 0;
    }
}
