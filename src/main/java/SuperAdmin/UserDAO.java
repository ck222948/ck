package SuperAdmin;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {//数据操作类
    public static List<User> getAllUsers() {//获取所有用户信息
        List<User> users = new ArrayList<>();
        try (Connection conn = DatabaseConnector.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT username, account, role, password FROM users");

            while (rs.next()) {
                users.add(new User(
                        rs.getString("username"),
                        rs.getString("account"),
                        rs.getString("role"),
                        rs.getString("password") // 获取密码
                ));
            }
        } catch (SQLException e) {
            UIUtils.showDatabaseError("加载用户数据出错: " + e.getMessage());
        }
        return users;
    }

    public static boolean addUser(User user) {
        try (Connection conn = DatabaseConnector.getConnection()) {
            // 检查账户唯一性
            if (userExists(user.getAccount())) {
                UIUtils.showErrorMessage("账户已被注册，请使用其他账户", "账户冲突");
                return false;
            }

            String sql = "INSERT INTO users (username, password, role, account) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, user.getUsername());
                pstmt.setString(2, PasswordUtil.encryptPassword(user.getPassword()));
                pstmt.setString(3, user.getRole());
                pstmt.setString(4, user.getAccount());//将SQL语句中的占位符?替换为实际的用户数据
                pstmt.executeUpdate();
            }
            return true;
        } catch (SQLException ex) {
            UIUtils.showDatabaseError("添加用户失败: " + ex.getMessage());
            return false;
        }
    }

    public static boolean updateUser(String originalAccount, User updatedUser) {
        try (Connection conn = DatabaseConnector.getConnection()) {
            // 如果账户发生了变化，检查新账户是否唯一
            if (!originalAccount.equals(updatedUser.getAccount())) {
                if (userExists(updatedUser.getAccount())) {
                    UIUtils.showErrorMessage("新账户已被注册，无法修改", "账户冲突");
                    return false;
                }
            }

            String sql = "UPDATE users SET username = ?, password = ?, role = ?, account = ? WHERE account = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, updatedUser.getUsername());
                pstmt.setString(2, PasswordUtil.encryptPassword(updatedUser.getPassword()));
                pstmt.setString(3, updatedUser.getRole());
                pstmt.setString(4, updatedUser.getAccount());
                pstmt.setString(5, originalAccount);
                pstmt.executeUpdate();
            }
            return true;
        } catch (SQLException ex) {
            UIUtils.showDatabaseError("更新用户失败: " + ex.getMessage());
            return false;
        }
    }

    public static boolean deleteUser(String account) {
        try (Connection conn = DatabaseConnector.getConnection()) {
            String sql = "DELETE FROM users WHERE account = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, account);
                pstmt.executeUpdate();
            }
            return true;
        } catch (SQLException ex) {
            UIUtils.showDatabaseError("删除用户失败: " + ex.getMessage());
            return false;
        }
    }

    public static List<User> searchUsers(String keyword) {
        List<User> users = new ArrayList<>();
        try (Connection conn = DatabaseConnector.getConnection()) {
            String sql = "SELECT username, account, password,role FROM users WHERE username LIKE ? OR account LIKE ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, "%" + keyword + "%");
                pstmt.setString(2, "%" + keyword + "%");
                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    users.add(new User(
                            rs.getString("username"),
                            rs.getString("account"),
                            rs.getString("role"),
                            rs.getString("password")
                    ));
                }
            }
        } catch (SQLException ex) {
            UIUtils.showDatabaseError("搜索错误: " + ex.getMessage());
        }
        return users;
    }

    public static List<User> filterByRole(String role) {
        List<User> users = new ArrayList<>();
        try (Connection conn = DatabaseConnector.getConnection()) {
            String sql = "SELECT username, role, password,account FROM users WHERE role = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, role);
                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    users.add(new User(
                            rs.getString("username"),
                            rs.getString("account"),
                            rs.getString("role"),
                            rs.getString("password")
                    ));
                }
            }
        } catch (SQLException ex) {
            UIUtils.showDatabaseError("筛选错误: " + ex.getMessage());
        }
        return users;
    }

    public static boolean userExists(String account) {//查询写入的account是否在数据库中已经存在
        try (Connection conn = DatabaseConnector.getConnection()) {
            String sql = "SELECT COUNT(*) FROM users WHERE account = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, account);
                ResultSet rs = pstmt.executeQuery();
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            return false;
        }
    }
}