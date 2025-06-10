package AnalysisAdmin;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * <p>用于展示一局算法性能分析数据</p>
 */
public class AlgorithmStepTimeChartPanel extends JPanel {
    private int index;
    private List<AlgorithmPerformanceData> performanceDataList;
    private AlgorithmPerformanceService service;
    private JComboBox<String> dataSelector;
    private JLabel infoLabel;
    private ChartPanel chartPanel;

    public AlgorithmStepTimeChartPanel() {
        service = new AlgorithmPerformanceService();
        this.performanceDataList = service.getAllData();
        this.index = 0;
        chartPanel = new ChartPanel(null);

        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        add(chartPanel, BorderLayout.CENTER);
        // 创建顶部控制面板
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(70, 130, 180));
        topPanel.setForeground(Color.WHITE);
        
        // 添加数据选择器
        dataSelector = new JComboBox<>();
        dataSelector.setBackground(new Color(70, 130, 180));
        dataSelector.setForeground(Color.WHITE);
        dataSelector.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                return label;
            }
        });

        for (int i = 0; i < performanceDataList.size(); i++) {
            AlgorithmPerformanceData data = performanceDataList.get(i);
            dataSelector.addItem("对局 " + (i+1) + ": " + data.getAlgorithmName());
        }
        dataSelector.addActionListener(e -> {
            index = dataSelector.getSelectedIndex();
            updateChartAndInfo();
        });
        topPanel.add(dataSelector, BorderLayout.NORTH);
        
        // 添加信息显示标签
        infoLabel = new JLabel();
        infoLabel.setBackground(new Color(70, 130, 180));
        topPanel.add(infoLabel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);
        
        updateChartAndInfo();
    }
    
    private void updateChartAndInfo() {
        AlgorithmPerformanceData performanceData = performanceDataList.get(index);
        
        // 更新信息标签
        Point mapSize = performanceData.getMapSize();
        String infoText = String.format("算法: %s | 地图尺寸: %dx%d | 障碍物密度: %.2f%%",
            performanceData.getAlgorithmName(), 
            (int)mapSize.getX(), (int)mapSize.getY(), 
            performanceData.getObstacleDensity() * 100);
        infoLabel.setText(infoText);
        
        // 创建图表
        XYSeries series = new XYSeries("起点、终点距离差");
        List<String> stepsWithTimes = performanceData.getStepsWithTimes();
        if (stepsWithTimes != null) {
            for (String stepWithTime : stepsWithTimes) {
                String[] parts = stepWithTime.split(";");
                for(int k = 0; k < parts.length; k++) {
                    String step = parts[k];
                    String[] part = step.split(",");
                    if (part.length == 2) {
                        double x = Double.parseDouble(part[0]);
                        double y = Double.parseDouble(part[1]);
                        series.add(x, y);
                    }
                }
            }
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);

        JFreeChart chart = ChartFactory.createScatterPlot(
                "单局算法性能分析展示",
                "起点、终点距离差",
                "耗费时间 (ms)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        chartPanel.setChart(chart);

        java.awt.Font font = new java.awt.Font("Microsoft YaHei", java.awt.Font.PLAIN, 12);
        chart.getTitle().setFont(font);
        XYPlot plot = chart.getXYPlot();
        plot.setDomainAxis(new NumberAxis("起点、终点距离差(格子数)"));  // X轴
        plot.setRangeAxis(new NumberAxis("耗费时间(ms)"));  // Y轴
        plot.getDomainAxis().setLabelFont(font);
        plot.getRangeAxis().setLabelFont(font);
        chart.getLegend().setItemFont(font);
        chart.setBackgroundPaint(new Color(70, 130, 180));
        //chart.getXYPlot().setBackgroundPaint(Color.WHITE);
        chart.getXYPlot().setDomainGridlinePaint(Color.WHITE);
        chart.getXYPlot().setRangeGridlinePaint(Color.WHITE);
    }
}