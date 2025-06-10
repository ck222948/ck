package SuperAdmin;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        DatabaseConnector.initializeDriver();
        SwingUtilities.invokeLater(() -> {
            AdminPanel app = new AdminPanel();
            app.setVisible(true);
        });
    }
}