package AnalysisAdmin;



import Login_register.log;

import javax.swing.*;
import java.awt.*;
/**
 * <p>算法性能分析界面</p>
 * <p>包含两个子界面</p>
 */
public class AlgorithmPerformanceFrame extends JFrame {
    public AlgorithmPerformanceFrame() {
        super("算法性能分析");

        // 创建菜单栏
        JMenuBar menuBar = new JMenuBar();

        // 创建文件菜单
        JMenu fileMenu = new JMenu("文件");

        JMenuItem exitItem = new JMenuItem("退出");
        exitItem.addActionListener(e -> {
             this.dispose();
             new log().showLogin();
        });
        fileMenu.add(exitItem);

        // 创建编辑菜单
        JMenu editMenu = new JMenu("编辑");
        JMenuItem batchDeleteItem = new JMenuItem("批量删除");
        batchDeleteItem.addActionListener(e -> {
            DeleteDialog deleteDialog = new DeleteDialog(this, "批量删除", true);
        });
        editMenu.add(batchDeleteItem);

        // 添加菜单到菜单栏
        menuBar.add(fileMenu);
        menuBar.add(editMenu);

        // 设置菜单栏
        this.setJMenuBar(menuBar);

        // 创建选项卡面板
        JTabbedPane tabbedPane = new JTabbedPane();

        // 添加两个图表面板作为不同的标签页
        tabbedPane.addTab("步骤时间分析", new AlgorithmStepTimeChartPanel());
        tabbedPane.addTab("算法比较", new AlgorithmCompareChartPanel());

        this.add(tabbedPane);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getContentPane().setBackground(new Color(70, 130, 180));
        this.pack();
        // 设置窗口居中显示
        this.setLocationRelativeTo(null);
    }

}