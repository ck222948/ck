package AnalysisAdmin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 提供MySQL数据库连接和查询功能。
 */
public class MySQLDatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/user";
    private static final String USER = "root";
    private static final String PASSWORD = "040220";
    private static Connection conn;
    private static Logger logger = LoggerFactory.getLogger(MySQLDatabaseConnection.class.getName());


    public static void connect() throws SQLException {
        try {
            if(null == conn || conn.isClosed()) {
                conn = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (SQLException e) {
            logger.error("连接失败: {}",e.getMessage());
            throw e;
        }
    }
    public void disconnect() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            logger.info("无法关闭连接: {}",e.getMessage());
        }
    }
    public static Connection getConnection() throws SQLException {
        if(null == conn || conn.isClosed()) connect();
        return conn;
    }
}