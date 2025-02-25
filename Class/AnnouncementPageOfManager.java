package Server.Class;


import Server.Public.Announcement;
import Server.Public.ClientController;
import Server.Public.Message;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AnnouncementPageOfManager extends JDialog {

    private JTable table;
    private DefaultTableModel model;
    private JPanel panel;

    public AnnouncementPageOfManager(Frame owner) throws IOException {
        super(owner, "公告列表", true);
        ClientController clientController = new ClientController();

        panel = new JPanel(new BorderLayout());
        panel.setLayout(new BorderLayout());
        // 添加 "发布公告" 按钮
        JButton publishButton = new JButton("发布公告");
        publishButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 发布公告的操作逻辑，例如弹出公告发布窗口
                showAddAnnouncementPage();
            }
        });

        JButton refreshButton = new JButton("刷新");
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshTable();
            }
        });

        // 创建按钮面板并将按钮右对齐
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(publishButton);
        buttonPanel.add(refreshButton);


        // 将按钮面板添加到主面板的北部（顶部）
        panel.add(buttonPanel, BorderLayout.NORTH);
        // 初始化表格模型
        model = new DefaultTableModel(new Object[]{"公告编号","公告标题"}, 0)
        {
            public boolean isCellEditable(int row, int column) {
                return column == -1;
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
    private void refreshTable() {
        // 清空表格中的所有行
        model.setRowCount(0);
        // 重新加载公告数据
        loadAnnouncements();
    }

    private void displayAnnouncementDetails(String announcementId) {
        try {
            ClientController clientController = new ClientController();
            List<Serializable> params = new ArrayList<>();
            params.add(announcementId);
            clientController.sendMessage(new Message(Message.MessageType.get_announcement_by_id, params));

            Message response = clientController.receiveMessage();
            Announcement announcement = (Announcement) response.getContent().get(0);

            // 创建新对话框显示公告的所有信息
            JDialog detailDialog = new JDialog(this, "公告详情", true);
            detailDialog.setLayout(new BorderLayout());

            JPanel infoPanel = new JPanel(new GridLayout(0, 2));  // 使用网格布局显示公告信息

            infoPanel.add(new JLabel("公告ID:"));
            JTextField idField = new JTextField(announcement.getAnnouncementId());
            idField.setEditable(false);
            infoPanel.add(idField);

            infoPanel.add(new JLabel("公告标题:"));
            JTextField titleField = new JTextField(announcement.getAnnouncementTitle());
            infoPanel.add(titleField);

            infoPanel.add(new JLabel("公告内容:"));
            JTextArea contentArea = new JTextArea(announcement.getAnnouncementContent());
            contentArea.setLineWrap(true);
            contentArea.setWrapStyleWord(true);
            infoPanel.add(new JScrollPane(contentArea));

            // 修改按钮
            JButton modifyButton = new JButton("修改公告");
            modifyButton.addActionListener((ActionEvent e) -> {
                try {
                    announcement.setAnnouncementTitle(titleField.getText());
                    announcement.setAnnouncementContent(contentArea.getText());

                    List<Serializable> modifyParams = new ArrayList<>();
                    modifyParams.add(announcement);
                    clientController.sendMessage(new Message(Message.MessageType.modify_announcement, modifyParams));
                    Message response1 = clientController.receiveMessage();
                    if(response1.getType().equals(Message.MessageType.success))
                        JOptionPane.showMessageDialog(detailDialog, "公告修改成功！");
                    else
                        JOptionPane.showMessageDialog(detailDialog, "公告修改失败！");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(detailDialog, "公告修改失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                } catch (ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            });

            // 删除按钮
            JButton deleteButton = new JButton("删除公告");
            deleteButton.addActionListener((ActionEvent e) -> {
                int confirm = JOptionPane.showConfirmDialog(detailDialog, "确定要删除此公告吗？", "确认删除", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        List<Serializable> deleteParams = new ArrayList<>();
                        deleteParams.add(announcement);
                        clientController.sendMessage(new Message(Message.MessageType.delete_announcement, deleteParams));

                        Message response2 = clientController.receiveMessage();
                        if(response2.getType().equals(Message.MessageType.success))
                            JOptionPane.showMessageDialog(detailDialog, "公告删除成功！");

                        detailDialog.dispose();  // 关闭详情窗口

                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(detailDialog, "公告删除失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    } catch (ClassNotFoundException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });

            // 底部按钮面板
            JPanel buttonPanel = new JPanel();
            buttonPanel.add(modifyButton);
            buttonPanel.add(deleteButton);

            detailDialog.add(infoPanel, BorderLayout.CENTER);
            detailDialog.add(buttonPanel, BorderLayout.SOUTH);
            detailDialog.setSize(500, 400);
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



    private void showAddAnnouncementPage()
    {
        JFrame frame = new JFrame("发布公告");
        AnnouncementAddPage announcementAddPage = new AnnouncementAddPage (frame);
        announcementAddPage.setModal(true);
        announcementAddPage.setVisible(true);

    }
}








