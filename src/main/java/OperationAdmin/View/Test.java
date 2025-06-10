package OperationAdmin.View;

import javax.swing.*;

public class Test extends JFrame {

    public void Test() {
        JFrame frame = new JFrame("手动设置按钮坐标和大小");
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(null); // 禁用布局管理器

        // 创建按钮并设置坐标和大小
        JButton button = new JButton("点击我");
        button.setBounds(50, 50, 200, 80); // x=100, y=50, 宽=200, 高=80

        frame.add(button);
        frame.setVisible(true);
    }



}
