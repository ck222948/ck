package SuperAdmin;

import javax.swing.*;
import java.awt.*;

public class UIUtils {
    public static JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("微软雅黑", Font.BOLD, 14));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createRaisedBevelBorder(),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        return button;
    }

    public static void showDatabaseError(String message) {
        JOptionPane.showMessageDialog(null,
                message,
                "数据库错误",
                JOptionPane.ERROR_MESSAGE);
    }

    public static void showInfoMessage(String message, String title) {
        JOptionPane.showMessageDialog(null,
                message,
                title,
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean validateUserInput(String username, String password, String confirmPassword, String account) {
        // 首先验证账户（主键）
        if (account.isEmpty()) {
            showErrorMessage("账户不能为空", "输入错误");
            return false;
        }


        // 然后验证用户名
        if (username.isEmpty()) {
            showErrorMessage("用户名不能为空", "输入错误");
            return false;
        }

        // 最后验证密码
        if (password.isEmpty()) {
            showErrorMessage("密码不能为空", "输入错误");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            showErrorMessage("两次输入的密码不一致", "输入错误");
            return false;
        }

        return true;
    }

    public static void showErrorMessage(String message, String title) {
        JOptionPane.showMessageDialog(null,
                message,
                title,
                JOptionPane.ERROR_MESSAGE);
    }
}