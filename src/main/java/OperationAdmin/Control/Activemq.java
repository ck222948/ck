package OperationAdmin.Control;
import javax.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;
import OperationAdmin.Model.Car;
import OperationAdmin.View.MapVisualization;
import redis.clients.jedis.Jedis;

import static OperationAdmin.View.MapVisualization.*;


public class Activemq extends Thread{
    ConnectionFactory connectionFactory;
    Destination destination;
    Session session;
    MessageConsumer consumer;
    MapVisualization.obscate_MapPanel mapPanel;
    private static final String BROKER_URL = "tcp://192.168.43.69:61616"; // ActiveMQ 地址
    private static final String QUEUE_NAME = "UpdateView";       // 队列名称
    public void ActiveMQListener (MapVisualization.obscate_MapPanel obstacle_mapPanel) {
        mapPanel = obstacle_mapPanel;
    }


    @Override
    public void run() {
        Connection connection = null;
        try {
            // 1. 创建连接工厂
            connectionFactory = new ActiveMQConnectionFactory(BROKER_URL);

            // 2. 创建连接并启动
            connection = connectionFactory.createConnection();
            connection.start();

            // 3. 创建会话（非事务性，自动确认消息）
            session= connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // 4. 创建目标队列
            destination= session.createQueue(QUEUE_NAME);

            // 5. 创建消费者
            consumer= session.createConsumer(destination);

            // 6. 注册消息监听器
            consumer.setMessageListener(message -> {
                if (message instanceof TextMessage) {
                    try {
                        String text = ((TextMessage) message).getText();
                        //System.out.println("收到消息: " + text);

                        //map=readMessage("map");
                        map=readBitmap("map");

                        int k=1;
                        for (Car car : cars) {
                            String carPosition = readMessage("Car00"+k);
                            try {
                                int[] result = parseIntegerPair(carPosition);
                                //System.out.println("x=" + result[0] + ", y=" + result[1]);
                                car.setPosition(result[0],result[1]);
                            } catch (IllegalArgumentException e) {
                                System.err.println("错误: " + e.getMessage());
                            }

                            k++;
                        }

                        mapPanel.repaint();
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("收到非文本消息: " + message);
                }
            });

            System.out.println("监听器已启动，等待消息...");

            // 保持主线程运行（实际应用中可能需要其他方式维持）
            Thread.sleep(Long.MAX_VALUE);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 7. 关闭连接
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String readBitmap(String key) {
        String bitmap = "";
        Jedis jedis = new Jedis("192.168.43.69", 6379);
        try {

            bitmap=getBitmapAsBinary(key);
            //bitmap = jedis.get(key);
            //System.out.println(jedis.get(key));

            // 读取数据

            return bitmap;

        } finally {
            // 关闭连接
            jedis.close();
        }



    }
    public static String getBitmapAsBinary(String key) {
        try (Jedis jedis = new Jedis("192.168.43.69", 6379)) {
            byte[] bytes = jedis.get(key.getBytes());
            if (bytes == null) return null;

            StringBuilder binaryStr = new StringBuilder();
            for (byte b : bytes) {
                for (int i = 7; i >= 0; i--) {
                    binaryStr.append((b >> i) & 1);
                }
            }
            return binaryStr.toString();
        }
    }


    public String readMessage(String key) {
        Jedis jedis = new Jedis("192.168.43.69", 6379);
        try {

            // 读取数据
            String value = jedis.get(key);
            //System.out.println(value); // 输出: Hello Redis
            return value;

        } finally {
            // 关闭连接
            jedis.close();
        }
    }
    public void writeMessage(String key,String value) {

        Jedis jedis = new Jedis("192.168.43.69", 6379);
        try {

            jedis.set(key,value);


        }catch (Exception e) {
            System.err.println("<wrong>: " + e.getMessage());
        }
        finally {

            jedis.close();
        }

    }

    public static int[] parseIntegerPair(String s) {
        // 步骤1：分割字符串
        String[] parts = s.split(",");
        if (parts.length != 2) {
            throw new IllegalArgumentException("输入应为两个整数，用逗号分隔");
        }

        // 步骤2：处理空格并验证格式
        String xStr = parts[0].trim();
        String yStr = parts[1].trim();
        if (xStr.isEmpty() || yStr.isEmpty()) {
            throw new IllegalArgumentException("空字符串");
        }

        // 步骤3：转换并返回结果
        try {
            return new int[]{
                    Integer.parseInt(xStr),
                    Integer.parseInt(yStr)
            };
        } catch (NumberFormatException e) {
            throw new NumberFormatException("无效整数格式: " + e.getMessage());
        }
    }



}
