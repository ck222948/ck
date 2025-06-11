package Login_register;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Base64;

public class Register {
    private JFrame registerFrame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JComboBox<String> roleComboBox;
    private JTextField accountField;

    public static void showRegister(JFrame parentFrame) {
        new Register(parentFrame);
    }

    private Register(JFrame parentFrame) {
        initializeUI(parentFrame);
    }

    private void initializeUI(JFrame parentFrame) {
        registerFrame = new JFrame("用户注册");
        registerFrame.setSize(log.WINDOW_WIDTH, log.WINDOW_HEIGHT);
        registerFrame.setLocationRelativeTo(parentFrame);
        registerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // 使用自定义背景面板
        BackgroundPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(new BorderLayout());

        // 添加一个透明覆盖层，方便叠加内容
        JPanel overlayPanel = new JPanel(new BorderLayout());
        overlayPanel.setOpaque(false);

        // 创建主面板（设置边距和布局）
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));

        // 顶部标题
        JLabel titleLabel = new JLabel("用户注册", JLabel.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 36));
        titleLabel.setForeground(new Color(33, 150, 243)); // 主色调一致

        titleLabel.setForeground(Color.WHITE);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // 表单区域
        JPanel formPanel = createFormPanel();
        mainPanel.add(formPanel, BorderLayout.CENTER);

        // 按钮区域
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        overlayPanel.add(mainPanel, BorderLayout.CENTER);
        backgroundPanel.add(overlayPanel, BorderLayout.CENTER);

        registerFrame.setContentPane(backgroundPanel);
        registerFrame.setVisible(true);
    }



    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(255, 255, 255, 120), 1),
                        "注册信息",
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
// 手机号
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1;
        JLabel phoneLabel = new JLabel("账号:");
        phoneLabel.setFont(new Font("微软雅黑", Font.PLAIN, 18));
        phoneLabel.setForeground(Color.WHITE);
        formPanel.add(phoneLabel, gbc);

        gbc.gridx = 1; gbc.gridwidth = 2;
        accountField = new JTextField(20);
        styleTextField(accountField);
        formPanel.add(accountField, gbc);



// 密码
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        JLabel passwordLabel = new JLabel("密码:");
        passwordLabel.setFont(new Font("微软雅黑", Font.PLAIN, 18));
        passwordLabel.setForeground(Color.WHITE);
        formPanel.add(passwordLabel, gbc);

        gbc.gridx = 1; gbc.gridwidth = 2;
        passwordField = new JPasswordField(20);
        styleTextField(passwordField);
        formPanel.add(passwordField, gbc);

// 确认密码
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        JLabel confirmLabel = new JLabel("确认密码:");
        confirmLabel.setFont(new Font("微软雅黑", Font.PLAIN, 18));
        confirmLabel.setForeground(Color.WHITE);
        formPanel.add(confirmLabel, gbc);

        gbc.gridx = 1; gbc.gridwidth = 2;
        confirmPasswordField = new JPasswordField(20);
        styleTextField(confirmPasswordField);
        formPanel.add(confirmPasswordField, gbc);
        // 用户名
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1;
        JLabel userLabel = new JLabel("用户名:");
        userLabel.setFont(new Font("微软雅黑", Font.PLAIN, 18));
        userLabel.setForeground(Color.WHITE);
        formPanel.add(userLabel, gbc);

        gbc.gridx = 1; gbc.gridwidth = 2;
        usernameField = new JTextField(20);
        styleTextField(usernameField);
        formPanel.add(usernameField, gbc);
// 角色
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 1;
        JLabel roleLabel = new JLabel("角色:");
        roleLabel.setFont(new Font("微软雅黑", Font.PLAIN, 18));
        roleLabel.setForeground(Color.WHITE);
        formPanel.add(roleLabel, gbc);

        gbc.gridx = 1; gbc.gridwidth = 2;
        String[] roles = {"超级管理员", "实验配置管理员", "实验分析员"};
        roleComboBox = new JComboBox<>(roles);
        styleComboBox(roleComboBox);
        formPanel.add(roleComboBox, gbc);

return formPanel;

    }


    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JButton registerButton = new JButton("注册");
        styleButton(registerButton);
        registerButton.addActionListener(this::handleRegister);

        JButton cancelButton = new JButton("取消");
        styleButton(cancelButton);
        cancelButton.addActionListener(e -> registerFrame.dispose());

        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);

        return buttonPanel;
    }


    private void handleRegister(ActionEvent e) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String role = (String) roleComboBox.getSelectedItem();
        String account = accountField.getText().trim();

        // 输入验证
        if (username.isEmpty() || password.isEmpty()||account.isEmpty()) {
            showError("用户名和密码不能为空");
            return;
        }

        if (password.length() < 4) {
            showError("密码长度不能少于4个字符");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("两次输入的密码不一致");
            return;
        }



        // 调用注册方法，成功后提示并关闭窗口
        if (registerUser(username, password, role, account)) {
            JOptionPane.showMessageDialog(registerFrame,
                    "注册成功！",
                    "成功",
                    JOptionPane.INFORMATION_MESSAGE);
            registerFrame.dispose();
        } else {
            showError("用户名已存在或注册失败");
        }
    }

    // 用户注册数据库操作
    private boolean registerUser(String username, String password, String role, String account) {
        String checkSql = "SELECT * FROM users WHERE account = ?";
        String insertSql = "INSERT INTO users (username, password, role, account) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            // 检账户是否已存在
            checkStmt.setString(1, account);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                return false; // 账户已存在
            }

            // 密码加密处理
            String encryptedPass = encryptPassword(password);

            // 插入新用户数据
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setString(1, username);
                insertStmt.setString(2, encryptedPass);
                insertStmt.setString(3, role);
                insertStmt.setString(4, account);
                insertStmt.executeUpdate();
                return true;
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    // 使用 SHA-256 加密密码

    private String encryptPassword(String password) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash); // 转 Base64 保存
        } catch (java.security.NoSuchAlgorithmException e) {
            e.printStackTrace();
            return password; // 加密失败则返回原始密码（不推荐）
        }
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






    private void showError(String message) {
        JOptionPane.showMessageDialog(registerFrame, message, "错误", JOptionPane.ERROR_MESSAGE);
    }
}