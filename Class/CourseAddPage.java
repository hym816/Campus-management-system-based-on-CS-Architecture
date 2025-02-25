package Server.Class;

import Server.Public.ClientController;
import Server.Public.Course;
import Server.Public.Message;
import Server.Public.courseTime;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class CourseAddPage extends JDialog {

    private JTextArea courseNameField;
    private JTextArea courseIdField;
    private JTextArea courseTeacherField;
    private JTextArea courseLocationField;
    private JTextArea courseTypeField;
    private JTextArea courseCreditsField;
    private JTextArea courseCapacityField;
    private JTextArea totalTimeField;
    private JTextArea teacherIdField;
    private JComboBox<String> dayOfWeekComboBox;
    private JComboBox<String> startTimeComboBox;
    private JComboBox<String> endTimeComboBox;
    private JTextArea courseTimesListArea; // 显示已添加的课程时间段
    private JButton addTimeButton; // 添加课程时间按钮
    private JButton saveButton;

    private ArrayList<courseTime> courseTimes; // 存储多个时间段

    private JPanel contentPane;

    public CourseAddPage(JFrame parent) {
        super(parent, "添加课程", true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        setContentPane(contentPane);

        courseTimes = new ArrayList<>(); // 初始化课程时间列表

        JLabel idLabel = new JLabel("课程代码:");
        JLabel nameLabel = new JLabel("课程名称:");
        JLabel teacherLabel = new JLabel("授课教师:");
        JLabel locationLabel = new JLabel("上课地点:");
        JLabel typeLabel = new JLabel("课程类型:");
        JLabel creditsLabel = new JLabel("学分:");
        JLabel capacityLabel = new JLabel("课程容量:");
        JLabel timeLabel = new JLabel("课程时间:");
        JLabel totalTimeLabel = new JLabel("课时数:");
        JLabel teacherIdLabel = new JLabel("教师工号:");

        courseIdField = createTextAreaWithScroll(2, 20);
        courseNameField = createTextAreaWithScroll(2, 20);
        courseTeacherField = createTextAreaWithScroll(2, 20);
        courseLocationField = createTextAreaWithScroll(2, 20);
        courseTypeField = createTextAreaWithScroll(2, 20);
        courseCreditsField = createTextAreaWithScroll(2, 20);
        courseCapacityField = createTextAreaWithScroll(2, 20);
        totalTimeField = createTextAreaWithScroll(2, 20);
        teacherIdField = createTextAreaWithScroll(2, 20);

        // 添加下拉框用于选择星期几
        String[] daysOfWeek = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
        dayOfWeekComboBox = new JComboBox<>(daysOfWeek);

        // 添加下拉框用于选择起始时间和结束时间
        String[] timeSlots = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
        startTimeComboBox = new JComboBox<>(timeSlots);
        endTimeComboBox = new JComboBox<>(timeSlots);

        // 添加时间段显示区域
        courseTimesListArea = createTextAreaWithScroll(5, 20);
        courseTimesListArea.setEditable(false); // 禁止用户手动编辑，仅用于显示已添加的时间段

        // 添加课程时间按钮
        addTimeButton = new JButton("添加时间段");
        addTimeButton.addActionListener(e -> handleAddTimeButtonClick());

        saveButton = new JButton("保存");
        saveButton.addActionListener(this::handleSaveButtonClick);

        JPanel formPanel = new JPanel(new GridLayout(11, 2, 10, 10)); // 11 行 2 列
        formPanel.add(idLabel);
        formPanel.add(new JScrollPane(courseIdField));
        formPanel.add(nameLabel);
        formPanel.add(new JScrollPane(courseNameField));
        formPanel.add(teacherLabel);
        formPanel.add(new JScrollPane(courseTeacherField));
        formPanel.add(teacherIdLabel);
        formPanel.add(new JScrollPane(teacherIdField));
        formPanel.add(locationLabel);
        formPanel.add(new JScrollPane(courseLocationField));
        formPanel.add(typeLabel);
        formPanel.add(new JScrollPane(courseTypeField));
        formPanel.add(creditsLabel);
        formPanel.add(new JScrollPane(courseCreditsField));
        formPanel.add(capacityLabel);
        formPanel.add(new JScrollPane(courseCapacityField));

        // 课程时间选择
        formPanel.add(timeLabel);
        JPanel timePanel = new JPanel();
        timePanel.add(new JLabel("星期:"));
        timePanel.add(dayOfWeekComboBox);
        timePanel.add(new JLabel("开始时间:"));
        timePanel.add(startTimeComboBox);
        timePanel.add(new JLabel("结束时间:"));
        timePanel.add(endTimeComboBox);
        formPanel.add(timePanel);

        formPanel.add(new JLabel("已添加的时间段:"));
        formPanel.add(new JScrollPane(courseTimesListArea));

        formPanel.add(totalTimeLabel);
        formPanel.add(new JScrollPane(totalTimeField));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addTimeButton);
        buttonPanel.add(saveButton);

        contentPane.add(formPanel, BorderLayout.CENTER);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(parent);
    }

    private JTextArea createTextAreaWithScroll(int rows, int columns) {
        JTextArea textArea = new JTextArea(rows, columns);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        return textArea;
    }

    // 处理添加时间段按钮点击事件
    private void handleAddTimeButtonClick() {
        String dayOfWeek = (String) dayOfWeekComboBox.getSelectedItem();
        String startTime = (String) startTimeComboBox.getSelectedItem();
        String endTime = (String) endTimeComboBox.getSelectedItem();

        // 创建并添加新时间段
        courseTime time = new courseTime(startTime, endTime, dayOfWeek);
        courseTimes.add(time);

        // 更新显示的时间段列表
        courseTimesListArea.append(dayOfWeek + " 第" + startTime + "-" + endTime + "节\n");
    }

    // 处理保存按钮点击事件
    private void handleSaveButtonClick(ActionEvent e) {
        try {
            ClientController clientController = new ClientController();

            String courseId = courseIdField.getText();
            String courseName = courseNameField.getText();
            String courseTeacher = courseTeacherField.getText();
            String courseLocation = courseLocationField.getText();
            String courseType = courseTypeField.getText();
            double courseCredits = Double.parseDouble(courseCreditsField.getText());
            int courseCapacity = Integer.parseInt(courseCapacityField.getText());
            String teacherId = teacherIdField.getText();
            int totalTime = Integer.parseInt(totalTimeField.getText());

            // 使用已添加的课程时间段
            Course course = new Course(courseId, courseTeacher, courseName, courseType, courseLocation, courseCapacity, totalTime, courseCredits, courseTimes, 0, teacherId);

            List<Serializable> temp = new ArrayList<>();
            temp.add(course);
            Message message = new Message(Message.MessageType.add_course, temp);
            clientController.sendMessage(message);
            Message response = clientController.receiveMessage();

            if (response.getType().equals(Message.MessageType.add_course_success)) {
                JOptionPane.showMessageDialog(this, "课程信息已添加！", "添加成功", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "课程信息添加失败！", "添加失败", JOptionPane.INFORMATION_MESSAGE);
            }

            dispose(); // 数据库操作完成后关闭窗口

        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "操作过程中发生错误：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

}




/*public class CourseAddPage extends JDialog {

    private JTextArea courseNameField;
    private JTextArea courseIdField;
    private JTextArea courseTeacherField;
    private JTextArea courseLocationField;
    private JTextArea courseTypeField;
    private JTextArea courseCreditsField;
    private JTextArea courseCapacityField;
    private JTextArea courseTimeField;
    private JTextArea totalTimeField;
    private JTextArea teacherIdField;
    private JButton saveButton;

    private JPanel contentPane;

    public CourseAddPage(JFrame parent)
    {
        super(parent, "添加课程", true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        setContentPane(contentPane);

        JLabel idLabel = new JLabel("课程代码:");
        JLabel nameLabel = new JLabel("课程名称:");
        JLabel teacherLabel = new JLabel("授课教师:");
        JLabel locationLabel = new JLabel("上课地点:");
        JLabel typeLabel = new JLabel("课程类型:");
        JLabel creditsLabel = new JLabel("学分:");
        JLabel capacityLabel = new JLabel("课程容量:");
        JLabel timeLabel = new JLabel("课程时间:");
        JLabel totalTimeLabel = new JLabel("课时数:");
        JLabel teacherIdLabel = new JLabel("教师工号:");

        // 初始化 JTextArea 并为每个添加滚动条
        courseIdField = createTextAreaWithScroll(2, 20);
        courseNameField = createTextAreaWithScroll(2, 20);
        courseTeacherField = createTextAreaWithScroll(2, 20);
        courseLocationField = createTextAreaWithScroll(2, 20);
        courseTypeField = createTextAreaWithScroll(2, 20);
        courseCreditsField = createTextAreaWithScroll(2, 20);
        courseCapacityField = createTextAreaWithScroll(2, 20);
        courseTimeField = createTextAreaWithScroll(2, 20);
        totalTimeField = createTextAreaWithScroll(2, 20);
        teacherIdField = createTextAreaWithScroll(2, 20);

        saveButton = new JButton("保存");
        saveButton.addActionListener(this::handleSaveButtonClick);

        // 设置 formPanel 的布局，增加间隔
        JPanel formPanel = new JPanel(new GridLayout(10, 2, 10, 10)); // 10 行 2 列，水平和垂直间隔为 10 像素
        formPanel.add(idLabel);
        formPanel.add(new JScrollPane(courseIdField));
        formPanel.add(nameLabel);
        formPanel.add(new JScrollPane(courseNameField));
        formPanel.add(teacherLabel);
        formPanel.add(new JScrollPane(courseTeacherField));
        formPanel.add(teacherIdLabel);
        formPanel.add(new JScrollPane(teacherIdField));
        formPanel.add(locationLabel);
        formPanel.add(new JScrollPane(courseLocationField));
        formPanel.add(typeLabel);
        formPanel.add(new JScrollPane(courseTypeField));
        formPanel.add(creditsLabel);
        formPanel.add(new JScrollPane(courseCreditsField));
        formPanel.add(capacityLabel);
        formPanel.add(new JScrollPane(courseCapacityField));
        formPanel.add(timeLabel);
        formPanel.add(new JScrollPane(courseTimeField));
        formPanel.add(totalTimeLabel);
        formPanel.add(new JScrollPane(totalTimeField));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);

        contentPane.add(formPanel, BorderLayout.CENTER);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(parent);






    }
    private JTextArea createTextAreaWithScroll(int rows, int columns) {
        JTextArea textArea = new JTextArea(rows, columns);
        textArea.setLineWrap(true); // 自动换行
        textArea.setWrapStyleWord(true); // 以单词为单位换行
        return textArea;
    }

    public static ArrayList<courseTime> parseJsonToCourseTimeList(String jsonString) {
        Gson gson = new Gson();
        java.lang.reflect.Type listType = new TypeToken<ArrayList<courseTime>>(){}.getType();
        return gson.fromJson(jsonString, listType);
    }

    public static String parseFormattedTime(String formattedTime) {
        Gson gson = new Gson();
        List<Map<String, String>> timeSlots = new ArrayList<>();

        String[] lines = formattedTime.split("\n");
        for (String line : lines) {
            Map<String, String> timeSlot = new HashMap<>();

            // 假设输入格式为 "周三 第3-5节课"
            String[] parts = line.split(" ");
            String dayOfWeek = parts[0];
            String timeRange = parts[1].substring(1, parts[1].length() - 2); // 获取"第3-5节课"中的"3-5"
            String[] times = timeRange.split("-");

            timeSlot.put("dayOfWeek", dayOfWeek);
            timeSlot.put("startTime", times[0]);
            timeSlot.put("endTime", times[1]);

            timeSlots.add(timeSlot);
        }

        // 将 List<Map<String, String>> 转换为 JSON 字符串
        return gson.toJson(timeSlots);}


    private void handleSaveButtonClick(ActionEvent e) {
        try {
            ClientController clientController = new ClientController();

            String courseId = courseIdField.getText();
            String courseName = courseNameField.getText();
            String courseTeacher = courseTeacherField.getText();
            String courseLocation = courseLocationField.getText();
            String courseType = courseTypeField.getText();
            double courseCredits = Double.parseDouble(courseCreditsField.getText());
            int courseCapacity = Integer.parseInt(courseCapacityField.getText());
            String courseTime = courseTimeField.getText();
            int totalTime = Integer.parseInt(totalTimeField.getText());
            String teacherId = teacherIdField.getText();
            ArrayList<courseTime> times = parseJsonToCourseTimeList(parseFormattedTime(courseTime));

            Course course = new Course(courseId, courseTeacher, courseName, courseType, courseLocation, courseCapacity, totalTime, courseCredits, times,0,teacherId);

            List<Serializable>temp=new ArrayList<>();
            temp.add(course);
            Message message = new Message(Message.MessageType.add_course,temp);
            clientController.sendMessage(message);
            Message response = clientController.receiveMessage();

            if (response.getType().equals(Message.MessageType.add_course_success)) {
                JOptionPane.showMessageDialog(this, "课程信息已添加！", "添加成功", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "课程信息添加失败！", "添加失败", JOptionPane.INFORMATION_MESSAGE);
            }

            // 数据库操作完成后关闭窗口
            dispose();

        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "操作过程中发生错误：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }



}*/
