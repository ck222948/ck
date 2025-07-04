package SuperAdmin;

import Login_register.log;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Vector;

public class AdminPanel extends JFrame {
    // UI组件
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> roleFilter;

    public AdminPanel() {
        // 设置窗口属性
        setTitle("用户管理系统 - 管理员面板");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 创建菜单栏
        createMenuBar();

        // 创建主面板
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(240, 240, 240));

        // 创建标题面板
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(new Color(70, 130, 180));
        JLabel titleLabel = new JLabel("用户管理系统");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // 创建工具栏
        JPanel toolbar = createToolbar();
        mainPanel.add(toolbar, BorderLayout.CENTER);

        // 创建表格
        createUserTable();
        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBorder(new EmptyBorder(10, 0, 0, 0));
        scrollPane.setBackground(Color.WHITE);
        mainPanel.add(scrollPane, BorderLayout.SOUTH);

        add(mainPanel);
        loadUserData();
    }

    private JPanel createToolbar() {//添加工具栏（按钮）
        JPanel toolBarContainer = new JPanel(new BorderLayout());
        toolBarContainer.setBackground(Color.WHITE);

        // 操作按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setBackground(new Color(240, 240, 240));

        JButton refreshButton = UIUtils.createStyledButton("刷新数据", new Color(70, 130, 180));
        JButton addButton = UIUtils.createStyledButton("添加用户", new Color(46, 139, 87));
        JButton editButton = UIUtils.createStyledButton("编辑用户", new Color(138, 43, 226));
        JButton deleteButton = UIUtils.createStyledButton("删除用户", new Color(178, 34, 34));

        buttonPanel.add(refreshButton);
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        // 搜索和过滤面板
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));
        searchPanel.setBackground(new Color(240, 240, 240));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        searchField = new JTextField(20);
        JButton searchButton = UIUtils.createStyledButton("搜索", new Color(52, 152, 219));

        roleFilter = new JComboBox<>(new String[]{"所有角色", "超级管理员", "系统分析员", "实验配置管理员"});
        roleFilter.setBackground(Color.WHITE);

        searchPanel.add(new JLabel("搜索:"));
        searchPanel.add(Box.createHorizontalStrut(5));
        searchPanel.add(searchField);
        searchPanel.add(Box.createHorizontalStrut(10));
        searchPanel.add(searchButton);
        searchPanel.add(Box.createHorizontalStrut(20));
        searchPanel.add(new JLabel("角色筛选:"));
        searchPanel.add(Box.createHorizontalStrut(5));
        searchPanel.add(roleFilter);

        // 添加事件监听器
        refreshButton.addActionListener(e -> refreshData());
        addButton.addActionListener(e -> addUser());
        editButton.addActionListener(e -> editUser());
        deleteButton.addActionListener(e -> deleteUser());
        searchButton.addActionListener(e -> searchUsers());
        roleFilter.addActionListener(e -> filterByRole());

        toolBarContainer.add(buttonPanel, BorderLayout.NORTH);
        toolBarContainer.add(searchPanel, BorderLayout.SOUTH);

        return toolBarContainer;
    }

    private void createMenuBar() {//菜单
        JMenuBar menuBar = new JMenuBar();
        JMenu exitMenu = new JMenu("退出");
        JMenuItem exitMenuItem = new JMenuItem("退出管理系统");
        exitMenu.add(exitMenuItem);
        exitMenuItem.addActionListener(e -> {

                System.out.println("尝试退出..."); // 调试
                this.dispose(); // 关闭当前窗口

                // 显示登录窗口（确保包路径正确）
                SwingUtilities.invokeLater(() -> {
                    try {
                        new log().showLogin();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
            });


        JMenu helpMenu = new JMenu("帮助");
        JMenuItem aboutItem = new JMenuItem("关于");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);

        menuBar.add(exitMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar); // 关键！必须设置菜单栏

    }

    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
                "用户管理系统 v2.1",
                "关于",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void createUserTable() {//表格
        String[] columns = {"用户名", "账号", "密码", "角色"};

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {//不可编辑
                return false;
            }
        };

        userTable = new JTable(tableModel);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);//可以单行选择
        userTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 14));
        userTable.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        userTable.setRowHeight(30);
        userTable.setGridColor(new Color(220, 220, 220));
        userTable.setShowGrid(true);
        userTable.setIntercellSpacing(new Dimension(1, 1));
    }

    private void loadUserData() {//获得表格内容

        List<User> users = UserDAO.getAllUsers();//select *
        tableModel.setRowCount(0);

        for (User user : users) {
            Vector<Object> row = new Vector<>();
            row.add(user.getUsername());
            row.add(user.getAccount());
            row.add(user.getPassword());
            row.add(user.getRole());
            tableModel.addRow(row);
        }
    }

    private void refreshData() {
        loadUserData();
        UIUtils.showInfoMessage("用户数据已刷新", "刷新完成");
    }

    private void addUser() {
        UserDialog dialog = new UserDialog(this, "添加新用户", false, null);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            User newUser = dialog.getUser();
            if (UserDAO.addUser(newUser)) {
                loadUserData();
                UIUtils.showInfoMessage("用户添加成功", "操作成功");
            } else {
                UIUtils.showErrorMessage("账号已存在，请重新输入", "输入错误");
            }
        }
    }

    private void editUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            UIUtils.showErrorMessage("请选择要编辑的用户", "操作提示");
            return;
        }
        // 获取行的所有信息
        String username = (String) userTable.getValueAt(selectedRow, 0);
        String account = (String) userTable.getValueAt(selectedRow, 1);
        String password = (String) userTable.getValueAt(selectedRow, 2);
        String role = (String) userTable.getValueAt(selectedRow, 3);

        User existingUser = new User(username, account, role, password);
        UserDialog dialog = new UserDialog(this, "编辑用户", true, existingUser);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            User updatedUser = dialog.getUser();
            // 使用账户作为唯一标识进行更新
            if (UserDAO.updateUser(account, updatedUser)) { // 传入原始账户
                loadUserData();
                UIUtils.showInfoMessage("用户信息更新成功", "操作成功");
            }
        }
    }

    private void deleteUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            UIUtils.showErrorMessage("请选择要删除的用户", "操作提示");
            return;
        }
        String account = (String) userTable.getValueAt(selectedRow, 1);
        String username = (String) userTable.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(//删除确认，增加容错
                this,
                "确定要删除用户 '" + username + "' (账号: " + account + ") 吗？",
                "确认删除",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (UserDAO.deleteUser(account)) {
                loadUserData();
                UIUtils.showInfoMessage("用户 '" + username + "' 已成功删除", "操作成功");
            }
        }
    }
    private void searchUsers() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadUserData();
            return;
        }

        List<User> users = UserDAO.searchUsers(searchTerm);//这里可以通过用户名和账户来查找
        updateTableWithUsers(users);

        UIUtils.showInfoMessage("找到 " + users.size() + " 条匹配记录", "搜索结果");
    }

    private void filterByRole() {//下拉条查找
        String selectedRole = (String) roleFilter.getSelectedItem();
        if (selectedRole.equals("所有角色")) {
            loadUserData();
            return;
        }

        List<User> users = UserDAO.filterByRole(selectedRole);
        updateTableWithUsers(users);

        UIUtils.showInfoMessage("找到 " + users.size() + " 个" + selectedRole, "筛选结果");
    }

    private void updateTableWithUsers(List<User> users) {//根据获得的用户来更新表格内容
        tableModel.setRowCount(0);
        for (User user : users) {
            Vector<Object> row = new Vector<>();
            row.add(user.getUsername());
            row.add(user.getAccount());
            row.add(user.getPassword()); // 显示密码
            row.add(user.getRole());
            tableModel.addRow(row);
        }
    }
}