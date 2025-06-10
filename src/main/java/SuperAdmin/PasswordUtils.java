package SuperAdmin;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtils {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int SALT_LENGTH = 16;

    /**
     * SHA-256加密（带盐值）
     */
    public static String sha256WithSalt(String password) {
        try {
            // 生成随机盐
            byte[] salt = new byte[SALT_LENGTH];
            RANDOM.nextBytes(salt);

            // 组合密码和盐
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] digest = md.digest(password.getBytes(StandardCharsets.UTF_8));

            // 返回格式: salt + digest
            byte[] combined = new byte[salt.length + digest.length];
            System.arraycopy(salt, 0, combined, 0, salt.length);
            System.arraycopy(digest, 0, combined, salt.length, digest.length);

            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception ex) {
            throw new RuntimeException("密码加密失败", ex);
        }
    }

    /**
     * 验证密码（与加密存储的密码比对）
     */
    public static boolean verifyPassword(String inputPassword, String storedPassword) {
        try {
            // 解码Base64
            byte[] combined = Base64.getDecoder().decode(storedPassword);

            // 提取盐
            byte[] salt = new byte[SALT_LENGTH];
            System.arraycopy(combined, 0, salt, 0, SALT_LENGTH);

            // 计算输入密码的哈希
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] inputHash = md.digest(inputPassword.getBytes(StandardCharsets.UTF_8));

            // 提取存储的哈希
            byte[] storedHash = new byte[combined.length - SALT_LENGTH];
            System.arraycopy(combined, SALT_LENGTH, storedHash, 0, storedHash.length);

            // 比对哈希值
            return MessageDigest.isEqual(inputHash, storedHash);
        } catch (Exception ex) {
            return false;
        }
    }
}