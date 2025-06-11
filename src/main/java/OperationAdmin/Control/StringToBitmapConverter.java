package OperationAdmin.Control;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class StringToBitmapConverter {
    private static final JedisPool jedisPool = new JedisPool("192.168.43.69", 6379);

    /**
     * 将二进制字符串保存为 Redis Bitmap
     * @param key Redis 键
     * @param binaryString 二进制字符串（只包含 '0' 和 '1'）
     */
    public static void saveBinaryStringAsBitmap(String key, String binaryString) {
        // 1. 验证输入
        if (binaryString == null || binaryString.isEmpty()) {
            throw new IllegalArgumentException("二进制字符串不能为空");
        }

        try (Jedis jedis = jedisPool.getResource()) {
            // 3. 删除旧数据（可选）
            jedis.del(key);

            // 4. 将二进制字符串转换为字节数组
            byte[] bytes = convertBinaryStringToBytes(binaryString);

            // 5. 保存到 Redis
            jedis.set(key.getBytes(), bytes);
        } catch (Exception e) {
            System.err.println("保存到Redis失败: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }


    /**
     * 将二进制字符串转换为字节数组
     */
    private static byte[] convertBinaryStringToBytes(String binaryString) {
        int totalBits = binaryString.length();
        int byteSize = (totalBits + 7) / 8; // 计算所需字节数
        byte[] bytes = new byte[byteSize];

        // 处理每个字节
        for (int byteIndex = 0; byteIndex < byteSize; byteIndex++) {
            byte b = 0;
            // 处理每个比特位（8位一组）
            for (int bitIndex = 0; bitIndex < 8; bitIndex++) {
                int globalIndex = byteIndex * 8 + bitIndex;
                if (globalIndex < totalBits) {
                    char c = binaryString.charAt(globalIndex);
                    if (c == '1') {
                        b |= (1 << (7 - bitIndex)); // 设置比特位
                    }
                }
            }
            bytes[byteIndex] = b;
        }
        return bytes;
    }
}
