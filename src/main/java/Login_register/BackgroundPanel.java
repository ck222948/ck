package Login_register;

import javax.swing.*;
import java.awt.*;

class BackgroundPanel extends JPanel {
    private Image backgroundImage;

    public BackgroundPanel() {
        try {
            // 加载背景图片（替换为你的图片路径）
            backgroundImage = new ImageIcon(getClass().getResource("/image/image3.jpg")).getImage();

        } catch (Exception e) {
            e.printStackTrace();
            // 如果图片加载失败，使用纯色背景
            setBackground(new Color(70, 130, 180));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        // 先调用父类方法清除原有内容
        super.paintComponent(g);

        // 创建Graphics2D对象以获得更好的渲染质量
        Graphics2D g2d = (Graphics2D) g.create();

        try {
            // 设置抗锯齿
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            if (backgroundImage != null) {
                // 高质量缩放背景图
                g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            } else {
                // 纯色背景
                g2d.setColor(getBackground());
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }

            // 半透明遮罩层
            // 调整半透明层色彩柔和，加入淡灰蓝
            g2d.setColor(new Color(255, 255, 255, 120)); // 柔和白透明
            g2d.fillRoundRect(100, 100, getWidth() - 200, getHeight() - 200, 30, 30);

        } finally {
            g2d.dispose(); // 释放Graphics2D资源
        }
    }
}