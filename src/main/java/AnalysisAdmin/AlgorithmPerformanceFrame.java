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
        
        // 添加两个图表面板作为不同的标签页
        tabbedPane.addTab("步骤时间分析", new AlgorithmStepTimeChartPanel());
        tabbedPane.addTab("算法比较", new AlgorithmCompareChartPanel());
        
        this.add(tabbedPane);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getContentPane().setBackground(new Color(70, 130, 180));
        this.pack();
    }
}