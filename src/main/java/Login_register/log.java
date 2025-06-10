package Login_register;

import AnalysisAdmin.AlgorithmPerformanceFrame;
import OperationAdmin.Control.Activemq;
import SuperAdmin.AdminPanel;

import OperationAdmin.View.Menu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Base64;

import static Login_register.DatabaseConnector.getConnection;

public class log {
    public static final int WINDOW_WIDTH = 1000;
    public static final int WINDOW_HEIGHT = 700;

    private JFrame frame;
    private JTextField userText;
    private JPasswordField passwordText;
    private JComboBox<String> roleBox;

    public void showLogin() {
        frame = new JFrame("登录界面");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setLocationRelativeTo(null);

        BackgroundPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(new BorderLayout());

        JPanel overlayPanel = new JPanel();
        overlayPanel.setLayout(new BorderLayout());
        overlayPanel.setOpaque(false);

        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BorderLayout(0, 30));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));

        JLabel titleLabel = new JLabel("实验管理系统", JLabel.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 36));
        titleLabel.setForeground(new Color(33, 150, 243)); // 主色调一致
        titleLabel.setForeground(Color.WHITE);
        contentPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = createFormPanel();
        contentPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = createButtonPanel();
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        overlayPanel.add(contentPanel, BorderLayout.CENTER);
        backgroundPanel.add(overlayPanel, BorderLayout.CENTER);
        frame.setContentPane(backgroundPanel);
        frame.setVisible(true);
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(255, 255, 255, 120), 1),
                        "用户登录",
                        javax.swing.border.TitledBorder.CENTER,
                        javax.swing.border.TitledBorder.DEFAULT_POSITION,
                        new Font("微软雅黑", Font.BOLD, 20),
                        Color.WHITE
                ),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 用户名
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel userLabel = new JLabel("账号:");
        userLabel.setFont(new Font("微软雅黑", Font.PLAIN, 18));
        userLabel.setForeground(Color.WHITE);
        formPanel.add(userLabel, gbc);

        gbc.gridx = 1; gbc.gridwidth = 2;
        userText = new JTextField(20);
        styleTextField(userText);
        formPanel.add(userText, gbc);

        // 密码
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        JLabel passwordLabel = new JLabel("密码:");
        passwordLabel.setFont(new Font("微软雅黑", Font.PLAIN, 18));
        passwordLabel.setForeground(Color.WHITE);
        formPanel.add(passwordLabel, gbc);

        gbc.gridx = 1; gbc.gridwidth = 2;
        passwordText = new JPasswordField(20);
        styleTextField(passwordText);
        formPanel.add(passwordText, gbc);

        // 角色
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        JLabel roleLabel = new JLabel("角色:");
        roleLabel.setFont(new Font("微软雅黑", Font.PLAIN, 18));
        roleLabel.setForeground(Color.WHITE);
        formPanel.add(roleLabel, gbc);

        gbc.gridx = 1; gbc.gridwidth = 2;
        String[] roles = {"超级管理员", "实验配置管理员", "实验分析员"};
        roleBox = new JComboBox<>(roles);
        styleComboBox(roleBox);
        formPanel.add(roleBox, gbc);

        return formPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JButton loginButton = new JButton("登录");
        styleButton(loginButton);
        loginButton.addActionListener(e -> handleLogin());

        JButton registerButton = new JButton("注册");
        styleButton(registerButton);
        registerButton.addActionListener(e -> Register.showRegister(frame));

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        return buttonPanel;
    }

    private void handleLogin() {
        String username = userText.getText().trim();
        String password = new String(passwordText.getPassword());
        String role = (String) roleBox.getSelectedItem();

        if (username.isEmpty() || password.isEmpty()) {
            showError("用户名和密码不能为空");
            return;
        }

        if (authenticateUser(username, password, role)) {
            frame.dispose();
            showRolePanel(username, role);
        } else {
            showError("用户名、密码或角色错误");
        }
    }

    private boolean authenticateUser(String username, String password, String role) {
        String sql = "SELECT password FROM users WHERE account = ? AND role = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, role);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");
                return storedPassword.equals(encryptPassword(password));
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void showRolePanel(String username, String role) {
        switch (role) {
            case "超级管理员":
                showSuperAdminPanel(username);
                break;
            case "实验配置管理员":
                showConfigAdminPanel(username);
                break;
            case "实验分析员":
                showAnalysisPanel(username);
                break;
        }
    }
//查询用户名
    /**
     * 根据账号查询用户名
     *
     * @param account 账号
     * @return 用户名
     */
    public String getUsernameById(String account) {
        String sql = "SELECT username FROM users WHERE account = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, account);  // 设置参数
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("username");  // 返回查询到的用户名
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;  // 没找到返回null
    }
    /**
     * 显示超级管理员面板
     *
     * @param account 账号
     */
    private void showSuperAdminPanel(String account) {
        // 显示超级管理员面板
        SuperAdmin.DatabaseConnector.initializeDriver();
        SwingUtilities.invokeLater(() -> {
            AdminPanel app = new AdminPanel();
            app.setVisible(true);
        });
    }
    /**
     * 显示配置管理员面板
     *
     * @param username 用户名
     */
    private void showConfigAdminPanel(String username) {

        Menu menu = new Menu();
        menu.Menu();
        //View.Test test = new View.Test();
        //test.View.Test();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            Activemq activeMQListenerService;
            public void run() {
                activeMQListenerService = new Activemq();
                activeMQListenerService.writeMessage("IsViewOpen","0");
            }
        });

    }
/**
     * 显示分析员面板
     *
     * @param username 用户名
     */
    private void showAnalysisPanel(String username) {
        AlgorithmPerformanceFrame frame = new AlgorithmPerformanceFrame();
        frame.setVisible(true);
    }

    private void styleTextField(JTextField textField) {
        textField.setFont(new Font("微软雅黑", Font.PLAIN, 18));
        textField.setPreferredSize(new Dimension(300, 40));
        textField.setOpaque(true); // 确保组件本身不透明
        textField.setBackground(Color.WHITE); // 不用透明颜色
        textField.setForeground(Color.DARK_GRAY);
        textField.setCaretColor(Color.DARK_GRAY);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
    }


/**
     * 样式化下拉框
     *
     * @param comboBox 下拉框
     */
    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setFont(new Font("微软雅黑", Font.PLAIN, 18));
        comboBox.setPreferredSize(new Dimension(300, 40));
        comboBox.setBackground(Color.WHITE);
        comboBox.setForeground(Color.DARK_GRAY);
        comboBox.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 1, true));

        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setBackground(isSelected ? new Color(33, 150, 243) : Color.WHITE);
                label.setForeground(isSelected ? Color.WHITE : Color.DARK_GRAY);
                label.setFont(new Font("微软雅黑", Font.PLAIN, 16));
                return label;
            }
        });
    }


/**
     * 样式化按钮
     *
     * @param button 按钮
 * */

    private void styleButton(JButton button) {
        button.setFont(new Font("微软雅黑", Font.BOLD, 18));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(33, 150, 243)); // 蓝色按钮
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(30, 136, 229)); // Hover 深一点
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(33, 150, 243));
            }
        });
    }

/**
     * 样式化管理员按钮
     *
     * @param button 按钮
     */
    private void styleAdminButton(JButton button) {
        button.setFont(new Font("微软雅黑", Font.BOLD, 20));
        button.setPreferredSize(new Dimension(300, 80));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(100, 150, 200));
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(70, 130, 180));
            }
        });
    }
//加密密码
    /**
     * 加密密码
     *
     * @param password 原始密码
     * @return 加密后的密码
     */
    private String encryptPassword(String password) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (java.security.NoSuchAlgorithmException e) {
            e.printStackTrace();
            return password;
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(frame, message, "错误", JOptionPane.ERROR_MESSAGE);
    }
}