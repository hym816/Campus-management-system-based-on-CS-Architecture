package Server;

import Server.Public.ClientController;
import Server.Public.ContentPanel;
import Server.Public.GradeTable;
import Server.Public.Message;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static Server.MainWindow.stuid;

public class Option3Panel implements ContentPanel {

    private JPanel panel;
    private GradeTable gradeTable;

    public Option3Panel() {
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20)); // 设置四周20像素的安全区域

        // 创建标题标签
        JLabel titleLabel = new JLabel("成绩查询");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 16)); // 设置字体样式和大小
        titleLabel.setHorizontalAlignment(SwingConstants.LEFT); // 左对齐
        panel.add(titleLabel, BorderLayout.NORTH);

        ClientController clientController = null;
        try {
            clientController = new ClientController();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        List<Serializable> temp = new ArrayList<>();
        temp.add(stuid);
        try {
            clientController.sendMessage(new Message(Message.MessageType.get_score_by_studentid,temp));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Message response = null;
        try {
            response = clientController.receiveMessage();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        Object[][] data = (Object[][]) response.getContent().get(0);

        // 实例化TableWithFilters并将其添加到panel
        gradeTable = new GradeTable(data);
        panel.add(gradeTable, BorderLayout.CENTER); // 直接将组件添加到panel
    }

    @Override
    public JPanel getPanel() {
        return panel;
    }
}
