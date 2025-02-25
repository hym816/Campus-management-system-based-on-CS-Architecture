package Server;

import Server.Public.ContentPanel;
import Server.Public.Message;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class Option2Panel implements ContentPanel {

    private JPanel panel;
    private static String studentId = "";
    Message receivedMessage;

    public Option2Panel() {

        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20)); // 设置四周20像素的安全区域
        // 创建标题标签
        JLabel titleLabel = new JLabel("个人信息概览");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 16)); // 设置字体样式和大小
        titleLabel.setHorizontalAlignment(SwingConstants.LEFT); // 左对齐

        // 添加标题到页面左上角
        panel.add(titleLabel, BorderLayout.NORTH);


        // 创建可编辑表格，传入示例数据和编辑权限
        PersonalInfoTable editableTable = new PersonalInfoTable(MainWindow.stuid, true); // 这里的 true 可以根据需要设置
        // 添加表格到中心区域
        panel.add(editableTable.getTablePanel(), BorderLayout.CENTER);
    }


    @Override
    public JPanel getPanel() {
        return panel;
    }
}
