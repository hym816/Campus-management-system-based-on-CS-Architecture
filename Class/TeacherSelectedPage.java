package Server.Class;

import Server.Public.ClientController;
import Server.Public.ContentPanel;
import Server.Public.Message;
import Server.Public.Student;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;
import java.util.*;

//import static java.lang.StringTemplate.STR;


public class TeacherSelectedPage extends JPanel implements ContentPanel {
    private JPanel panel;
    private JButton toggleButton;
    private JPanel detailPanel;
    private JTextField[] detailFields;
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> categoryComboBox;
    private JComboBox<String> courseComboBox;
    private JButton resetButton;
    private Button announcementButton;
    private Object[][] data;
    private  JPanel infoPanel;




    Student student = new Student("09024101","任小林","男",null);

    public TeacherSelectedPage(String teacherName) throws IOException, ClassNotFoundException {


        data = fetchCourseDataFromDatabase(teacherName);
        String[] columnNames = {"课程代码", "课程名称", "任课教师", "类别", "学分", "课容量","总学时", "上课时间", "上课地点", "操作"};

        panel = new JPanel(new BorderLayout());
        panel.setLayout(new BorderLayout());

        // 初始化表格
        tableModel = new DefaultTableModel(data, columnNames) {
            // 禁止编辑除最后一列外的所有列
            @Override
            public boolean isCellEditable(int row, int column) {
                return  column == columnNames.length - 1; // 只允许最后两列被编辑
            }
        };

        table = new JTable(tableModel);
        table.getColumnModel().getColumn(columnNames.length - 1).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(columnNames.length - 1).setCellEditor(new ButtonEditor(new JButton()));


        customizeTableAppearance();

        // 创建一个垂直布局的面板来容纳按钮和过滤器面板
        JPanel verticalPanel = new JPanel();
        verticalPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();

        detailPanel = new JPanel(new BorderLayout());
        detailPanel.setBorder(BorderFactory.createTitledBorder("课程详细信息"));
        panel.add(detailPanel, BorderLayout.SOUTH);
        // 添加公告按钮
        String announcementText = "这是公告";
        announcementButton = new Button(announcementText);
        announcementButton.addActionListener(e -> {
            try {
                showAnnouncement();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.8; // 按钮占据80%的宽度
        gbc.fill = GridBagConstraints.HORIZONTAL; // 水平填充
        gbc.anchor = GridBagConstraints.LINE_START; // 对齐左边
        verticalPanel.add(announcementButton, gbc);

        gbc.gridy = 1; // 下一行
        gbc.weightx = 0.0; // 不占据额外空间
        gbc.fill = GridBagConstraints.NONE; // 不填充
        gbc.insets = new Insets(0, 0, 0, 0); // 内边距
        verticalPanel.add(Box.createRigidArea(new Dimension(0, 20)), gbc); // 添加垂直空白区域

        // 初始化下拉菜单
        categoryComboBox = createComboBox(3); // 根据第3列数据初始化下拉菜单
        courseComboBox = createComboBox(1);
        resetButton = new JButton("重置");
        resetButton.addActionListener(e -> resetFilters());

        categoryComboBox.addActionListener(e -> filterTable());
        courseComboBox.addActionListener(e -> filterTable());

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0)); // 使用FlowLayout居左，水平间距5像素
        filterPanel.add(new JLabel("课程名称:"));
        filterPanel.add(courseComboBox);
        filterPanel.add(new JLabel("课程类别:"));
        filterPanel.add(categoryComboBox);
        filterPanel.add(resetButton);

        gbc.gridy = 2; // 下一行
        gbc.weightx = 0.8; // 按钮占据80%的宽度
        gbc.fill = GridBagConstraints.HORIZONTAL; // 水平填充
        gbc.anchor = GridBagConstraints.LINE_START; // 对齐左边
        verticalPanel.add(filterPanel, gbc);

        infoPanel = new JPanel(new GridLayout(0, 2));
        detailPanel.add(infoPanel, BorderLayout.CENTER);

        JButton scoringButton = new JButton("打分");
        scoringButton.addActionListener(e -> {
            try {
                openScoringPage();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });

        gbc.gridy = 3; // 下一行
        gbc.weightx = 0.0; // 不占据额外空间
        gbc.fill = GridBagConstraints.NONE; // 不填充
        verticalPanel.add(scoringButton, gbc);
        // 将垂直布局的面板添加到北侧
        panel.add(verticalPanel, BorderLayout.NORTH);

        // 将表格添加到中心
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        toggleButton = new JButton("收起");
        toggleButton.addActionListener(e -> toggleDetailPanel());
        detailPanel.add(toggleButton, BorderLayout.NORTH);
    }
    private void showAnnouncement() throws IOException {
        // 可选：添加边框

        // 创建一个新的JFrame来显示Schedule面板
        JFrame frame = new JFrame("公告列表");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 关闭窗口时销毁窗口
        frame.setSize(800, 600); // 设置窗口大小

        AnnouncementPageOfUsers announcementPage = new AnnouncementPageOfUsers(frame);


        // 将Schedule面板添加到父窗口的中心


        // 使窗口可见
        frame.setVisible(true);



    }

    private Object[][] fetchCourseDataFromDatabase(String teacherName) throws IOException, ClassNotFoundException {
        ArrayList<Object[]> courseList = new ArrayList<>();
        ClientController clientController = new ClientController();
        List<Serializable>temp = new ArrayList<>();
        temp.add(teacherName);
        clientController.sendMessage(new Message(Message.MessageType.get_course_by_teachername,temp));
        Message response =clientController.receiveMessage();
        courseList = (ArrayList<Object[]>) response.getContent().get(0);
        // 将列表转换为数组
        return courseList.toArray(new Object[0][0]);
    }

    public static String formatCourseTime(String jsonTime) {
        Gson gson = new Gson();

        // 定义 JSON 解析的类型
        Type listType = new TypeToken<List<Map<String, String>>>(){}.getType();

        // 解析 JSON 字符串为 List<Map<String, String>>
        List<Map<String, String>> timeSlots = gson.fromJson(jsonTime, listType);

        StringBuilder formattedTime = new StringBuilder();
        for (Map<String, String> timeSlot : timeSlots) {
            String dayOfWeek = timeSlot.get("dayOfWeek");
            String startTime = timeSlot.get("startTime");
            String endTime = timeSlot.get("endTime");
            formattedTime.append(dayOfWeek)
                    .append(" 第")
                    .append(startTime)
                    .append("-")
                    .append(endTime)
                    .append("节课")

                    .append("\n");
        }

        return formattedTime.toString().trim(); // 去掉最后一个换行符
    }
    public static String parseFormattedTime(String formattedTime) {
        Gson gson = new Gson();
        List<Map<String, String>> timeSlots = new ArrayList<>();

        String[] lines = formattedTime.split("\n");
        for (String line : lines) {
            Map<String, String> timeSlot = new HashMap<>();

            // 假设输入格式为 "周三 第3~5节课"
            String[] parts = line.split(" ");
            String dayOfWeek = parts[0];
            String timeRange = parts[1].substring(1, parts[1].length() - 2); // 获取"第3~5节课"中的"3~5"
            String[] times = timeRange.split("-");

            timeSlot.put("dayOfWeek", dayOfWeek);
            timeSlot.put("startTime", times[0]);
            timeSlot.put("endTime", times[1]);

            timeSlots.add(timeSlot);
        }

        // 将 List<Map<String, String>> 转换为 JSON 字符串
        return gson.toJson(timeSlots);}


    private void showCourseDetailsOnly(int row, JPanel infoPanel) {
        // 清空之前的信息
        infoPanel.removeAll();
        System.out.println("Respond");

        detailFields = new JTextField[9];
        // 假设每行数据后续包含所有详细信息
        Object[] CourseDetails = data[row];

        // 动态生成文本框并添加到详细信息面板
        String[] labels = {"课程代码", "课程名称", "任课教师", "类别", "学分", "总学时", "上课时间", "上课地点"};
        for (int i = 1; i < labels.length; i++) {
            infoPanel.add(new JLabel(labels[i] + ":"));
            detailFields[i] = new JTextField(CourseDetails[i].toString()); // 从数组的第2个元素开始
            detailFields[i].setEditable(false);
            infoPanel.add(detailFields[i]);
        }

        detailPanel.setVisible(true); // 确保详细信息面板可见
        toggleButton.setText("收起"); // 确保按钮显示为“收起”
        infoPanel.revalidate();
        infoPanel.repaint();
    }
    private void openScoringPage() throws IOException, ClassNotFoundException {
        // 获取选中的课程代码
        int selectedRow = table.getSelectedRow();
        System.out.println(selectedRow);
        if (selectedRow >= 0) {
            String courseId = (String) table.getValueAt(selectedRow, 0); // 假设课程代码在第一列
            // 创建并显示 ScoringPage 对话框
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            ScoringPage scoringPage = new ScoringPage(parentFrame, courseId);
            scoringPage.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "请先选择一门课程。", "警告", JOptionPane.WARNING_MESSAGE);
        }
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

    private void resetFilters() {
        categoryComboBox.setSelectedIndex(0);
        courseComboBox.setSelectedIndex(0);
        filterTable(); // 显示所有数据
    }

    private void filterTable() {
        String semester = (String) categoryComboBox.getSelectedItem();
        String course = (String) courseComboBox.getSelectedItem();

        tableModel.setRowCount(0); // 清空表格内容
        for (Object[] row : data) {
            if ((semester.equals("全部") || row[3].equals(semester)) &&
                    (course.equals("全部") || row[1].equals(course))) {
                tableModel.addRow(row);
            }
        }
    }

    private void customizeTableAppearance() {
        table.setRowHeight(30); // 设置行高

        // 设置字体和颜色
        table.setFont(new Font("宋体", Font.PLAIN, 16));
        table.getTableHeader().setFont(new Font("宋体", Font.BOLD, 16));
    }

    private class ButtonRenderer implements TableCellRenderer {
        private final JButton button = new JButton("编辑");

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            button.setText((String) value); // 如果需要动态改变按钮文本
            return button;
        }
    }

    private class ButtonEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
        private JButton button;
        private boolean isEditing = false;
        private int lastRow = -1;
        private int lastColumn = -1;

        public ButtonEditor(JButton button) {
            this.button = button;
            this.button.addActionListener(this);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            lastRow = row;
            lastColumn = column;
            button.setText((String) value); // 如果需要动态改变按钮文本
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            isEditing = false;
            return new String(button.getText());
        }

        @Override
        public boolean stopCellEditing() {
            isEditing = false;
            return super.stopCellEditing();
        }

        @Override
        public boolean shouldSelectCell(EventObject anEvent) {
            return true;
        }

        @Override
        public boolean isCellEditable(EventObject anEvent) {
            return true;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            fireEditingStopped(); // 停止编辑
            String buttonText = button.getText();
            int row = lastRow;
            int column = lastColumn;

            showCourseDetailsOnly(row,infoPanel);

            // 处理按钮点击事件
//            System.out.println(STR."Button clicked in row \{row}, column \{column}. Text: \{buttonText}");

            // 这里可以添加更多的逻辑，比如弹出对话框或执行某些操作
        }
    }

    @Override
    public JPanel getPanel() {
        return panel;
    }




    private void toggleDetailPanel() {
        // 切换详细信息面板的可见状态
        if (detailPanel.isVisible()) {
            detailPanel.setVisible(false);
            toggleButton.setText("展开");
        } else {
            detailPanel.setVisible(true);
            toggleButton.setText("收起");
        }
    }

}