package OperationAdmin.Model;

public class Position {
    public final int x;
    public final int y;
    public final long timestamp;

    public Position(int x, int y, long timestamp) {
        this.x = x;
        this.y = y;
        this.timestamp = timestamp;
    }

    public static Position parsePosition(String str) {
        String[] parts = str.split("\\|");
        String[] coords = parts[0].split(",");
        int x = Integer.parseInt(coords[0]);
        int y = Integer.parseInt(coords[1]);
        long timestamp = Long.parseLong(parts[1]);
        System.out.println("x=" + x + ", y=" + y + ", timestamp=" + timestamp);
        return new Position(x, y, timestamp);
    }
}
