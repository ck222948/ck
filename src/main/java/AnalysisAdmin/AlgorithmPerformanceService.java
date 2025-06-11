package AnalysisAdmin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.awt.Point;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * <p>算法性能数据服务类</p>
 */
public class AlgorithmPerformanceService {
    private static final Logger logger = LoggerFactory.getLogger(AlgorithmPerformanceService.class.getName());
    private List<AlgorithmPerformanceData> performanceDataList;

    public AlgorithmPerformanceService() {
        this.performanceDataList = new ArrayList<>();
    }

    /**
     * <p>加载所有算法性能数据</p>
     * <p>先访问redis缓存查询数据，如果没有再转入db查询</p>
     */
    public void loadAllData() {
        // 1. 尝试从Redis获取数据
       /* try (Jedis jedis = new Jedis("172.168.43.69", 6379)) {
            String redisData = jedis.get("algorithm_performance_data");
            if (redisData != null && !redisData.isEmpty()) {
                // 解析Redis数据并填充到performanceDataList
                performanceDataList = parseRedisData(redisData);
                return;
            }
        } catch (Exception e) {
            logger.error("Redis连接失败: {}", e.getMessage());
        }*/

        // 2. Redis无数据则从MySQL查询
        try (Connection connection = MySQLDatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM algorithm_performance";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();

            performanceDataList.clear();
            while (rs.next()) {
                AlgorithmPerformanceData data = new AlgorithmPerformanceData();
                data.setId(UUID.fromString(rs.getString("id")));
                data.setAlgorithmName(rs.getString("algorithm_name"));
                data.setPlanTime(rs.getDouble("plan_time"));
                data.setMapSize(new Point(rs.getInt("map_width"), rs.getInt("map_height")));
                data.setObstacleDensity(rs.getDouble("obstacle_density"));
                List<String> steps = List.of(rs.getString("steps_with_times").split(";"));
                data.setStepsWithTimes(steps);
                performanceDataList.add(data);
            }
        } catch (SQLException e) {
            logger.error("MySQL连接失败: {}", e.getMessage());
        }
    }

    private  List<AlgorithmPerformanceData> parseRedisData(String redisData) {
        // 实现Redis数据解析逻辑
        // 这里需要根据实际Redis存储格式实现
        return new ArrayList<>();
    }

    /**
     * 按地图尺寸筛选数据
     * @param size 地图尺寸
     * @return 匹配的算法性能数据列表
     */
    public List<AlgorithmPerformanceData> filterByMapSize(Point size) {
        String sql = "SELECT * FROM algorithm_performance WHERE map_width = ? AND map_height = ?";
        this.performanceDataList.clear();
        try (Connection connection = MySQLDatabaseConnection.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, (int) size.getX());
            statement.setInt(2, (int) size.getY());
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                AlgorithmPerformanceData data = new AlgorithmPerformanceData();
                data.setId(UUID.fromString(rs.getString("id")));
                data.setAlgorithmName(rs.getString("algorithm_name"));
                data.setPlanTime(rs.getDouble("plan_time"));
                data.setMapSize(new Point(rs.getInt("map_width"), rs.getInt("map_height")));
                data.setObstacleDensity(rs.getDouble("obstacle_density"));
                List<String> steps = List.of(rs.getString("steps_with_times").split(";"));
                data.setStepsWithTimes(steps);
                performanceDataList.add(data);
            }
            return performanceDataList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 按障碍物密度筛选数据
     * @param density 障碍物密度(百分比)
     * @return 匹配的算法性能数据列表
     */
    public List<AlgorithmPerformanceData> filterByObstacleDensity(double density) {
        String sql = "SELECT * FROM algorithm_performance WHERE obstacle_density = ?";
        this.performanceDataList.clear();
        try (Connection connection = MySQLDatabaseConnection.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setDouble(1, density);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                AlgorithmPerformanceData data = new AlgorithmPerformanceData();
                data.setId(UUID.fromString(rs.getString("id")));
                data.setAlgorithmName(rs.getString("algorithm_name"));
                data.setPlanTime(rs.getDouble("plan_time"));
                data.setMapSize(new Point(rs.getInt("map_width"), rs.getInt("map_height")));
                data.setObstacleDensity(rs.getDouble("obstacle_density"));
                List<String> steps = List.of(rs.getString("steps_with_times").split(";"));
                data.setStepsWithTimes(steps);
                performanceDataList.add(data);
            }
            return performanceDataList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取所有性能数据
     * @return 所有算法性能数据列表
     */
    public List<AlgorithmPerformanceData> getAllData() {
        loadAllData();
        return performanceDataList;
    }

    /**
     * 分页获取性能数据
     * @param offset 起始位置
     * @param limit 每页数量
     * @return 分页数据列表
     */
    public List<AlgorithmPerformanceData> getDataByOffset(int offset, int limit) {
        String sql = "SELECT * FROM algorithm_performance LIMIT ?, ?";
        this.performanceDataList.clear();
        try (Connection connection = MySQLDatabaseConnection.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, offset);
            statement.setInt(2, limit);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                AlgorithmPerformanceData data = new AlgorithmPerformanceData();
                data.setId(UUID.fromString(rs.getString("id")));
                data.setAlgorithmName(rs.getString("algorithm_name"));
                data.setPlanTime(rs.getDouble("plan_time"));
                data.setMapSize(new Point(rs.getInt("map_width"), rs.getInt("map_height")));
                data.setObstacleDensity(rs.getDouble("obstacle_density"));
                List<String> steps = List.of(rs.getString("steps_with_times").split(";"));
                data.setStepsWithTimes(steps);
                performanceDataList.add(data);
            }
            return performanceDataList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public void saveData(AlgorithmPerformanceData data) {
        try (Connection connection = MySQLDatabaseConnection.getConnection()) {
            String sql = "INSERT INTO algorithm_performance (id, algorithm_name, plan_time, map_width, map_height, obstacle_density) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, data.getId().toString());
            statement.setString(2, data.getAlgorithmName());
            statement.setDouble(3, data.getPlanTime());
            statement.setInt(4, (int)data.getMapSize().getX());
            statement.setInt(5, (int)data.getMapSize().getY());
            statement.setDouble(6, data.getObstacleDensity());
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("保存数据失败: {}", e.getMessage());
        }
    }
    public void saveData(List<AlgorithmPerformanceData> dataList) {
        for (AlgorithmPerformanceData data : dataList) {
            saveData(data);
        }
    }

    /**
     * 根据id删除数据
     * @param id 要删除的数据id
     */
    public void deleteById(UUID id) {
        try (Connection connection = MySQLDatabaseConnection.getConnection()) {
            String sql = "DELETE FROM algorithm_performance WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, id.toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("删除数据失败: {}", e.getMessage());
        }
    }

    /**
     * <p>保存数据</p>
     * @param stepsWithTimes 格式：^(\d+,\d+)(;\d+,\d+)*$
     * @param algorithmName 算法名称
     * @param planTime 规划耗时
     * @param mapSize 地图尺寸
     * @param obstacleDensity 障碍物密度
     */
    public void saveData(String stepsWithTimes, String algorithmName, double planTime, Point mapSize, double obstacleDensity) {
        try (Connection connection = MySQLDatabaseConnection.getConnection()) {
            String sql = "INSERT INTO algorithm_performance (id, algorithm_name, plan_time, map_width, map_height, obstacle_density, steps_with_times) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            UUID id = UUID.randomUUID();
            statement.setString(1, id.toString());
            statement.setString(2, algorithmName);
            statement.setDouble(3, planTime);
            statement.setInt(4, (int) mapSize.getX());
            statement.setInt(5, (int) mapSize.getY());
            statement.setDouble(6, obstacleDensity);
            statement.setString(7, stepsWithTimes);
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("保存数据失败: {}", e.getMessage());
        }
    }
}