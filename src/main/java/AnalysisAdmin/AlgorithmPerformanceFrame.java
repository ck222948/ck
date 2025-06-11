package AnalysisAdmin;

import javax.swing.*;
import java.awt.*;
/**
 * <p>算法性能分析界面</p>
 * <p>包含两个子界面</p>
 */
public class AlgorithmPerformanceFrame extends JFrame {
    public AlgorithmPerformanceFrame() {
        super("算法性能分析");

        // 创建选项卡面板
        JTabbedPane tabbedPane = new JTabbedPane();
        setLayout(new BorderLayout());
        // 添加两个图表面板作为不同的标签页
        tabbedPane.addTab("步骤时间分析", new AlgorithmStepTimeChartPanel());
        tabbedPane.addTab("算法比较", new AlgorithmCompareChartPanel());

        // 创建退出按钮
        JButton exitButton = new JButton("退出");
        exitButton.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        exitButton.setPreferredSize(new Dimension(100, 35));
        exitButton.setBackground(new Color(70, 130, 180));
        exitButton.setForeground(Color.WHITE);
        exitButton.setFocusPainted(false);
        exitButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(50, 110, 160), 1),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        add(tabbedPane, BorderLayout.CENTER);
        add(exitButton, BorderLayout.SOUTH);
        // 为退出按钮添加动作监听器
        exitButton.addActionListener(e -> {
            System.out.println("尝试退出..."); // 调试
            this.dispose(); // 关闭当前窗口

            // 显示登录窗口（确保包路径正确）
            SwingUtilities.invokeLater(() -> {
                try {
                    new Login_register.log().showLogin();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        });

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getContentPane().setBackground(new Color(70, 130, 180));
        this.pack();
        // 使窗口居中显示
        this.setLocationRelativeTo(null);


    }
}