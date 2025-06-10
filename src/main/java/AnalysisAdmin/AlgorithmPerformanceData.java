package AnalysisAdmin;

import java.awt.*;
import java.util.UUID;
import java.util.List;

public class AlgorithmPerformanceData {
    /**
     * UUID
     */
    private UUID id;
    /**
     * 算法名称
     */
    private String algorithmName;
    /**
     * 算法规划的实际平均时间，单位为ms
     */
    private double planTime;
    /**
     * <p>算法规划的每一步耗时，单位为ms,格式为“步数，耗时”</p>
     * <p>平均时间根据这个时间计算</p>
     */
    private List<String> stepsWithTimes;
    /**
     * 地图尺寸，单位为格子数
     */
    private Point mapSize;
    /**
     * 障碍物密度，单位为百分比
     */
    private double obstacleDensity;
    public AlgorithmPerformanceData(UUID id, String algorithmName, double planTime, Point mapSize, double obstacleDensity) {
        this.id = id;
        this.algorithmName = algorithmName;
        this.planTime = planTime;
        this.mapSize = mapSize;
        this.obstacleDensity = obstacleDensity;
    }
    public AlgorithmPerformanceData(UUID id, String algorithmName, double planTime, List<String> stepsWithTimes, Point mapSize, double obstacleDensity) {
        this.id = id;
        this.algorithmName = algorithmName;
        this.planTime = planTime;
        this.stepsWithTimes = stepsWithTimes;
        this.mapSize = mapSize;
        this.obstacleDensity = obstacleDensity;
    }
    public AlgorithmPerformanceData() {
        this.id = UUID.randomUUID();
        this.algorithmName = "";
        this.planTime = 0;
        this.mapSize = new Point(0,0);
        this.obstacleDensity = 0;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public void setAlgorithmName(String algorithmName) {
        this.algorithmName = algorithmName;
    }

    public double getPlanTime() {
        return planTime;
    }

    public void setPlanTime(double planTime) {
        this.planTime = planTime;
    }

    public Point getMapSize() {
        return mapSize;
    }

    public void setMapSize(Point mapSize) {
        this.mapSize = mapSize;
    }

    public double getObstacleDensity() {
        return obstacleDensity;
    }

    public void setObstacleDensity(double obstacleDensity) {
        this.obstacleDensity = obstacleDensity;
    }

    public List<String> getStepsWithTimes() {
        return stepsWithTimes;
    }

    public void setStepsWithTimes(List<String> stepsWithTimes) {
        this.stepsWithTimes = stepsWithTimes;
    }
}
