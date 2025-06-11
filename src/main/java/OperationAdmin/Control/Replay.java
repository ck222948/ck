package OperationAdmin.Control;

import OperationAdmin.View.MapReplay;

import javax.swing.*;

import static OperationAdmin.View.MapVisualization.*;

public class Replay {
    public Replay() {
        MapReplay mapReplay = new MapReplay();
        if (obstacle_map == null || obstacle_map.length() < MAP_WIDTH * MAP_HEIGHT) {
            JFrame frame = new JFrame("多车地图回放");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            JOptionPane.showMessageDialog(frame, "地图为空或无效", "错误", JOptionPane.ERROR_MESSAGE);
        }
        else {
            mapReplay.createAndShowGUI();
        }
    }
}
