package Login_register;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        log log = new log();
        SwingUtilities.invokeLater(log::showLogin);
        System.out.println("Hello, World!");
    }
}