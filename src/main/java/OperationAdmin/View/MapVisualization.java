package OperationAdmin.View;

import OperationAdmin.Control.Activemq;
import OperationAdmin.Control.StringToBitmapConverter;
import OperationAdmin.Model.Car;
import redis.clients.jedis.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MapVisualization {


    public static List<Car> cars = new ArrayList<>();
    Activemq activeMQListenerService;




    public static int MAP_WIDTH = 10;
    public static int MAP_HEIGHT = 10;
    static int CELL_SIZE = 40;
    private static int tage = 0;
    static final Color[] TERRAIN_COLORS = {
            new Color(255, 255, 255),
            new Color(139, 69, 19),
            new Color(34, 139, 34)


    };
    static final Color[] Map_COLORS = {

            new Color(0, 0, 0),
            new Color(255, 255, 255)

    };
    public static String obstacle_map;
    public static String map;


    void draw_black() {
        int x=MAP_HEIGHT*MAP_WIDTH;
        String temp = "";
        for(int y=0;y<x;y++) {
            temp=temp+"0";
        }
        map= temp;

    }


    public void createAndShowGUI() {
        activeMQListenerService = new Activemq();



        JLabel labelWidth = new JLabel("宽度:");
        JTextField textWidth = new JTextField("10", 5);  // 默认值10
        JLabel labelHeight = new JLabel("高度:");
        JTextField textHeight = new JTextField("10", 5); // 默认值10
        JLabel labelcar_Num = new JLabel("添加小车数量:");
        JTextField textlabelcar_Num = new JTextField("1", 5);  // 默认值1
        JButton buttonConfirm = new JButton("修改地图尺寸");
        JLabel GPS_num = new JLabel("导航器数量:");
        JTextField textGPS_num = new JTextField("1", 5);
        JButton buttonGPS = new JButton("修改导航器数量");
        JLabel Plannings = new JLabel("算法选择:");





        labelWidth.setBounds(1050, 40, 80, 30);
        textWidth.setBounds(1130,  40, 100, 30);
        labelHeight.setBounds(1050, 80, 80, 30);
        textHeight.setBounds(1130, 80, 100, 30);
        buttonConfirm.setBounds(1240, 50, 200, 50);
        labelcar_Num.setBounds(1050, 150, 80, 30);
        textlabelcar_Num.setBounds(1130, 155, 100, 30);
        buttonGPS.setBounds(1280, 195, 150, 40);
        GPS_num.setBounds(1050, 200, 80, 30);
        textGPS_num.setBounds(1130, 200, 100, 30);
        Plannings.setBounds(1150, 300, 80, 30);


        draw_black();

        JFrame frame = new JFrame("单字符串地图可视化");
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(null); // 1. 禁用布局管理器

        obscate_MapPanel obstacle_mapPanel = new obscate_MapPanel();
        obstacle_mapPanel.setBounds(0, 0, MAP_WIDTH*(CELL_SIZE+1), MAP_HEIGHT*(CELL_SIZE+1)); // 限制面板区域




        JButton buttonAddCar = new JButton("添加小车");
        buttonAddCar.setBounds(1280, 140, 150, 40);


        // 创建按钮

        JButton button_Customize = new JButton("自定义生成地图");
        button_Customize.setBounds(1050, 380, 400, 120);
        JButton button_explore = new JButton("随机生成地图");
        button_explore.setBounds(1050, 580, 400, 120);
        JButton button_Start = new JButton("开始探索");
        button_Start.setBounds(1050, 780, 400, 120);




        // 添加按钮到窗口
        frame.add(labelWidth);
        frame.add(textWidth);
        frame.add(labelHeight);
        frame.add(textHeight);
        frame.add(buttonConfirm);
        frame.add(button_explore);
        frame.add(button_Customize);
        frame.add(button_Start);
        frame.add(buttonAddCar);
        frame.add(labelcar_Num);
        frame.add(textlabelcar_Num);
        frame.add(buttonGPS);
        frame.add(GPS_num);
        frame.add(textGPS_num);
        frame.add(Plannings);

        // 创建下拉框
        String[] options = {"JPS", "AStar","Dijkstra","BFS","Floyd"};
        JComboBox<String> comboBox = new JComboBox<>(options);

        // 设置默认选中项
        comboBox.setSelectedIndex(0);
        activeMQListenerService.writeMessage("algorithm", String.valueOf(comboBox.getSelectedIndex()));

        // 添加选择监听器
        comboBox.addActionListener(e -> {
            //String selected = (String) comboBox.getSelectedItem();
            activeMQListenerService.writeMessage("algorithm", String.valueOf(comboBox.getSelectedIndex()));
            //int selectedIndex = comboBox.getSelectedIndex();
            //System.out.println("选中的索引是: " + selectedIndex);



            //System.out.println("选中了: " + selected);
        });


        //frame.setLayout(null); // 设置为绝对布局
        comboBox.setBounds(1300, 300, 80, 30);

        frame.add(comboBox);
        //frame.setVisible(true);





        //修改地图大小
        buttonConfirm.addActionListener(e -> {
            try {
                int newWidth = Integer.parseInt(textWidth.getText());
                int newHeight = Integer.parseInt(textHeight.getText());



                MAP_WIDTH = newWidth;
                MAP_HEIGHT = newHeight;
                activeMQListenerService.writeMessage("mapWidth",String.valueOf(MAP_WIDTH));
                activeMQListenerService.writeMessage("mapLength",String.valueOf(MAP_HEIGHT));
                if(MAP_WIDTH>MAP_HEIGHT) {
                    CELL_SIZE=880/MAP_WIDTH;
                }
                else {
                    CELL_SIZE=880/MAP_HEIGHT;
                }


                //customizeMap();
                draw_black();
                obstacle_mapPanel.setBounds(0, 0, MAP_WIDTH*(CELL_SIZE+1), MAP_HEIGHT*(CELL_SIZE+1)); // 限制面板区域






                // 更新面板尺寸
                obstacle_mapPanel.setPreferredSize(new Dimension(
                        MAP_WIDTH * CELL_SIZE + 1,
                        MAP_HEIGHT * CELL_SIZE + 1
                ));
                obstacle_mapPanel.repaint();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "请输入有效整数", "错误", JOptionPane.ERROR_MESSAGE);
            }
        });

        //添加小车
        buttonAddCar.addActionListener(e -> {
            if(obstacle_map==null)
            {
                JOptionPane.showMessageDialog(frame, "您还没有添加地图", "错误", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int cur_Num = Integer.parseInt(textlabelcar_Num.getText());



            int maxAttempts = 1000;
            int x = -1, y = -1;
            boolean positionValid = false;
            for(int j=0;j<cur_Num;j++)
            {
                for (int attempts = 0; attempts < maxAttempts; attempts++) {
                    x = (int) (Math.random() * MAP_WIDTH);
                    y = (int) (Math.random() * MAP_HEIGHT);

                    int index = y * MAP_WIDTH + x;
                    //System.out.println(x+","+y);


                    // 检查坐标有效性和可通行性
                    if (index >= obstacle_map.length() || obstacle_map.charAt(index) != '0') {

                        continue; // 跳过障碍物或越界
                    }

                    // 检查是否与其他小车位置冲突
                    boolean collision = false;
                    for (Car car : cars) {
                        if (car.getX() == x && car.getY() == y) {
                            collision = true;
                            break;
                        }
                    }
                    if (collision) continue;


                    positionValid = true;
                    break;



                }
                if (positionValid) {

                    String id = "Car00" + (cars.size() + 1);
                    cars.add(new Car(x, y, id));
                    //System.out.println(x+","+y);
                    activeMQListenerService.writeMessage(id,x+","+y);
                    activeMQListenerService.writeMessage("CarNumber", String.valueOf(cars.size()));
                    obstacle_mapPanel.repaint();
                } else {

                    JOptionPane.showMessageDialog(frame, "无法找到有效位置", "错误", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }




        });

        buttonGPS.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int NaviNumber = Integer.parseInt(textGPS_num.getText());


                    if (NaviNumber < 1 || NaviNumber > 10) {
                        JOptionPane.showMessageDialog(frame, "请输入1-10之间的整数", "错误", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    activeMQListenerService.writeMessage("NaviNumber", String.valueOf(NaviNumber));






                    // 更新面板尺寸
                    obstacle_mapPanel.setPreferredSize(new Dimension(
                            MAP_WIDTH * CELL_SIZE + 1,
                            MAP_HEIGHT * CELL_SIZE + 1
                    ));
                    obstacle_mapPanel.repaint();

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "请输入有效整数", "错误", JOptionPane.ERROR_MESSAGE);
                }

            }
        });

        button_explore.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tage=1;
                initializeMap(); // 生成初始地图

                obstacle_mapPanel.repaint();

            }
        });

        button_Start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(obstacle_map==null)
                {
                    JOptionPane.showMessageDialog(frame, "您还没有添加地图", "错误", JOptionPane.WARNING_MESSAGE);
                    return;
                }


                tage=2;
                activeMQListenerService.writeMessage("IsViewOpen","1");

                //activeMQListenerService.writeMessage("map",ex_bitmap(map));
                //ex_bitmap(map,"map");
                StringToBitmapConverter.saveBinaryStringAsBitmap("map",map);

                //activeMQListenerService.writeMessage("obstacle_map",);
                //ex_bitmap(obstacle_map,"obstacle_map");
                StringToBitmapConverter.saveBinaryStringAsBitmap("obstacle_map",obstacle_map);
                activeMQListenerService.ActiveMQListener(obstacle_mapPanel);
                activeMQListenerService.start();


                obstacle_mapPanel.repaint();

            }
        });

        button_Customize.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tage=1;
                customizeMap(); // 生成初始地图
                obstacle_mapPanel.repaint();


            }
        });





        // 添加鼠标点击监听
        obstacle_mapPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (tage==1) {
                    int col = e.getX() / CELL_SIZE;
                    int row = e.getY() / CELL_SIZE;

                    if (row >= 0 && row < MAP_HEIGHT && col >= 0 && col < MAP_WIDTH) {
                        // 计算一维索引
                        int index = row * MAP_WIDTH + col;

                        // 切换地形状态（0↔1）
                        char[] chars = obstacle_map.toCharArray();
                        chars[index] = (chars[index] == '0') ? '1' : '0';
                        obstacle_map = new String(chars);

                        obstacle_mapPanel.repaint();
                        //frame.add(button_explore);
                    }






                }

            }
        });

        frame.add(obstacle_mapPanel);
        frame.setSize(1600, 1200); // 手动设置窗口大小
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);




    }

    private void ex_bitmap(String map,String key) {
        Jedis jedis = new Jedis("192.168.43.69", 6379);
        try {


            for (int i = 0; i < map.length(); i++) {

                if(map.charAt(i) =='0') {
                    jedis.setbit(key,i,false);

                }
                else {
                    jedis.setbit(key,i,true);
                }

            }



        }catch (Exception e) {
            System.err.println("<wrong>: " + e.getMessage());
        }
        finally {

            jedis.close();
        }



    }

    private void customizeMap() {
        int x=MAP_HEIGHT*MAP_WIDTH;
        String temp = "";
        for(int y=0;y<x;y++) {
            temp=temp+"0";
        }
        obstacle_map= temp;
    }

    private void initializeMap() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(MAP_WIDTH * MAP_HEIGHT);

        for (int i = 0; i < MAP_WIDTH * MAP_HEIGHT; i++) {
            // 10%概率生成障碍物，否则平地
            sb.append(random.nextDouble() < 0.1 ? '1' : '0');
        }
        obstacle_map = sb.toString();
    }


    public class obscate_MapPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            for (int row = 0; row < MAP_HEIGHT; row++) {
                for (int col = 0; col < MAP_WIDTH; col++) {
                    boolean flage_text=false;
                    // 计算一维索引
                    int index = row * MAP_WIDTH + col;

                    switch (tage){
                        case 0:{
                            int terrainType = Character.getNumericValue(map.charAt(index));
                            g.setColor(Map_COLORS[terrainType]);
                            break;
                        }
                        case 1:{
                            // 获取地形类型（字符转换为数值）
                            int terrainType = Character.getNumericValue(obstacle_map.charAt(index));
                            // 设置颜色
                            g.setColor(TERRAIN_COLORS[terrainType]);
                            break;
                        }
                        case 2:{
                            int terrainType_map = Character.getNumericValue(map.charAt(index));
                            int terrainType_obscate_map = Character.getNumericValue(obstacle_map.charAt(index));


                            for (Car car : cars) {

                                int x = car.getX();  // 获取当前小车的 x 坐标
                                int y = car.getY();  // 获取当前小车的 y 坐标

                                if(x==col && y==row)
                                {

                                    g.setColor(TERRAIN_COLORS[2]);
                                    g.fillRect(
                                            col * CELL_SIZE,
                                            row * CELL_SIZE,
                                            CELL_SIZE - 1,  // 留1像素边框
                                            CELL_SIZE - 1
                                    );
                                    g.setColor(Color.WHITE); // 白色文字（确保与小车颜色对比）
                                    g.setFont(new Font("Arial", Font.BOLD, 12)); // 设置字体
                                    // 计算文字位置（居中）
                                    int textX = col * CELL_SIZE +2; // 根据字体大小微调
                                    int textY = row * CELL_SIZE +CELL_SIZE/2+2;
                                    g.drawString(car.getId(), textX, textY);

                                    flage_text=true;
                                    break;
                                }
                                else{
                                    if(terrainType_map==0)
                                    {
                                        g.setColor(Map_COLORS[0]);
                                    }
                                    else if(terrainType_obscate_map==1)
                                    {
                                        g.setColor(TERRAIN_COLORS[1]);
                                    }
                                    else
                                    {
                                        g.setColor(Map_COLORS[1]);
                                    }

                                }


                            }


                        }
                    }




                    if(!flage_text){
                        // 绘制单元格
                        g.fillRect(
                                col * CELL_SIZE,
                                row * CELL_SIZE,
                                CELL_SIZE - 1,  // 留1像素边框
                                CELL_SIZE - 1
                        );
                    }


                }
            }
        }
    }



}