package Server.Public;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class GradeTable extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> semesterComboBox;
    private JComboBox<String> courseComboBox;
    private JComboBox<String> typeComboBox;
    private JComboBox<String> creditComboBox;
    private JButton resetButton;

    private Object[][] data;  // 原始数据保存

    public GradeTable(Object[][] data) {
        this.data = data; // 保存原始数据
        setLayout(new BorderLayout());

        // 初始化表格
        String[] columnNames = {"学期", "课程", "必修/选修", "学分", "成绩"};
        tableModel = new DefaultTableModel(data, columnNames);
        table = new JTable(tableModel);
        customizeTableAppearance();

        // 初始化下拉菜单
        semesterComboBox = createComboBox(0); // 根据第1列数据初始化下拉菜单
        courseComboBox = createComboBox(1);   // 根据第2列数据初始化下拉菜单
        typeComboBox = createComboBox(2);     // 根据第3列数据初始化下拉菜单
        creditComboBox = createComboBox(3);   // 根据第4列数据初始化下拉菜单

        // 初始化重置按钮
        resetButton = new JButton("重置");
        resetButton.addActionListener(e -> resetFilters());

        // 为下拉菜单添加监听器
        semesterComboBox.addActionListener(e -> filterTable());
        courseComboBox.addActionListener(e -> filterTable());
        typeComboBox.addActionListener(e -> filterTable());
        creditComboBox.addActionListener(e -> filterTable());

        // 创建筛选面板
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); // 使用FlowLayout居左
        filterPanel.add(new JLabel("学期:"));
        filterPanel.add(semesterComboBox);
        filterPanel.add(new JLabel("课程:"));
        filterPanel.add(courseComboBox);
        filterPanel.add(new JLabel("必修/选修:"));
        filterPanel.add(typeComboBox);
        filterPanel.add(new JLabel("学分:"));
        filterPanel.add(creditComboBox);
        filterPanel.add(resetButton);

        // 将筛选面板和表格添加到面板
        add(filterPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void customizeTableAppearance() {
        table.setRowHeight(30); // 设置行高

        // 设置字体和颜色
        table.setFont(new Font("宋体", Font.PLAIN, 16));
        table.getTableHeader().setFont(new Font("宋体", Font.BOLD, 16));

        // 自定义渲染器
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    if (row % 2 == 0) {
                        c.setBackground(Color.WHITE); // 白色
                    } else {
                        c.setBackground(new Color(240, 240, 240)); // 浅灰色
                    }
                }
                return c;
            }
        };
        table.setDefaultRenderer(Object.class, renderer);
    }

    private JComboBox<String> createComboBox(int columnIndex) {
        Set<String> uniqueValues = new HashSet<>();
        for (Object[] row : data) {
            uniqueValues.add(row[columnIndex].toString());
        }
        ArrayList<String> sortedValues = new ArrayList<>(uniqueValues);
        sortedValues.sort(String::compareTo); // 排序
        sortedValues.add(0, "全部"); // 添加默认选项
        return new JComboBox<>(sortedValues.toArray(new String[0]));
    }

    private void filterTable() {
        String semester = (String) semesterComboBox.getSelectedItem();
        String course = (String) courseComboBox.getSelectedItem();
        String type = (String) typeComboBox.getSelectedItem();
        String credit = (String) creditComboBox.getSelectedItem();

        tableModel.setRowCount(0); // 清空表格内容
        for (Object[] row : data) {
            if ((semester.equals("全部") || row[0].equals(semester)) &&
                    (course.equals("全部") || row[1].equals(course)) &&
                    (type.equals("全部") || row[2].equals(type)) &&
                    (credit.equals("全部") || row[3].toString().equals(credit))) {
                tableModel.addRow(row);
            }
        }
    }

    private void resetFilters() {
        semesterComboBox.setSelectedIndex(0);
        courseComboBox.setSelectedIndex(0);
        typeComboBox.setSelectedIndex(0);
        creditComboBox.setSelectedIndex(0);
        filterTable(); // 显示所有数据
    }
}
