package OperationAdmin.View;

import OperationAdmin.Control.Explore_experiments;
import OperationAdmin.Control.Replay;

import javax.swing.*;
import java.awt.*;

public class Menu extends JFrame {
    private static final int WIDTH = 1000;
    private static final int HEIGHT = 700;

    public void Menu() {
        JFrame frame = new JFrame("菜单");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WIDTH, HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setLayout(null);

        // 设置背景颜色
        frame.getContentPane().setBackground(new Color(240, 240, 240));

        // 创建标题
        JLabel titleLabel = new JLabel("地图探索实验系统", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 32));
        titleLabel.setForeground(new Color(70, 130, 180));
        titleLabel.setBounds(0, 50, WIDTH, 60);
        frame.add(titleLabel);

        // 创建按钮面板
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 1, 0, 30));
        buttonPanel.setBounds(WIDTH/2 - 200, HEIGHT/2 - 150, 400, 300);
        buttonPanel.setOpaque(false); // 透明背景

        // 创建按钮样式
        Font buttonFont = new Font("微软雅黑", Font.PLAIN, 20);
        Color buttonColor = new Color(70, 130, 180);
        Color buttonHoverColor = new Color(100, 150, 200);
        Color buttonTextColor = Color.WHITE;
        Dimension buttonSize = new Dimension(400, 80);

        // 创建三个按钮
        JButton buttonExplore = createStyledButton("进行探索实验", buttonFont, buttonColor, buttonHoverColor, buttonTextColor, buttonSize);
        JButton buttonReplay = createStyledButton("进行实验回放", buttonFont, buttonColor, buttonHoverColor, buttonTextColor, buttonSize);
        JButton buttonQuit = createStyledButton("退出登录", buttonFont, buttonColor, buttonHoverColor, buttonTextColor, buttonSize);

        // 添加按钮到面板
        buttonPanel.add(buttonExplore);
        buttonPanel.add(buttonReplay);
        buttonPanel.add(buttonQuit);

        // 添加面板到窗口
        frame.add(buttonPanel);

        // 绑定按钮事件
        buttonExplore.addActionListener(e -> {
            Explore_experiments explore_experiments = new Explore_experiments();
            Explore_experiments.Ini_explore();
             frame.dispose();
        });

        buttonReplay.addActionListener(e -> {
            Replay replay = new Replay();
        });

        buttonQuit.addActionListener(e -> {

            int confirm = JOptionPane.showConfirmDialog(frame,
                    "确定要退出系统吗？", "退出确认",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {

                frame.dispose();
                // 显示登录窗口（确保包路径正确）
                SwingUtilities.invokeLater(() -> {
                    try {
                        new Login_register.log().showLogin();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
            }
        });
        frame.setVisible(true);
    }
    // 创建美观按钮的辅助方法
    private JButton createStyledButton(String text, Font font, Color bgColor,
                                       Color hoverColor, Color textColor, Dimension size) {
        JButton button = new JButton(text);
        button.setFont(font);
        button.setBackground(bgColor);
        button.setForeground(textColor);
        button.setPreferredSize(size);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 鼠标悬停效果
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        return button;
    }
}
