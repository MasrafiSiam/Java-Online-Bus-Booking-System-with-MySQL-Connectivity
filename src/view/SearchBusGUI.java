package view;

/**
 * Kept for backward compatibility.
 * The search functionality is now embedded inside DashboardGUI → SearchBusPanel.
 * This class simply opens a standalone window around the same panel.
 */
public class SearchBusGUI extends BaseFrame {

    public SearchBusGUI() {
        super("Search Buses", 900, 680);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        SearchBusPanel panel = new SearchBusPanel();
        panel.setBounds(0, 0, 880, 660);
        root.add(panel);

        setVisible(true);
    }
}
