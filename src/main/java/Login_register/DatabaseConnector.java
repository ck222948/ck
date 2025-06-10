package Login_register;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {
    private static final String URL = "jdbc:mysql://localhost:3306/user"; // 数据库地址和端口
    private static final String USER = "root";// 数据库用户名
    private static final String PASSWORD = "040220";// 数据库密码

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
                throw new RuntimeException("无法加载MySQL JDBC驱动", e);

        }
    }
    // 获取数据库连接的方法
    public static Connection getConnection() throws SQLException {
        // 连接数据库
        return DriverManager.getConnection(URL, USER, PASSWORD); // 连接数据库
    }

    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            if (conn != null) {
                System.out.println("数据库连接成功！");
            } else {
                System.out.println("数据库连接失败。");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
