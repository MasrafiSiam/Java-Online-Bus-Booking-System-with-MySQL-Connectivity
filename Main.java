import util.Theme;
import view.LoginGUI;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Apply global UI settings
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        SwingUtilities.invokeLater(() -> {
            Theme.applyGlobals();
            new LoginGUI();
        });
    }
}
