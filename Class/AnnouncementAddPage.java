package Server.Class;

import Server.Public.Announcement;
import Server.Public.ClientController;
import Server.Public.Message;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AnnouncementAddPage extends JDialog {


    private JTextField announcementTitleField;
    private JTextField announcementIdField;
    private JTextField announcementContentField;


    private JButton saveButton;
    private JPanel contentPane;

    public AnnouncementAddPage(JFrame parent) {


        super(parent, "添加公告", true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        setContentPane(contentPane);

        JLabel idLabel = new JLabel("公告编号:");
        JLabel titleLabel = new JLabel("公告标题:");
        JLabel contentLabel = new JLabel("公告内容:");

        announcementIdField = new JTextField(20);
        announcementContentField = new JTextField(20);
        announcementTitleField = new JTextField(20);


        saveButton = new JButton("保存");
        saveButton.addActionListener(this::handleSaveButtonClick);

        JPanel formPanel = new JPanel(new GridLayout(3, 2));
        formPanel.add(idLabel);
        formPanel.add(announcementIdField);
        formPanel.add(titleLabel);
        formPanel.add(announcementTitleField);
        formPanel.add(contentLabel);
        formPanel.add(announcementContentField);


        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);

        contentPane.add(formPanel, BorderLayout.CENTER);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(parent);


    }

    private void handleSaveButtonClick(ActionEvent e) {
        try {
            ClientController clientController = new ClientController();

            String announcementId = announcementIdField.getText();
            String announcementName = announcementTitleField.getText();
            String announcementContent = announcementContentField.getText();
            LocalDateTime now = LocalDateTime.now();
            Timestamp announcementTime = Timestamp.valueOf(now);

            Announcement announcement = new Announcement(announcementId, announcementName, announcementContent, announcementTime);
            List<Serializable> temp = new ArrayList<>();
            temp.add(announcement);

            Message message = new Message(Message.MessageType.add_announcement, temp);
            clientController.sendMessage(message);

            Message response = clientController.receiveMessage();
            if (response.getType().equals(Message.MessageType.success)) {
                JOptionPane.showMessageDialog(this, "公告已添加！", "添加成功", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "公告添加失败！", "添加失败", JOptionPane.INFORMATION_MESSAGE);
            }

            dispose(); // 关闭窗口

        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace(); // 记录异常细节
            JOptionPane.showMessageDialog(this, "操作时发生错误：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }


}

