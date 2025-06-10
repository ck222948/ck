package SuperAdmin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class UserDialog extends JDialog {
    private JTextField accountField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JComboBox<String> roleCombo;

    private boolean confirmed = false;
    private User userResult;
    private boolean isEditMode;

    public UserDialog(JFrame parent, String title, boolean isEditMode, User existingUser) {
        super(parent, title, true);
        this.isEditMode = isEditMode;
        initUI(existingUser);
    }

    private void initUI(User existingUser) {
        setSize(500, 400);
        setLayout(new BorderLayout());
        setLocationRelativeTo(getParent());
        setResizable(false);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(getTitle()));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        if (isEditMode && existingUser != null) {
            addEditModeFields(formPanel, gbc, existingUser);
        } else {
            addAddModeFields(formPanel, gbc);
        }

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton saveButton = new JButton("保存");
        JButton cancelButton = new JButton("取消");

        saveButton.addActionListener(e -> handleSaveAction());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        contentPanel.add(formPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(contentPanel);
    }


    private void addEditModeFields(JPanel formPanel, GridBagConstraints gbc, User existingUser) {
        // 账户字段移动到第一个位置（主键）
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("账户:"), gbc); // 不再设为只读

        gbc.gridx = 1;
        accountField = new JTextField(existingUser.getAccount());
        formPanel.add(accountField, gbc);
        // 当前用户名（只读）现在放在账户下方
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("用户名:"), gbc);

        gbc.gridx = 1;
        JTextField currentUsernameField = new JTextField(existingUser.getUsername());
        currentUsernameField.setEditable(false);
        formPanel.add(currentUsernameField, gbc);

        // 新用户名（可编辑）
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("新用户名:"), gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(existingUser.getUsername());
        formPanel.add(usernameField, gbc);

        // 新密码
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("密码:"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(existingUser.getPassword()); // 预填密码
        formPanel.add(passwordField, gbc);

        // 确认密码
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("确认密码:"), gbc);

        gbc.gridx = 1;
        confirmPasswordField = new JPasswordField();
        formPanel.add(confirmPasswordField, gbc);

        // 角色
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("角色:"), gbc);

        gbc.gridx = 1;
        roleCombo = new JComboBox<>(new String[]{"管理员", "分析员", "配置员"});
        roleCombo.setSelectedItem(existingUser.getRole());
        formPanel.add(roleCombo, gbc);
    }

    private void addAddModeFields(JPanel formPanel, GridBagConstraints gbc) {
        // 账户字段移动到第一个位置（主键）
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("账户:"), gbc);

        gbc.gridx = 1;
        accountField = new JTextField(20);
        formPanel.add(accountField, gbc);


        // 用户名
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("用户名:"), gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(20);
        formPanel.add(usernameField, gbc);

        // 密码
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("密码:"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        formPanel.add(passwordField, gbc);

        // 确认密码
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("确认密码:"), gbc);

        gbc.gridx = 1;
        confirmPasswordField = new JPasswordField(20);
        formPanel.add(confirmPasswordField, gbc);

        // 角色
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("角色:"), gbc);

        gbc.gridx = 1;
        roleCombo = new JComboBox<>(new String[]{"管理员", "分析员", "配置员"});
        formPanel.add(roleCombo, gbc);
    }

    private void handleSaveAction() {
        // 收集表单数据
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String role = (String) roleCombo.getSelectedItem();
        String account = accountField.getText().trim();

        // 验证逻辑（需要确保账户不为空）
        if (!UIUtils.validateUserInput(username, password, confirmPassword, account)) {
            return;
        }

        // 使用正确的参数顺序：用户名、账户、角色
        userResult = new User(username, account, role,password);
        confirmed = true;
        dispose();
    }
    public User getUser() {
        return userResult;
    }
    public boolean isConfirmed() {
        return confirmed;
    }
}