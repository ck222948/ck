package SuperAdmin;

import javax.swing.*;
import java.awt.*;

public class UIUtils {//UI辅助方法
    public static JButton createStyledButton(String text, Color color) {//按钮
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

    public static void showDatabaseError(String message) {//错误信息收集
        JOptionPane.showMessageDialog(null,
                message,
                "数据库错误",
                JOptionPane.ERROR_MESSAGE);
    }

    public static void showInfoMessage(String message, String title) {//提示框
        JOptionPane.showMessageDialog(null,
                message,
                title,
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean validateUserInput(String username, String account) {//验证对话框
        // 验证账户
        if (account.isEmpty()) {
            showErrorMessage("账户不能为空", "输入错误");
            return false;
        }


        // 验证用户名
        if (username.isEmpty()) {
            showErrorMessage("用户名不能为空", "输入错误");
            return false;
        }



        return true;
    }

    public static void showErrorMessage(String message, String title) {//错误提示框
        JOptionPane.showMessageDialog(null,
                message,
                title,
                JOptionPane.ERROR_MESSAGE);
    }
}