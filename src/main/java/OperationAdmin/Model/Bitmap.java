package OperationAdmin.Model;

public class Bitmap {
    private final int width;
    private final int height;
    private final int totalBits;
    private final int byteSize;
    private byte[] bitmap;
    public Bitmap(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Width and height must be positive integers");
        }
        this.width = width;
        this.height = height;
        this.totalBits = width * height;
        this.byteSize = (totalBits + 7) / 8; // 计算所需字节数
        this.bitmap = new byte[byteSize];    // 初始化为全0
    }

    /**
     * 将二维坐标转换为一维索引
     * @param row 行坐标
     * @param col 列坐标
     * @return 一维索引
     */
    private int getIndex(int row, int col) {
        return row * width + col;
    }

    /**
     * 计算比特所在的字节位置和偏移量
     * @param index 一维索引
     * @return 字节索引和位偏移
     */
    private int[] getBytePosition(int index) {
        int byteIndex = index / 8;
        int bitOffset = index % 8;
        return new int[]{byteIndex, bitOffset};
    }

    /**
     * 获取位图的字节表示(可用于存储)
     * @return 字节数组
     */
    public byte[] getBytes() {
        return bitmap.clone();
    }
}
