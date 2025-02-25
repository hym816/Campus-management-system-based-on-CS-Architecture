package Server.Class;

import Server.Public.*;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Schedule extends JPanel implements ContentPanel {
    private JPanel panel;
    private JTable table;
    private DefaultTableModel tableModel;
    private Object[][] data;
    private JPanel detailPanel;
    String id;

    public Schedule(String id) throws IOException, ClassNotFoundException {
        this.id=id;



        setLayout(new BorderLayout());

        String[] columnNames = {" ", "周一", "周二", "周三", "周四", "周五", "周六", "周日"};
        tableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 表格不可编辑
            }
        };

        table = new JTable(tableModel); // 确保table在此处已被初始化

        for (int i = 0; i < 13; i++) {
            tableModel.addRow(new String[columnNames.length]);
            table.setValueAt(i + 1, i, 0); // 现在table已被初始化，可以安全地调用setValueAt
        }

        panel = this;
        loadScheduleFromDatabase();

        customizeTableAppearance();
        add(new JScrollPane(table), BorderLayout.CENTER);











    }

    public void loadScheduleFromDatabase() throws IOException, ClassNotFoundException {

        ClientController clientController = new ClientController();
        List< Serializable> temp = new ArrayList<>();
        temp.add(id);
        clientController.sendMessage(new Message(Message.MessageType.get_course_by_studentid,temp));
        Message response = clientController.receiveMessage();
        List<Course> courses = (List<Course>) response.getContent().get(0);
        for (Course course : courses) {
            fillCourseInSchedule(course);
        }

        /*try {
            List<Course> courses = CourseDAO.getCourses(id);

            for (Course course : courses) {
                fillCourseInSchedule(course);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading schedule from database.", "Error", JOptionPane.ERROR_MESSAGE);
        }*/
    }

    private void fillCourseInSchedule(Course course) {
        String courseName = course.getCourseName();
        List<courseTime> courseTimes = course.getTimes();

        for (courseTime time : courseTimes) {
            int dayIndex = getDayIndex(time.getDayOfWeek());
            int startHour = Integer.parseInt(time.getStartTime());
            int endHour = Integer.parseInt(time.getEndTime());

            for (int i = startHour - 1; i < endHour; i++) {
                table.setValueAt(courseName, i, dayIndex);
            }
        }
    }

    private int getDayIndex(String dayOfWeek) {
        switch (dayOfWeek) {
            case "周一":
                return 1;
            case "周二":
                return 2;
            case "周三":
                return 3;
            case "周四":
                return 4;
            case "周五":
                return 5;
            case "周六":
                return 6;
            case "周日":
                return 7;
            default:
                return -1; // 错误处理
        }
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









    @Override
    public JPanel getPanel() {

        return panel;
    }

    /*public static void main()
    {
        SwingUtilities.invokeLater(() -> {
            // 创建测试数据
            Object[][] testData = {{"", "", "", "", "", "", "", ""}};
            // 初始化Schedule对象
            Schedule schedule = new Schedule();

            // 创建JFrame并设置基本属性
            JFrame frame = new JFrame("测试Schedule");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);

            // 添加Schedule到frame
            frame.add(schedule.getPanel(), BorderLayout.CENTER);

            // 显示窗口
            frame.setVisible(true);
        });


    }*/
}


