package OperationAdmin.Model;

public class Car {
    private int x;      // 列坐标（对应地图的 col）
    private int y;      // 行坐标（对应地图的 row）
    private String id;   // 唯一标识符（可选）

    public Car(int x, int y, String id) {
        this.x = x;
        this.y = y;
        this.id = id;
    }
    // Getter 和 Setter 方法
    public int getX() { return x; }
    public int getY() { return y; }

    public String getId() { return id; }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
