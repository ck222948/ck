package OperationAdmin.View;


import OperationAdmin.Model.Position;
import redis.clients.jedis.Jedis;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;

import static OperationAdmin.View.MapVisualization.*;

public class MapReplay {

    private List<List<Position>> carPaths = new ArrayList<>();
    private int[] currentSteps;
    private Timer replayTimer;

    private JSlider progressSlider;
    private boolean isSliderBeingDragged = false;

    // 新增播放控制按钮
    private JButton playPauseButton;
    private boolean isPlaying = false;

    public void createAndShowGUI() {
        JFrame frame = new JFrame("多车地图回放");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(null);

        MapPanel mapPanel = new MapPanel();
        int panelWidth = MAP_WIDTH * (CELL_SIZE + 1);
        int panelHeight = MAP_HEIGHT * (CELL_SIZE + 1);
        mapPanel.setBounds(0, 0, panelWidth, panelHeight);
        frame.add(mapPanel);

        // 添加进度条，放在地图下方
        progressSlider = new JSlider(0, 100, 0);
        progressSlider.setBounds(10, panelHeight , panelWidth - 120, 40);
        progressSlider.setEnabled(false);
        frame.add(progressSlider);

        // 添加播放/暂停按钮，放在进度条右侧
        playPauseButton = new JButton("播放");
        playPauseButton.setBounds(panelWidth - 100, panelHeight , 90, 40);
        playPauseButton.setEnabled(false);
        frame.add(playPauseButton);

        // 进度条监听
        progressSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (progressSlider.getValueIsAdjusting()) {
                    isSliderBeingDragged = true;
                    int val = progressSlider.getValue();
                    for (int i = 0; i < currentSteps.length; i++) {
                        currentSteps[i] = Math.min(val, carPaths.get(i).size());
                    }
                    mapPanel.repaint();
                    // 拖动时自动暂停播放
                    if (isPlaying) {
                        pauseReplay();
                    }
                } else {
                    isSliderBeingDragged = false;
                }
            }
        });

        // 按钮监听，点击切换播放状态
        playPauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isPlaying) {
                    pauseReplay();
                } else {
                    resumeReplay();
                }
            }
        });

        frame.setSize(panelWidth + 16, panelHeight + 80);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Redis keys
        List<String> redisKeys = new ArrayList<>();
        for (int i = 1; i <= cars.size(); i++) {
            String key = String.format("Car00"+ i+"path");
            redisKeys.add(key);
        }
        loadCarPathsFromRedis(redisKeys);

        startReplay(mapPanel);
    }

    private void loadCarPathsFromRedis(List<String> redisKeys) {
        try (Jedis jedis = new Jedis("192.168.43.69", 6379)) {
            carPaths.clear();
            for (String key : redisKeys) {
                List<String> rawPath = jedis.lrange(key, 0, -1);
                jedis.del(key);
                List<Position> path = new ArrayList<>();
                for (String posStr : rawPath) {
                    Position pos = Position.parsePosition(posStr);
                    path.add(pos);
                }
                carPaths.add(path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private long replayStartTime;
    private long firstPointTime;

    public void startReplay(MapPanel mapPanel) {
        if (carPaths.isEmpty()) return;

        currentSteps = new int[carPaths.size()];

        firstPointTime = Long.MAX_VALUE;
        for (List<Position> path : carPaths) {
            if (!path.isEmpty()) {
                firstPointTime = Math.min(firstPointTime, path.get(0).timestamp);
            }
        }

        int maxPathLength = carPaths.stream().mapToInt(List::size).max().orElse(0);
        progressSlider.setMaximum(maxPathLength);
        progressSlider.setValue(0);
        progressSlider.setEnabled(true);
        playPauseButton.setEnabled(true);
        isPlaying = false;
        playPauseButton.setText("播放");

        replayStartTime = System.currentTimeMillis();

        replayTimer = new Timer(20, e -> {
            if (!isPlaying) return;  // 播放暂停时不更新

            long elapsed = System.currentTimeMillis() - replayStartTime;

            boolean allFinished = true;
            for (int i = 0; i < carPaths.size(); i++) {
                List<Position> path = carPaths.get(i);
                if (currentSteps[i] < path.size()) {
                    Position nextPos = path.get(currentSteps[i]);
                    long relativeTime = nextPos.timestamp - firstPointTime;

                    if (elapsed >= relativeTime) {
                        currentSteps[i]++;
                        allFinished = false;
                    } else if (currentSteps[i] < path.size()) {
                        allFinished = false;
                    }
                }
            }

            mapPanel.repaint();

            if (!isSliderBeingDragged) {
                int maxCurrentStep = 0;
                for (int step : currentSteps) {
                    if (step > maxCurrentStep) maxCurrentStep = step;
                }
                progressSlider.setValue(maxCurrentStep);
            }

            if (allFinished) {
                pauseReplay();
                progressSlider.setValue(progressSlider.getMaximum());
            }
        });

        replayTimer.start();
    }

    private void pauseReplay() {
        isPlaying = false;
        playPauseButton.setText("继续");
    }

    private void resumeReplay() {
        isPlaying = true;
        playPauseButton.setText("暂停");
        // 重置起始时间，保证播放从当前步骤时间点继续
        replayStartTime = System.currentTimeMillis() - getCurrentMaxRelativeTime();
    }

    private long getCurrentMaxRelativeTime() {
        long maxRelativeTime = 0;
        for (int i = 0; i < carPaths.size(); i++) {
            List<Position> path = carPaths.get(i);
            int step = currentSteps[i] - 1;
            if (step >= 0 && step < path.size()) {
                long relativeTime = path.get(step).timestamp - firstPointTime;
                if (relativeTime > maxRelativeTime) maxRelativeTime = relativeTime;
            }
        }
        return maxRelativeTime;
    }

    public class MapPanel extends JPanel {
        private final Color[] CAR_COLORS = {
                Color.RED, new Color(0, 120, 255), Color.GREEN, Color.ORANGE, Color.MAGENTA, Color.CYAN
        };

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            for (int row = 0; row < MAP_HEIGHT; row++) {
                for (int col = 0; col < MAP_WIDTH; col++) {
                    int index = row * MAP_WIDTH + col;
                    g.setColor(Map_COLORS[1]);
                    g.fillRect(col * CELL_SIZE, row * CELL_SIZE, CELL_SIZE - 1, CELL_SIZE - 1);
                }
            }
            for (int row = 0; row < MAP_HEIGHT; row++) {
                for (int col = 0; col < MAP_WIDTH; col++) {
                    int index = row * MAP_WIDTH + col;
                    int terrainType = Character.getNumericValue(obstacle_map.charAt(index));
                    if (terrainType == 1) {
                        g.setColor(TERRAIN_COLORS[1]);
                        g.fillRect(col * CELL_SIZE, row * CELL_SIZE, CELL_SIZE - 1, CELL_SIZE - 1);
                    }
                }
            }

            int markerSize = (int) (CELL_SIZE * 0.6);
            markerSize = Math.min(markerSize, CELL_SIZE);
            markerSize = Math.max(markerSize, 1);
            int offset = (CELL_SIZE - markerSize) / 2;

            for (int carId = 0; carId < carPaths.size(); carId++) {
                List<Position> path = carPaths.get(carId);
                Color carColor = CAR_COLORS[carId % CAR_COLORS.length];

                g.setColor(new Color(carColor.getRed(), carColor.getGreen(), carColor.getBlue(), 150));
                for (int i = 0; i < currentSteps[carId] - 1; i++) {
                    Position p = path.get(i);
                    g.fillOval(
                            p.x * CELL_SIZE + offset,
                            p.y * CELL_SIZE + offset,
                            markerSize,
                            markerSize);
                }

                if (currentSteps[carId] > 0 && currentSteps[carId] <= path.size()) {
                    Position p = path.get(currentSteps[carId] - 1);
                    g.setColor(carColor);
                    g.fillRect(
                            p.x * CELL_SIZE + offset,
                            p.y * CELL_SIZE + offset,
                            markerSize,
                            markerSize);
                }
            }
        }
    }
}
