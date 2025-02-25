package Server.Class;

import Server.MainWindow;
import Server.Public.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.Button;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.List;
import java.util.*;


import java.util.Map;


public class CourseSelectedPage extends JPanel implements ContentPanel {
    private JPanel panel;
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> categoryComboBox;
    private JComboBox<String> courseComboBox;
    private Button resetButton;
    private Button announcementButton;
    private Object[][] data;
    private JTextField[] detailFields;
    private Button scheduleButton;
    private JFrame parentFrame;
    private Button refreshButton;
    Student student;

   // Student student = new Student(MainWindow.stuid,"任小林","男",null);

    public CourseSelectedPage(String id) throws IOException, ClassNotFoundException {
        ClientController clientController = new ClientController();
        List <Serializable> temp = new ArrayList<>();
        temp.add(id);
        clientController.sendMessage(new Message(Message.MessageType.get_students_by_studentid,temp));
        Message response = clientController.receiveMessage();

        student = (Student) response.getContent().get(0);

        data = fetchCourseDataFromDatabase();
        String[] columnNames = {"课程代码", "课程名称", "任课教师","教师工号", "类别", "学分", "课容量","已选人数","总学时", "上课时间", "上课地点", "操作","操作"};

        panel = new JPanel(new BorderLayout());
        panel.setLayout(new BorderLayout());

        // 初始化表格
        tableModel = new DefaultTableModel(data, columnNames) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == columnNames.length - 2 || column == columnNames.length - 1; // 只允许最后两列被编辑
            }
        };

        table = new JTable(tableModel);



        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(150); // 设定一个合理的默认宽度
        }
        adjustRowHeights(table);


        table.getColumnModel().getColumn(columnNames.length - 1).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(columnNames.length - 1).setCellEditor(new ButtonEditor(new JButton()));
        table.getColumnModel().getColumn(columnNames.length - 2).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(columnNames.length - 2).setCellEditor(new ButtonEditor(new JButton()));
        table.setDefaultRenderer(Object.class, new MultiLineCellRenderer());
        table.setDefaultEditor(Object.class, new MultiLineCellEditor());

        customizeTableAppearance();

        // 创建一个垂直布局的面板来容纳按钮和过滤器面板
        JPanel verticalPanel = new JPanel();
        verticalPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();

        // 添加公告按钮
        String announcementText = "公告";
        announcementButton = new Button(announcementText);
        announcementButton.addActionListener(e-> {
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
        verticalPanel.add(Box.createRigidArea(new Dimension(0, 20)), gbc); //

        // 添加垂直空白区域

        // 初始化下拉菜单
        categoryComboBox = createComboBox(4); // 根据第3列数据初始化下拉菜单
        courseComboBox = createComboBox(1);
        resetButton = new Button("重置");
        resetButton.addActionListener(e -> resetFilters());

        categoryComboBox.addActionListener(e -> filterTable());
        courseComboBox.addActionListener(e -> filterTable());

        refreshButton = new Button("刷新");
        refreshButton.addActionListener(e -> refreshTableData());
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0)); // 使用FlowLayout居左，水平间距5像素
        filterPanel.add(new JLabel("课程名称:"));
        filterPanel.add(courseComboBox);
        filterPanel.add(new JLabel("课程类别:"));
        filterPanel.add(categoryComboBox);
        filterPanel.add(resetButton);
        filterPanel.add(refreshButton);

        gbc.gridy = 2; // 下一行
        gbc.weightx = 0.8; // 按钮占据80%的宽度
        gbc.fill = GridBagConstraints.HORIZONTAL; // 水平填充
        gbc.anchor = GridBagConstraints.LINE_START; // 对齐左边
        verticalPanel.add(filterPanel, gbc);







        gbc.gridy = 3; // 新的一行
        gbc.weightx = 0.2; // 按钮占据20%的宽度
        gbc.fill = GridBagConstraints.HORIZONTAL; // 不填充
        gbc.anchor = GridBagConstraints.LINE_END; // 对齐右边
        scheduleButton = new Button("我的课表");
        scheduleButton.addActionListener(e -> {

            try {
                showMySchedule();
            } catch (IOException | ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }

        }); // 定义点击事件

        verticalPanel.add(scheduleButton, gbc);

        // 将垂直布局的面板添加到北侧
        panel.add(verticalPanel, BorderLayout.NORTH);

        // 将表格添加到中心
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
    }



    private Object[][] fetchCourseDataFromDatabase() throws IOException, ClassNotFoundException {
        ClientController clientController = new ClientController();
        clientController.sendMessage(new Message(Message.MessageType.get_all_course));


        Message response = clientController.receiveMessage();

        ArrayList<Object[]> courseList = new ArrayList<>();
        courseList = (ArrayList<Object[]>) response.getContent().get(0);
        System.out.println(1);





        // 将列表转换为数组
        return courseList.toArray(new Object[0][0]);
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

    private void refreshTableData() {
        try {
            // 从数据库重新获取数据
            Object[][] newData = fetchCourseDataFromDatabase();
            // 更新表格模型中的数据
            tableModel.setDataVector(newData, new String[]{"课程代码", "课程名称", "任课教师","教师工号", "类别", "学分", "课容量", "已选人数", "总学时", "上课时间", "上课地点", "操作", "操作"});

            for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
                table.getColumnModel().getColumn(i).setPreferredWidth(150); // 设定一个合理的默认宽度
            }
            adjustRowHeights(table);


            table.getColumnModel().getColumn(tableModel.getColumnCount() - 1).setCellRenderer(new ButtonRenderer());
            table.getColumnModel().getColumn(tableModel.getColumnCount() - 1).setCellEditor(new ButtonEditor(new JButton()));
            table.getColumnModel().getColumn(tableModel.getColumnCount() - 2).setCellRenderer(new ButtonRenderer());
            table.getColumnModel().getColumn(tableModel.getColumnCount() - 2).setCellEditor(new ButtonEditor(new JButton()));

            tableModel.fireTableDataChanged();  // 通知模型数据已更新
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
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

    private void showMySchedule() throws IOException, ClassNotFoundException {
        Schedule schedule = new Schedule(student.getStudentId());
        schedule.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // 可选：添加边框

        // 创建一个新的JFrame来显示Schedule面板
        JFrame frame = new JFrame("我的课表");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 关闭窗口时销毁窗口
        frame.setSize(800, 600); // 设置窗口大小

        // 将Schedule面板添加到父窗口的中心
        frame.add(schedule, BorderLayout.CENTER);

        // 使窗口可见
        frame.setVisible(true);
        // 这里可以添加逻辑来显示用户的课表
        // 例如，打开一个新的窗口显示课表详情


    }

    private class ButtonEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
        private JButton button;
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
            return button.getText();
        }

        @Override
        public boolean stopCellEditing() {
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

            if (lastColumn == 11) { // "选课" 列
                try {
                    handleEnrollAction(lastRow);
                } catch (SQLException | ClassNotFoundException | IOException ex) {
                    throw new RuntimeException(ex);
                }
            } else if (lastColumn == 12) { // "退选" 列
                try {
                    handleWithdrawAction(lastRow);
                } catch (IOException | ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }

        // 处理选课操作
        private void handleEnrollAction(int row) throws SQLException, IOException, ClassNotFoundException {
            ClientController clientController = new ClientController();
            String courseId = (String) table.getValueAt(row, 0);
            String courseName = (String)table.getValueAt(row,1);
            String courseTeacher = (String) table.getValueAt(row, 2);
            String teacherId = (String) table.getValueAt(row,3);
            String courseCategory = (String)table.getValueAt(row,4);
            Double courseCredits = (Double) table.getValueAt(row, 5);
            int capacity =(int)table.getValueAt(row,6);
            int num =(int)table.getValueAt(row,7);
            int totalTime = (int) table.getValueAt(row,8);

            String location =(String)table.getValueAt(row,10);
            String time = parseFormattedTime((String) table.getValueAt(row,9));
            ArrayList<courseTime> times =parseJsonToCourseTimeList(time);
            Course course = new Course(courseId,courseTeacher,courseName,courseCategory,location,capacity,totalTime,courseCredits,times,num,teacherId);
            List< Serializable> temp = new ArrayList<>();
            temp.add(course);
            temp.add(student);
            clientController.sendMessage(new Message(Message.MessageType.select_course,temp));

            Message response = clientController.receiveMessage();


            // 在这里添加选课的逻辑
            if(response.getType().equals(Message.MessageType.success)) {
                JOptionPane.showMessageDialog(panel, "选课成功: " + courseId);
                refreshTableData();


            }
            else
                JOptionPane.showMessageDialog(panel, "选课失败: " + courseId+" 时间冲突！");

            //System.out.println("Enroll clicked for course ID: " + courseId);



        }

        // 处理退选操作
        private void handleWithdrawAction(int row) throws IOException, ClassNotFoundException {
            ClientController clientController =new ClientController();
            String courseId = (String) table.getValueAt(row, 0);
            String courseName = (String)table.getValueAt(row,1);
            String courseTeacher = (String) table.getValueAt(row, 2);
            String teacherId = (String) table.getValueAt(row,3);
            String courseCategory = (String)table.getValueAt(row,4);
            Double courseCredits = (Double) table.getValueAt(row, 5);
            int capacity =(int)table.getValueAt(row,6);
            int num = (int)table.getValueAt(row,7);
            int totalTime = (int) table.getValueAt(row,8);

            String location =(String)table.getValueAt(row,10);
            String time = parseFormattedTime((String) table.getValueAt(row,9));
            ArrayList<courseTime> times =parseJsonToCourseTimeList(time);
            Course course = new Course(courseId,courseTeacher,courseName,courseCategory,location,capacity,totalTime,courseCredits,times,num,teacherId);
            List<Serializable>temp = new ArrayList<>();
            temp.add(course);
            temp.add(student);
            clientController.sendMessage(new Message(Message.MessageType.unselect_course,temp));

            Message response = clientController.receiveMessage();
            if(response.getType().equals(Message.MessageType.success)) {
                JOptionPane.showMessageDialog(panel, "退选成功: " + courseId);
                refreshTableData();
            }
        }
    }
    private void adjustRowHeights(JTable table) {
        for (int row = 0; row < table.getRowCount(); row++) {
            int maxHeight = 0;
            for (int column = 0; column < table.getColumnCount(); column++) {
                TableCellRenderer renderer = table.getCellRenderer(row, column);
                Component comp = table.prepareRenderer(renderer, row, column);
                int preferredHeight = comp.getPreferredSize().height;
                maxHeight = Math.max(maxHeight, preferredHeight);
            }
            // 设置行高为最大高度
            table.setRowHeight(row, maxHeight + table.getRowMargin());
        }
    }



    public static ArrayList<courseTime> parseJsonToCourseTimeList(String jsonString) {
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<courseTime>>(){}.getType();
        return gson.fromJson(jsonString, listType);
    }


    @Override
    public JPanel getPanel() {
        return panel;
    }











}

