package Server;

import Server.Public.ContentPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class StuInfo implements ContentPanel {

    private JPanel panel;
    private StudentInfoTable studentInfoTable; // StudentInfoTable实例
    public StuInfo() {
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20)); // 设置四周20像素的安全区域

        // 添加左上角的“学生信息管理”标签
        JLabel titleLabel = new JLabel("学生信息管理");
        titleLabel.setFont(new Font("宋体", Font.BOLD, 18)); // 设置字体样式
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.add(titleLabel);

        // 创建StudentInfoTable实例并添加到中心区域
        Object[][] testData = {
                {"大一", "001", "计算机学院", "一班", "张三", "男", "2000-01-01", "123456789012345678", "2018-09-01", "在读", "zhangsan@example.com", "12345678901", "北京市海淀区", "/path/to/avatar1.png"},
                {"大二", "002", "电子信息学院", "二班", "李四", "女", "2001-02-02", "987654321098765432", "2017-09-01", "在读", "lisi@example.com", "09876543210", "北京市朝阳区", "/path/to/avatar2.png"},
        };
        studentInfoTable = new StudentInfoTable();

        // 将“学生信息管理”标签和表格添加到主面板
        panel.add(titlePanel, BorderLayout.NORTH);
        panel.add(studentInfoTable, BorderLayout.CENTER);
    }

    @Override
    public JPanel getPanel() {
        return panel;
    }
}
