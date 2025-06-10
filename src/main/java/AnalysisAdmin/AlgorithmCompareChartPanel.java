package AnalysisAdmin;

import org.jfree.chart.*;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;

/**
 * <p>算法性能图表面板</p>
 * <p>展示所有对局的数据</p>
 */
public class AlgorithmCompareChartPanel extends JPanel {
    /**
     * 顶部左上角下拉列表，设置表格横坐标
     */
    private JComboBox<String> sortComboBox;
    /**
     * 顶部右上角下拉列表，设置除了横坐标以外的自变量为固定的值
     */
    private JComboBox<String> filterComboBox;
    /**
     * 顶部右上角下拉列表的标签
     */
    private JLabel filterLabel;
    /**
     * 顶部左上角下拉列表的标签
     */
    private JLabel sortLabel;
    /**
     * 地图尺寸选项，需要读取外部数据
     */
    private Vector<String> sizeOptions;
    /**
     * 障碍物密度选项，需要读取外部数据
     */
    private Vector<String> densityOptions;
    /**
     * 实际数据，需要从redis（缓存）中读取或从数据库中读取
     */
    private List<AlgorithmPerformanceData> performanceDataList;
    /**
     * 算法数据服务类，用于对算法性能数据进行crud操作
     */
    private AlgorithmPerformanceService performanceService;

    private JPanel controllerPanel;
    private ChartPanel chartPanel;
    private Logger logger = LoggerFactory.getLogger(AlgorithmCompareChartPanel.class.getName());
    public AlgorithmCompareChartPanel() {
        init();
    }

    public void init()
    {
        performanceService = new AlgorithmPerformanceService();

        sizeOptions = new Vector<String>();
        densityOptions = new Vector<String>();

        sortLabel = new JLabel("排序方式");
        sortLabel.setForeground(Color.WHITE);
        filterLabel = new JLabel("设置障碍物密度");
        filterLabel.setForeground(Color.WHITE);



        performanceDataList = performanceService.getAllData();
        //performanceDataList = new ArrayList<>();
        XYSeriesCollection dataset = new XYSeriesCollection();


        if(null != performanceDataList && !performanceDataList.isEmpty()) {
            Map<String, XYSeries> seriesMap = new HashMap<>();
            for (AlgorithmPerformanceData data : performanceDataList) {
                String algorithmName = data.getAlgorithmName();
                double obstacleDensity = data.getObstacleDensity();
                Point mapSize = data.getMapSize();
                double planTime = data.getPlanTime();

                if(!sizeOptions.contains("(" + (int)mapSize.getX() + "," + (int)mapSize.getY() + ")")) {
                    sizeOptions.add("(" + (int)mapSize.getX() + "," + (int)mapSize.getY() + ")");
                }
                if(!densityOptions.contains(String.valueOf(obstacleDensity))) {
                    densityOptions.add(String.valueOf(obstacleDensity));
                }
                if (!seriesMap.containsKey(algorithmName)) {
                    seriesMap.put(algorithmName, new XYSeries(algorithmName));
                }
                seriesMap.get(algorithmName).add(mapSize.getX() * mapSize.getY(), planTime);
            }
            for (XYSeries series : seriesMap.values()) {
                dataset.addSeries(series);
            }
        }

        sortComboBox = new JComboBox<>(new DefaultComboBoxModel<>(new String[]{"按地图大小", "按障碍物密度"}));
        sortComboBox.setBackground(new Color(70, 130, 180));
        sortComboBox.setForeground(Color.WHITE);
        sortComboBox.addActionListener(e -> {
            if (Objects.equals(sortComboBox.getSelectedItem(), "按地图大小")) {
                setChartByMapSize();
            }
            else {
                setChartByObstacleDensity();
            }
        });
        filterComboBox = new JComboBox<>(new DefaultComboBoxModel<>(densityOptions));
        filterComboBox.setBackground(new Color(70, 130, 180));
        filterComboBox.setForeground(Color.WHITE);

        controllerPanel = new JPanel();
        controllerPanel.add(sortLabel);
        controllerPanel.add(sortComboBox);
        controllerPanel.add(filterLabel);
        controllerPanel.add(filterComboBox);

        chartPanel = new ChartPanel(new JFreeChart("sd", new Font("Microsoft YaHei", Font.PLAIN, 12),
                new XYPlot(), true));
        setChartDataWithMapSize();

        this.setLayout(new BorderLayout());
        add(controllerPanel, BorderLayout.NORTH);
        add(chartPanel, BorderLayout.CENTER);
        chartPanel.setMouseZoomable(true);  // 启用鼠标缩放
        chartPanel.setDisplayToolTips(true);

        chartPanel.addChartMouseListener(new ChartMouseListener() {
            @Override
            public void chartMouseClicked(ChartMouseEvent event) {}

            @Override
            public void chartMouseMoved(ChartMouseEvent event) {
                ChartRenderingInfo info = chartPanel.getChartRenderingInfo();
                Point2D point = event.getTrigger().getPoint();
                EntityCollection entities = info.getEntityCollection();

                if (entities != null) {
                    ChartEntity entity = entities.getEntity(point.getX(), point.getY());
                    if (entity instanceof XYItemEntity xyEntity) {
                        XYDataset dataset = xyEntity.getDataset();
                        int series = xyEntity.getSeriesIndex();
                        int item = xyEntity.getItem();

                        String algorithmName = (String) dataset.getSeriesKey(series);
                        double xValue = dataset.getXValue(series, item);
                        double yValue = dataset.getYValue(series, item);

                        // 使用JToolTip替代默认提示
                        JToolTip tooltip = chartPanel.createToolTip();
                        tooltip.setTipText(String.format("<html><b>%s</b><br>地图大小: %.0f<br>时间: %.2fms</html>",
                                algorithmName, xValue, yValue));
                        chartPanel.setToolTipText(tooltip.getTipText());
                        logger.info("算法名称: {}, 地图大小: {}, 时间: {}", algorithmName, xValue, yValue);
                    } else {
                        chartPanel.setToolTipText(null);
                    }
                }
            }
        });

        // 设置蓝色风格颜色
        Color steelBlue = new Color(70, 130, 180);
        this.setBackground(steelBlue);
        controllerPanel.setBackground(steelBlue);
        chartPanel.setBackground(steelBlue);

    }
    /**
     * <p>将地图尺寸作为X轴，算法规划时间作为Y轴，显示在图表上</p>
     * <p>障碍物密度为不变量，但可以设置为不同的不变量</p>
     */
    public void setChartByMapSize() {
        filterComboBox.setModel(new DefaultComboBoxModel<>( densityOptions));
        filterLabel.setText("设置障碍物密度:");
        chartPanel.getChart().getXYPlot().getDomainAxis().setLabel("地图大小");
        ActionListener[] listeners = filterComboBox.getActionListeners();
        for(ActionListener listener: listeners) {
            filterComboBox.removeActionListener(listener);
        }
        filterComboBox.addActionListener(e -> {
            //查询指定的障碍物密度的数据，并绘制图表
            performanceDataList = performanceService.filterByObstacleDensity(Double.parseDouble((String) filterComboBox.getSelectedItem()));
            setChartDataWithMapSize();
            filterComboBox.updateUI();
        });
        filterComboBox.setModel(new DefaultComboBoxModel<>(densityOptions));
        filterComboBox.setSelectedItem(sizeOptions.get(0));
    }
    /**
     * <p>将障碍物密度作为X轴，算法规划时间作为Y轴，显示在图表上</p>
     * <p>地图尺寸为不变量，但可以设置为不同的不变量</p>
     */
    public void setChartByObstacleDensity() {
        filterComboBox.setModel(new DefaultComboBoxModel<>(sizeOptions));
        filterLabel.setText("设置地图大小:");
        XYPlot plot = chartPanel.getChart().getXYPlot();
        plot.setDomainAxis(new NumberAxis("障碍物密度(百分比)"));  // X轴
        chartPanel.getChart().getXYPlot().getDomainAxis().setLabel("障碍物密度");
        ActionListener[] listeners = filterComboBox.getActionListeners();
        for(ActionListener listener: listeners) {
            filterComboBox.removeActionListener(listener);
        }
        filterComboBox.addActionListener(e -> {
            //查询指定的地图尺寸的数据，并绘制图表
            String mapSizeStr = (String) filterComboBox.getSelectedItem();
            String[] mapSizeStrArr = null;
            if (mapSizeStr != null) {
                mapSizeStrArr = mapSizeStr.split(",");
            }
            Point mapSize = null;
            if (mapSizeStrArr != null) {
                mapSize = new Point(Integer.parseInt(mapSizeStrArr[0].substring(1)),
                        Integer.parseInt(mapSizeStrArr[1].substring(0, mapSizeStrArr[1].length() - 1)));
            }
            if (mapSize != null) {
                performanceDataList = performanceService.filterByMapSize(mapSize);
                setChartDataWithObstacleDensity();
            }
            filterComboBox.updateUI();
        });
        filterComboBox.setModel(new DefaultComboBoxModel<>(sizeOptions));
        filterComboBox.setSelectedItem(sizeOptions.get(0));
    }

    /**
     * <p>将地图尺寸作为X轴根据数据设置图标数据</p>
     */
    public void setChartDataWithMapSize() {
        XYSeriesCollection dataset = new XYSeriesCollection();
        if(null != performanceDataList && !performanceDataList.isEmpty()) {
            Map<String, XYSeries> seriesMap = new HashMap<>();
            for (AlgorithmPerformanceData data : performanceDataList) {
                String algorithmName = data.getAlgorithmName();
                Point mapSize = data.getMapSize();
                double planTime = data.getPlanTime();
                if (!seriesMap.containsKey(algorithmName)) {
                    seriesMap.put(algorithmName, new XYSeries(algorithmName));
                }
                seriesMap.get(algorithmName).add(mapSize.getX() * mapSize.getY(), planTime);
            }

            for (XYSeries series : seriesMap.values()) {
                dataset.addSeries(series);
            }
        }
        JFreeChart chart = ChartFactory.createScatterPlot(
                "算法性能比较",
                "地图大小(格子数)",
                "规划时间(ms)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false);
        chartPanel.setChart(chart);
        java.awt.Font font = new java.awt.Font("Microsoft YaHei", java.awt.Font.PLAIN, 12);
        chart.getTitle().setFont(font);
        XYPlot plot = chart.getXYPlot();
        plot.setDomainAxis(new NumberAxis("地图大小(格子数)"));  // X轴
        plot.setRangeAxis(new NumberAxis("规划时间(ms)"));  // Y轴
        plot.getDomainAxis().setLabelFont(font);
        plot.getRangeAxis().setLabelFont(font);
        chart.getLegend().setItemFont(font);
        chart.setBackgroundPaint(new Color(70, 130, 180));
        //chart.getXYPlot().setBackgroundPaint(Color.WHITE);
        chart.getXYPlot().setDomainGridlinePaint(Color.WHITE);
        chart.getXYPlot().setRangeGridlinePaint(Color.WHITE);
    }
    /**
     * <p>将障碍物密度作为X轴根据数据设置图标数据</p>
     */
    public void setChartDataWithObstacleDensity() {
        XYSeriesCollection dataset = new XYSeriesCollection();
        if (null != performanceDataList && !performanceDataList.isEmpty()) {
            Map<String, XYSeries> seriesMap = new HashMap<>();
            for (AlgorithmPerformanceData data : performanceDataList) {
                String algorithmName = data.getAlgorithmName();
                double obstacleDensity = data.getObstacleDensity();
                double planTime = data.getPlanTime();

                if (!seriesMap.containsKey(algorithmName)) {
                    seriesMap.put(algorithmName, new XYSeries(algorithmName));
                }
                seriesMap.get(algorithmName).add(obstacleDensity, planTime);
            }
            for (XYSeries series : seriesMap.values()) {
                dataset.addSeries(series);
            }
        }
        JFreeChart chart = ChartFactory.createScatterPlot(
                "算法性能比较",
                "障碍物密度（百分比）",
                "规划时间(ms)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false);
        chartPanel.setChart(chart);
        java.awt.Font font = new java.awt.Font("Microsoft YaHei", java.awt.Font.PLAIN, 12);
        chart.getTitle().setFont(font);
        XYPlot plot = chart.getXYPlot();
        plot.setDomainAxis(new NumberAxis("障碍物密度（百分比）"));  // X轴
        plot.setRangeAxis(new NumberAxis("规划时间(ms)"));  // Y轴
        plot.getDomainAxis().setLabelFont(font);
        plot.getRangeAxis().setLabelFont(font);
        chart.getLegend().setItemFont(font);
        chart.setBackgroundPaint(new Color(70, 130, 180));
        //chart.getXYPlot().setBackgroundPaint(Color.WHITE);
        chart.getXYPlot().setDomainGridlinePaint(Color.WHITE);
        chart.getXYPlot().setRangeGridlinePaint(Color.WHITE);
    }
}