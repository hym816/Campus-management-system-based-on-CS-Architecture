package Server.Class;


import Server.Public.Announcement;
import Server.Public.ClientController;
import Server.Public.Message;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AnnouncementPageOfUsers extends JDialog {



    private JTable table;
    private DefaultTableModel model;
    private JPanel panel;

    public AnnouncementPageOfUsers(Frame owner) throws IOException {

        super(owner, "公告列表", true);
        ClientController clientController = new ClientController();
        panel = new JPanel(new BorderLayout());
        panel.setLayout(new BorderLayout());
        // 初始化表格模型
        model = new DefaultTableModel(new Object[]{"公告编号","公告标题"}, 0)
        {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // 初始化表格
        table = new JTable(model)
        {
        };

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    String announcementId = (String) table.getValueAt(row, 0);  // 获取公告ID
                    displayAnnouncementDetails(announcementId);  // 调用函数显示公告详情
                }
            }
        });
        loadAnnouncements();
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        table.setRowHeight(30);
        this.add(panel);
        this.pack();  // 包装窗口大小以适应其组件
        this.setVisible(true);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }
    private void displayAnnouncementDetails(String announcementId) {
        try {
            ClientController clientController = new ClientController();
            List<Serializable> params = new ArrayList<>();
            params.add(announcementId);
            clientController.sendMessage(new Message(Message.MessageType.get_announcement_by_id, params));
            Message response = clientController.receiveMessage();
            Announcement announcement = (Announcement) response.getContent().get(0);
            // 创建新对话框显示公告内容
            JDialog detailDialog = new JDialog(this, "公告详情", true);
            JTextArea textArea = new JTextArea(announcement.getAnnouncementContent());
            textArea.setWrapStyleWord(true);
            textArea.setLineWrap(true);
            textArea.setEditable(false);
            detailDialog.add(new JScrollPane(textArea), BorderLayout.CENTER);
            detailDialog.setSize(400, 300);
            detailDialog.setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "加载公告详情失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void loadAnnouncements() {
        try {
            ClientController clientController = new ClientController();
            clientController.sendMessage(new Message(Message.MessageType.get_all_announcement));
            Message response = clientController.receiveMessage();
            List<Announcement> announcements = (List<Announcement>) response.getContent().get(0);
            System.out.println("Loaded announcements count: " + announcements.size());
            for (Announcement announcement : announcements) {
                String id =announcement.getAnnouncementId();
                String title = announcement.getAnnouncementTitle();

                model.addRow(new Object[]{id,title});
            }
            System.out.println("Model row count after loading: " + model.getRowCount());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "加载公告数据失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }


}









