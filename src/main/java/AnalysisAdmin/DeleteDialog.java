package AnalysisAdmin;
import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.*;
import java.util.List;
import java.util.UUID;

public class DeleteDialog extends JDialog {
    private JTable table;
    private JComboBox<Integer> pageSizeComboBox;
    private JButton prevButton, nextButton;
    private JTextField pageInputField;
    private JLabel currentPageLabel;
    private JLabel totalRecordsLabel;
    private int currentPage = 1;
    private int totalPages = 100;
    private int totalRecords;
    private AlgorithmPerformanceService service;

    public DeleteDialog(JFrame parent, String title, boolean modal) {
        super(parent, title, modal);
        setSize(600, 500);
        getContentPane().setBackground(new Color(225, 240, 255));

        setLocationRelativeTo(parent);
        service = new AlgorithmPerformanceService();
        totalRecords = service.getAllData().size();
        totalPages = (totalRecords - 1) / 5 + 1;

        table = new JTable() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.setBackground(Color.WHITE);
        table.setGridColor(new Color(100, 149, 237));
        table.setSelectionBackground(new Color(100, 149, 237));
        table.setSelectionForeground(Color.WHITE);
        table.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        JScrollPane scrollPane = new JScrollPane(table);

        // 添加右击菜单
        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.setBackground(new Color(225, 240, 255));
        JMenuItem deleteItem = new JMenuItem("删除选中行");
        deleteItem.setBackground(new Color(225, 240, 255));
        deleteItem.setForeground(new Color(0, 0, 139));
        deleteItem.addActionListener(e -> {
            int[] selectedRows = table.getSelectedRows();
            if(selectedRows.length > 0) {
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                for(int i = selectedRows.length - 1; i >= 0; i--) {
                    UUID id = (UUID) model.getValueAt(selectedRows[i], 0);
                    service.deleteById(id);
                    model.removeRow(selectedRows[i]);
                }
                updateTableData();
            }
        });
        popupMenu.add(deleteItem);

        table.setComponentPopupMenu(popupMenu);

        // 创建分页组件
        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        paginationPanel.setBackground(new Color(225, 240, 255));

        // 每页显示数量下拉框
        pageSizeComboBox = new JComboBox<>(new Integer[]{5, 10, 20, 50});
        pageSizeComboBox.setBackground(Color.WHITE);
        pageSizeComboBox.setForeground(new Color(0, 0, 139));
        pageSizeComboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLoweredBevelBorder(),
                BorderFactory.createEmptyBorder(2, 5, 2, 5)
        ));
        pageSizeComboBox.addActionListener(e -> updateTableData());

        // 上一页/下一页按钮
        prevButton = new JButton("<");
        prevButton.setBackground(new Color(100, 149, 237));
        prevButton.setForeground(Color.WHITE);
        prevButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createRaisedBevelBorder(),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        prevButton.addActionListener(e -> {
            if(currentPage > 1) {
                currentPage--;
                updateTableData();
            }
        });

        totalRecordsLabel = new JLabel();
        totalRecordsLabel.setForeground(new Color(0, 0, 139));
        currentPageLabel = new JLabel();
        currentPageLabel.setForeground(new Color(0, 0, 139));

        nextButton = new JButton(">");
        nextButton.setBackground(new Color(100, 149, 237));
        nextButton.setForeground(Color.WHITE);
        nextButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createRaisedBevelBorder(),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        nextButton.addActionListener(e -> {
            if(currentPage < totalPages) {
                currentPage++;
                updateTableData();
            }
        });

        // 页码输入框
        pageInputField = new JTextField(5);
        pageInputField.setBackground(Color.WHITE);
        pageInputField.setForeground(new Color(0, 0, 139));
        pageInputField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLoweredBevelBorder(),
                BorderFactory.createEmptyBorder(2, 5, 2, 5)
        ));
        ((AbstractDocument)pageInputField.getDocument()).setDocumentFilter(new NumberFilter());
        pageInputField.addActionListener(e -> {
            try {
                int page = Integer.parseInt(pageInputField.getText());
                if(page >= 1 && page <= totalPages) {
                    currentPage = page;
                    updateTableData();
                }
            } catch(NumberFormatException ex) {
                // 忽略无效输入
            }
        });

        paginationPanel.add(new JLabel("每页显示:"));
        paginationPanel.add(pageSizeComboBox);
        paginationPanel.add(prevButton);
        paginationPanel.add(currentPageLabel);
        paginationPanel.add(nextButton);
        paginationPanel.add(new JLabel("跳转到:"));
        paginationPanel.add(pageInputField);
        paginationPanel.add(totalRecordsLabel);
        pageSizeComboBox.setSelectedItem(0);
        updateTableData();
        // 添加组件到对话框
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(paginationPanel, BorderLayout.SOUTH);

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setVisible(true);

    }

    private void updateTableData() {
        // 获取当前页码和每页行数
        int limit = Integer.parseInt(pageSizeComboBox.getSelectedItem().toString());
        int offset = (currentPage - 1) * limit;

        totalRecords = service.getAllData().size();
        totalPages = (totalRecords - 1) / limit + 1;

        // 调用AlgorithmPerformanceService获取分页数据

        List<AlgorithmPerformanceData> pageData = service.getDataByOffset(offset, limit);

        // 更新表格数据
        Object[][] tableData = new Object[pageData.size()][7];
        for(int i = 0; i < pageData.size(); i++) {
            AlgorithmPerformanceData data = pageData.get(i);
            tableData[i][0] = data.getId();
            tableData[i][1] = data.getAlgorithmName();
            tableData[i][2] = data.getPlanTime();
            tableData[i][3] = data.getMapSize().getX();
            tableData[i][4] = data.getMapSize().getY();
            tableData[i][5] = data.getObstacleDensity();
            tableData[i][6] = data.getStepsWithTimes();
        }

        String[] columnNames = {"id", "算法名称", "平均规划时间(us)", "地图长度", "地图宽度", "障碍物密度", "起点、终点距离差及规划时间(us)"};
        table.setModel(new DefaultTableModel(tableData, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });

        currentPageLabel.setText(currentPage + "/" + totalPages);
        totalRecordsLabel.setText("共 " + totalRecords + " 条记录");
    }

    // 数字输入过滤器
    class NumberFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if(string.matches("\\d*")) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if(text.matches("\\d*")) {
                super.replace(fb, offset, length, text, attrs);
            }
        }
    }
}