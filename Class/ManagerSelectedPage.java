package Server.Class;

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
import java.util.List;
import java.util.*;


public class ManagerSelectedPage extends JPanel implements ContentPanel {
    private JPanel panel;
    private JButton toggleButton;
    private JButton refreshButton;
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> categoryComboBox;
    private JComboBox<String> courseComboBox;
    private JButton resetButton;
    private Button announcementButton;
    private Object[][] data;
    private JTextArea[] detailFields;
    private JButton addCourseButton;
    private JPanel detailPanel;
    private  JPanel infoPanel;
    private JFrame parentFrame;



    Student student = new Student("09024101","任小林","男",null);



    public ManagerSelectedPage(JFrame parentFrame) throws IOException, ClassNotFoundException {
        this.parentFrame = parentFrame;
        data = fetchCourseDataFromDatabase();
        String[] columnNames = {"课程代码", "课程名称", "任课教师","教师工号", "类别", "学分", "课容量","已选人数","总学时", "上课时间", "上课地点", "操作","操作"};

        panel = new JPanel(new BorderLayout());
        panel.setLayout(new BorderLayout());

        // 初始化表格
        tableModel = new DefaultTableModel(data, columnNames) {
            // 禁止编辑除最后一列外的所有列
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == columnNames.length - 2 || column == columnNames.length - 1; // 只允许最后两列被编辑
            }
        };


        table = new JTable(tableModel);
        table.setRowHeight(0, 40); // 这里将所有行的高度设置为 40，可以根据需要调整

        table.getColumnModel().getColumn(columnNames.length - 1).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(columnNames.length - 1).setCellEditor(new ButtonEditor(new JButton()));
        table.getColumnModel().getColumn(columnNames.length - 2).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(columnNames.length - 2).setCellEditor(new ButtonEditorAnother(new JButton()));

        table.setDefaultRenderer(Object.class, new MultiLineCellRenderer());
        table.setDefaultEditor(Object.class, new MultiLineCellEditor());
        customizeTableAppearance();

        // 创建一个垂直布局的面板来容纳按钮和过滤器面板
        JPanel verticalPanel = new JPanel();
        verticalPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        refreshButton = new JButton("刷新");
        refreshButton.addActionListener(e -> {
            try {
                refreshTableData();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });

        gbc.gridy = 4; // 新的一行
        gbc.weightx = 0.2; // 按钮占据80%的宽度
        gbc.fill = GridBagConstraints.HORIZONTAL; // 水平填充
        gbc.anchor = GridBagConstraints.LINE_START; // 对齐左边


        // 添加公告按钮
        String announcementText = "公告";
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

        // 添加“添加课程”按钮
        addCourseButton = new JButton("添加课程");
        addCourseButton.addActionListener(e -> handleAddCourseButtonClick());

        gbc.gridy = 2; // 下一行
        gbc.weightx = 0.8; // 按钮占据80%的宽度
        gbc.fill = GridBagConstraints.HORIZONTAL; // 水平填充
        gbc.anchor = GridBagConstraints.LINE_START; // 对齐左边
        verticalPanel.add(addCourseButton, gbc);

        gbc.gridy = 3; // 再下一行为过滤器面板
        gbc.weightx = 0.8; // 按钮占据80%的宽度
        gbc.fill = GridBagConstraints.HORIZONTAL; // 水平填充
        gbc.anchor = GridBagConstraints.LINE_START; // 对齐左边
        gbc.insets = new Insets(5, 0, 0, 0); // 内边距

        gbc.gridy = 1; // 下一行
        gbc.weightx = 0.0; // 不占据额外空间
        gbc.fill = GridBagConstraints.NONE; // 不填充
        gbc.insets = new Insets(0, 0, 0, 0); // 内边距
        verticalPanel.add(Box.createRigidArea(new Dimension(0, 20)), gbc); // 添加垂直空白区域

        detailPanel = new JPanel(new BorderLayout());
        detailPanel.setBorder(BorderFactory.createTitledBorder("课程详细信息"));
        panel.add(detailPanel, BorderLayout.SOUTH);

        // 初始化下拉菜单
        categoryComboBox = createComboBox(4); // 根据第3列数据初始化下拉菜单
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
        filterPanel.add(refreshButton);

        gbc.gridy = 2; // 下一行
        gbc.weightx = 0.8; // 按钮占据80%的宽度
        gbc.fill = GridBagConstraints.HORIZONTAL; // 水平填充
        gbc.anchor = GridBagConstraints.LINE_START; // 对齐左边
        verticalPanel.add(filterPanel, gbc);

        // 将垂直布局的面板添加到北侧
        panel.add(verticalPanel, BorderLayout.NORTH);

        // 将表格添加到中心
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        infoPanel = new JPanel(new GridLayout(0, 2));
        detailPanel.add(infoPanel, BorderLayout.CENTER);

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

        AnnouncementPageOfManager announcementPage = new AnnouncementPageOfManager(frame);


        // 将Schedule面板添加到父窗口的中心


        // 使窗口可见
        frame.setVisible(true);



    }
    private Object[][] fetchCourseDataFromDatabase() throws IOException, ClassNotFoundException {
        ClientController clientController = new ClientController();
        clientController.sendMessage(new Message(Message.MessageType.get_all_course_manager));
        Message response = clientController.receiveMessage();
        ArrayList<Object[]> courseList = new ArrayList<>();
        courseList = (ArrayList<Object[]>) response.getContent().get(0);



       /* try {
            // 建立数据库连接
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();

            // 执行SQL查询
            ResultSet rs = stmt.executeQuery("SELECT * FROM course");

            // 处理结果集并填充数据
            while (rs.next()) {
                Object[] course = new Object[11];
                course[0] = rs.getString("courseId");
                course[1] = rs.getString("courseName");
                course[2] = rs.getString("courseTeacher");
                course[3] = rs.getString("category");
                course[4] = rs.getDouble("courseCredits");
                course[5] = rs.getInt("courseCapacity");
                course[6] = rs.getInt("totalTime");
                String time = formatCourseTime(rs.getString("courseTime"));
                course[7] = time;
                course[8] = rs.getString("location");
                course[9] = "选课"; // 操作列的按钮文本
                course[10] = "退选"; // 操作列的按钮文本
                courseList.add(course);
            }

            // 关闭连接
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "无法从数据库读取课程信息", "错误", JOptionPane.ERROR_MESSAGE);
        }*/

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



    void showAddPage()
    {
        CourseAddPage courseAddPage = new CourseAddPage(parentFrame);
        courseAddPage.setModal(true);
        courseAddPage.setVisible(true);
    }

    private void handleAddCourseButtonClick() {
        showAddPage();
    }

    private void showCourseDetailsOnly(int row, JPanel infoPanel) {
        // 清空之前的信息
        infoPanel.removeAll();
        System.out.println("Respond");

        detailFields = new JTextArea[13];
        // 假设每行数据后续包含所有详细信息
        Object[] CourseDetails = data[row];

        // 动态生成文本框并添加到详细信息面板
        String[] labels = {"课程代码", "课程名称", "任课教师","教师工号", "类别", "学分","课容量", "已选人数","总学时", "上课时间", "上课地点"};
        for (int i = 1; i < labels.length; i++) {
            infoPanel.add(new JLabel(labels[i] + ":"));
            detailFields[i] = new JTextArea(CourseDetails[i].toString()); // 从数组的第2个元素开始
            detailFields[i].setEditable(false);
            infoPanel.add(detailFields[i]);
        }

        detailPanel.setVisible(true); // 确保详细信息面板可见
        toggleButton.setText("收起"); // 确保按钮显示为“收起”
        infoPanel.revalidate();
        infoPanel.repaint();
    }


    private void showCourseDetails(int row, JPanel infoPanel) {
        // 清空之前的信息
        infoPanel.removeAll();
        System.out.println("Respond");

        detailFields = new JTextArea[13];
        // 假设每行数据后续包含所有详细信息
        Object[] CourseDetails = data[row];

        // 动态生成文本框并添加到详细信息面板
        String[] labels = {"课程代码", "课程名称", "任课教师","教师工号", "类别", "学分","课容量","已选人数", "总学时", "上课时间", "上课地点"};
        for (int i = 0; i < labels.length; i++) {
            infoPanel.add(new JLabel(labels[i] + ":"));
            detailFields[i] = new JTextArea(CourseDetails[i].toString());
            if (i == 0) {
                detailFields[i].setEditable(false); // 设置第0个文本框为不可编辑
            }// 从数组的第2个元素开始
            infoPanel.add(detailFields[i]);
        }
        // 添加保存按钮
        JButton saveButton = new JButton("保存");
        saveButton.addActionListener(e -> {
            try {
                saveCourseDetails();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });
        infoPanel.add(saveButton);
        JButton deleteButton = new JButton("删除");
        deleteButton.addActionListener(e-> {
            try {
                deleteCourseDetails();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });
        infoPanel.add(deleteButton);
        detailPanel.setVisible(true); // 确保详细信息面板可见
        toggleButton.setText("收起"); // 确保按钮显示为“收起”
        infoPanel.revalidate();
        infoPanel.repaint();
    }
  /* private void showCourseDetails(int row, JPanel infoPanel) {
       // 清空之前的信息
       infoPanel.removeAll();
       detailFields = new JTextArea[10];
       Object[] CourseDetails = data[row];

       String[] labels = {"课程代码", "课程名称", "任课教师", "教师工号", "类别", "学分", "课容量", "已选人数", "总学时", "上课地点"};
       for (int i = 0; i < labels.length; i++) {
           infoPanel.add(new JLabel(labels[i] + ":"));

           // 设置JTextArea为1行高，20列宽
           detailFields[i] = new JTextArea(1, 20);
           detailFields[i].setText(CourseDetails[i].toString());

           if (i == 0) {
               detailFields[i].setEditable(false); // 课程代码不可编辑
           }

           // 使用JScrollPane以便显示
           JScrollPane scrollPane = new JScrollPane(detailFields[i]);
           scrollPane.setPreferredSize(new Dimension(200, 30)); // 控制文本框和滚动条的尺寸
           infoPanel.add(scrollPane);
       }

       // 添加时间选择部分
       JLabel timeLabel = new JLabel("课程时间:");
       infoPanel.add(timeLabel);

       JPanel timePanel = new JPanel();
       String[] daysOfWeek = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
       JComboBox<String> dayOfWeekComboBox = new JComboBox<>(daysOfWeek);
       String[] timeSlots = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
       JComboBox<String> startTimeComboBox = new JComboBox<>(timeSlots);
       JComboBox<String> endTimeComboBox = new JComboBox<>(timeSlots);

       timePanel.add(new JLabel("星期:"));
       timePanel.add(dayOfWeekComboBox);
       timePanel.add(new JLabel("开始时间:"));
       timePanel.add(startTimeComboBox);
       timePanel.add(new JLabel("结束时间:"));
       timePanel.add(endTimeComboBox);

       infoPanel.add(timePanel);

       // 显示已添加时间段
       JTextArea courseTimesListArea = new JTextArea(5, 20);
       courseTimesListArea.setEditable(false);
       infoPanel.add(new JLabel("已添加的时间段:"));
       infoPanel.add(new JScrollPane(courseTimesListArea));

       // 添加课程时间按钮
       JButton addTimeButton = new JButton("添加时间段");
       addTimeButton.addActionListener(e -> {
           String dayOfWeek = (String) dayOfWeekComboBox.getSelectedItem();
           String startTime = (String) startTimeComboBox.getSelectedItem();
           String endTime = (String) endTimeComboBox.getSelectedItem();
           courseTimesListArea.append(dayOfWeek + " 第" + startTime + "-" + endTime + "节\n");
       });
       infoPanel.add(addTimeButton);

       // 添加保存和删除按钮
       JButton saveButton = new JButton("保存");
       saveButton.addActionListener(e -> {
           try {
               saveCourseDetails();
           } catch (IOException | ClassNotFoundException ex) {
               throw new RuntimeException(ex);
           }
       });
       infoPanel.add(saveButton);

       JButton deleteButton = new JButton("删除");
       deleteButton.addActionListener(e -> {
           try {
               deleteCourseDetails();
           } catch (IOException | ClassNotFoundException ex) {
               throw new RuntimeException(ex);
           }
       });
       infoPanel.add(deleteButton);

       detailPanel.setVisible(true);
       infoPanel.revalidate();
       infoPanel.repaint();
   }
*/

   /*private void showCourseDetails(int row, JPanel infoPanel) {
       // 清空之前的信息
       infoPanel.removeAll();

       // 使用滚动面板包裹整个infoPanel
       JScrollPane scrollPane = new JScrollPane(infoPanel);
       scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
       scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
       detailPanel.add(scrollPane);

       detailFields = new JTextArea[10];
       Object[] CourseDetails = data[row];

       String[] labels = {"课程代码", "课程名称", "任课教师", "教师工号", "类别", "学分", "课容量", "已选人数", "总学时", "上课地点"};
       for (int i = 0; i < labels.length; i++) {
           infoPanel.add(new JLabel(labels[i] + ":"));
           detailFields[i] = new JTextArea(1, 20); // 将每个输入框设置为一行
           detailFields[i].setText(CourseDetails[i].toString());
           if (i == 0) {
               detailFields[i].setEditable(false); // 课程代码不可编辑
           }
           infoPanel.add(new JScrollPane(detailFields[i])); // 给每个文本域添加滚动条
       }

       // 添加时间选择部分
       JLabel timeLabel = new JLabel("课程时间:");
       infoPanel.add(timeLabel);

       JPanel timePanel = new JPanel();
       String[] daysOfWeek = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
       JComboBox<String> dayOfWeekComboBox = new JComboBox<>(daysOfWeek);
       String[] timeSlots = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
       JComboBox<String> startTimeComboBox = new JComboBox<>(timeSlots);
       JComboBox<String> endTimeComboBox = new JComboBox<>(timeSlots);

       timePanel.add(new JLabel("星期:"));
       timePanel.add(dayOfWeekComboBox);
       timePanel.add(new JLabel("开始时间:"));
       timePanel.add(startTimeComboBox);
       timePanel.add(new JLabel("结束时间:"));
       timePanel.add(endTimeComboBox);

       infoPanel.add(timePanel);

       // 显示已添加时间段
       JTextArea courseTimesListArea = new JTextArea(3, 20); // 显示区高度调整为3行
       courseTimesListArea.setEditable(false);
       infoPanel.add(new JLabel("已添加的时间段:"));
       infoPanel.add(new JScrollPane(courseTimesListArea)); // 给显示区添加滚动条

       // 添加课程时间按钮
       JButton addTimeButton = new JButton("添加时间段");
       addTimeButton.addActionListener(e -> {
           String dayOfWeek = (String) dayOfWeekComboBox.getSelectedItem();
           String startTime = (String) startTimeComboBox.getSelectedItem();
           String endTime = (String) endTimeComboBox.getSelectedItem();
           courseTimesListArea.append(dayOfWeek + " 第" + startTime + "-" + endTime + "节\n");
       });
       infoPanel.add(addTimeButton);

       // 添加保存和删除按钮
       JButton saveButton = new JButton("保存");
       saveButton.addActionListener(e -> {
           try {
               saveCourseDetails();
           } catch (IOException | ClassNotFoundException ex) {
               throw new RuntimeException(ex);
           }
       });
       infoPanel.add(saveButton);

       JButton deleteButton = new JButton("删除");
       deleteButton.addActionListener(e -> {
           try {
               deleteCourseDetails();
           } catch (IOException | ClassNotFoundException ex) {
               throw new RuntimeException(ex);
           }
       });
       infoPanel.add(deleteButton);

       detailPanel.setVisible(true);
       infoPanel.revalidate();
       infoPanel.repaint();
   }*/



    private void deleteCourseDetails() throws IOException, ClassNotFoundException {
        ClientController clientController   = new ClientController();
        String courseId = detailFields[0].getText();
        String courseName = detailFields[1].getText();
        String teacher = detailFields[2].getText();
        String teacherId =detailFields[3].getText();
        String category = detailFields[4].getText();
        Double credits = Double.valueOf(detailFields[5].getText());
        int capacity = Integer.parseInt(detailFields[6].getText());
        int num = Integer.parseInt(detailFields[7].getText());
        int totalHours = Integer.parseInt(detailFields[8].getText());
        String schedule = detailFields[9].getText();
        // 上课时间
        ArrayList<courseTime>time = parseJsonToCourseTimeList(parseFormattedTime(schedule));
        String location = detailFields[10].getText();
        // 实现保存功能，可以将修改后的数据更新到 data 数组或数据库
        Course course = new Course(courseId,teacher,courseName,category,location,capacity,totalHours,credits,time,num,teacherId);
        List<Serializable>temp = new ArrayList<>();
        temp.add(course);
        clientController.sendMessage(new Message(Message.MessageType.delete_course,temp));
        Message response = clientController.receiveMessage();

        if(response.getType().equals(Message.MessageType.success))
            JOptionPane.showMessageDialog(this, "课程信息已删除！", "删除成功", JOptionPane.INFORMATION_MESSAGE);
        else
            JOptionPane.showMessageDialog(this, "课程信息删除失败！", "删除失败", JOptionPane.INFORMATION_MESSAGE);



    }
    private void saveCourseDetails() throws IOException, ClassNotFoundException {
        String courseId = detailFields[0].getText();
        String courseName = detailFields[1].getText();
        String teacher = detailFields[2].getText();
        String teacherId = detailFields[3].getText();
        String category = detailFields[4].getText();
        Double credits = Double.valueOf(detailFields[5].getText());
        int capacity = Integer.parseInt(detailFields[6].getText());
        int totalHours = Integer.parseInt(detailFields[7].getText());
        String location = detailFields[8].getText();

        // 获取添加的时间段
        ArrayList<courseTime> courseTimes = new ArrayList<>();
        // 解析并转换时间段为 courseTime 对象
        // courseTimes 从已添加的文本区域或其他数据结构中获取

        Course course = new Course(courseId, teacher, courseName, category, location, capacity, totalHours, credits, courseTimes, 0, teacherId);
        ClientController clientController = new ClientController();
        List<Serializable> temp = new ArrayList<>();
        temp.add(course);
        clientController.sendMessage(new Message(Message.MessageType.updata_course, temp));
        Message response = clientController.receiveMessage();

        if (response.getType().equals(Message.MessageType.success)) {
            JOptionPane.showMessageDialog(this, "课程信息已保存！", "保存成功", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "课程信息保存失败！", "保存失败", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    /*private void saveCourseDetails() throws IOException, ClassNotFoundException {
        String courseId = detailFields[0].getText();
        String courseName = detailFields[1].getText();
        String teacher = detailFields[2].getText();
        String teacherId =detailFields[3].getText();
        String category = detailFields[4].getText();
        Double credits = Double.valueOf(detailFields[5].getText());
        int capacity = Integer.parseInt(detailFields[6].getText());
        int num = Integer.parseInt(detailFields[7].getText());
        int totalHours = Integer.parseInt(detailFields[8].getText());
        String schedule = detailFields[9].getText();
        // 上课时间
        ArrayList<courseTime>time = parseJsonToCourseTimeList(parseFormattedTime(schedule));
        String location = detailFields[10].getText();
        // 实现保存功能，可以将修改后的数据更新到 data 数组或数据库
        Course course = new Course(courseId,teacher,courseName,category,location,capacity,totalHours,credits,time,num,teacherId);
        ClientController clientController = new ClientController();
        List<Serializable>temp = new ArrayList<>();
        temp.add(course);
        clientController.sendMessage(new Message(Message.MessageType.updata_course,temp));
        Message response = clientController.receiveMessage();


        if(response.getType().equals(Message.MessageType.success))
            JOptionPane.showMessageDialog(this, "课程信息已保存！", "保存成功", JOptionPane.INFORMATION_MESSAGE);
        else
            JOptionPane.showMessageDialog(this, "课程信息保存失败！", "保存失败", JOptionPane.INFORMATION_MESSAGE);

    }*/





    public static ArrayList<courseTime> parseJsonToCourseTimeList(String jsonString) {
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<courseTime>>(){}.getType();
        return gson.fromJson(jsonString, listType);
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


            showCourseDetails(row,infoPanel);

            // 处理按钮点击事件
            System.out.println("Button clicked in row " + row + ", column " + column + ". Text: " + buttonText);

            // 这里可以添加更多的逻辑，比如弹出对话框或执行某些操作
        }


    }

    private class ButtonEditorAnother extends AbstractCellEditor implements TableCellEditor, ActionListener {
        private JButton button;
        private boolean isEditing = false;
        private int lastRow = -1;
        private int lastColumn = -1;

        public ButtonEditorAnother(JButton button) {
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
            System.out.println("Button clicked in row " + row + ", column " + column + ". Text: " + buttonText);

            // 这里可以添加更多的逻辑，比如弹出对话框或执行某些操作
        }


    }



    @Override
    public JPanel getPanel() {
        return panel;
    }

    private void refreshTableData() throws IOException, ClassNotFoundException {
        data = fetchCourseDataFromDatabase(); // 从数据库重新获取数据
        tableModel.setRowCount(0); // 清空当前的表格数据
        for (Object[] row : data) {
            tableModel.addRow(row); // 添加新数据
        }
        table.revalidate();
        table.repaint();
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

class MultiLineCellRenderer extends JTextArea implements TableCellRenderer {
    public MultiLineCellRenderer() {
        setLineWrap(true);
        setWrapStyleWord(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {
        setText(value != null ? value.toString() : "");
        setSize(table.getColumnModel().getColumn(column).getWidth(), getPreferredSize().height);

        if (table.getRowHeight(row) < getPreferredSize().height) {
            table.setRowHeight(row, getPreferredSize().height); // 动态设置行高
        }

        return this;
    }
}


// 自定义编辑器
class MultiLineCellEditor extends DefaultCellEditor {
    private JTextArea textArea;

    public MultiLineCellEditor() {
        super(new JTextField());
        textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        textArea.setText(value != null ? value.toString() : "");
        return new JScrollPane(textArea);
    }

    @Override
    public Object getCellEditorValue() {
        return textArea.getText();
    }
}
