package OperationAdmin.Control;


import OperationAdmin.View.MapVisualization;

public class Explore_experiments {
    public Explore_experiments() {

    }
    public static void Ini_explore() {
        MapVisualization visualizer = new MapVisualization();
        visualizer.createAndShowGUI();
    }
}
