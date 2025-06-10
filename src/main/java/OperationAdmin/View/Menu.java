package OperationAdmin.View;



import OperationAdmin.Control.Explore_experiments;
import OperationAdmin.Control.Replay;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Menu extends JFrame {
    int width = 1000;
    int height = 700;

    public void Menu() {
        JFrame frame = new JFrame("菜单");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(width, height);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new java.awt.FlowLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        setResizable(false);
        frame.setLayout(null); // 禁用布局管理器
        // 创建按钮
        JButton button_explore = new JButton("进行探索实验");

        button_explore.setBounds(550, 150, 500, 150); // x=100, y=50, 宽=200, 高=80

        // 绑定点击事件（使用匿名内部类）
        button_explore.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Explore_experiments explore_experiments = new Explore_experiments();
                Explore_experiments.Ini_explore();
                //frame.setVisible(false);

            }
        });
        // 添加按钮到窗口
        frame.add(button_explore);
        JButton button_back = new JButton("进行实验回放");
        button_back.setBounds(550, 450, 500, 150); // x=100, y=50, 宽=200, 高=80

        // 绑定点击事件（使用匿名内部类）
        button_back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 点击后的逻辑

                Replay replay = new Replay();
            }
        });
        // 添加按钮到窗口
        frame.add(button_back);

        frame.add(button_explore);
        JButton button_quit = new JButton("退出登录");
        button_quit.setBounds(550, 750, 500, 150); // x=100, y=50, 宽=200, 高=80

        // 绑定点击事件（使用匿名内部类）
        button_quit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 点击后的逻辑
                JOptionPane.showMessageDialog(frame, "退出登录按钮被点击了！");
            }
        });
        // 添加按钮到窗口
        frame.add(button_quit);


        // 显示窗口（确保在事件分派线程中运行）
        SwingUtilities.invokeLater(() -> frame.setVisible(true));

    }


}
