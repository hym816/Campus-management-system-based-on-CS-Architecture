package Server.Library;

import Server.InfoTable;
import Server.Public.ClientController;
import Server.Public.ContentPanel;
import Server.Public.Message;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class bestReader implements ContentPanel {
    private JPanel panel;
    private ClientController client;
    private InfoTable table;
    private DefaultTableModel model;
    private JScrollPane scrollPane;

    public bestReader() {
        panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // 四周添加20像素的边距

        // 顶部部分：标签
        RoundedPanel roundedPanel = new RoundedPanel(20, new Color(200, 200, 200));
        roundedPanel.setLayout(new GridBagLayout()); // 使用 GridBagLayout 居中内容
        roundedPanel.setPreferredSize(new Dimension(600, 80)); // 设置容器的大小
        JLabel label = new JLabel("借阅Top10：");
        label.setFont(new Font("黑体", Font.PLAIN, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER; // 居中标签
        roundedPanel.add(label, gbc);
        panel.add(roundedPanel, BorderLayout.NORTH);

        // 初始化表格
        initializeTable();

        // 底部部分：刷新按钮
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        Button refreshButton = new Button("刷新");
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadTableDataFromServer(); // 点击按钮时刷新表格数据
            }
        });
        bottomPanel.add(refreshButton);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        // 初次加载表格数据
        loadTableDataFromServer();
    }

    // 初始化表格的方法
    private void initializeTable() {
        Set<Integer> editableColumns = new HashSet<>(); // 定义可编辑列
        String[] columnNames = {"学号", "读者", "借阅次数"};
        model = new DefaultTableModel(columnNames, 0); // 初始化表格模型
        table = new InfoTable(0, 3, editableColumns);
        table.setModel(model);
        table.setRowHeight(30); // 设置表格的行高为30
        for (int i = 0; i < columnNames.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(100); // 调整每列宽度
        }
        scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
    }

    // 从服务器加载表格数据的方法
    private void loadTableDataFromServer() {
        // 从服务器获取数据
        List<Serializable> send = new ArrayList<>();
        send.add("0");
        List<Serializable> topReaders = new ArrayList<>();
        Message topBook = new Message(Message.MessageType.topReaders, send);

        try {
            client = new ClientController(); // 初始化 client
            client.sendMessage(topBook);
            Message receivedMessage = client.receiveMessage();
            topReaders = receivedMessage.getContent();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "无法连接到服务器，请检查网络连接。", "连接错误", JOptionPane.ERROR_MESSAGE);
        }

        updateTableData(topReaders);
    }

    // 更新表格数据的方法
    private void updateTableData(List<Serializable> topReaders) {
        int size = topReaders.size();
        model.setRowCount(0); // 清空现有数据

        if (size > 0) {
            for (int i = 0; i < size / 3; i++) {
                Object[] rowData = {
                        topReaders.get(0 + 3 * i),
                        topReaders.get(1 + 3 * i),
                        topReaders.get(2 + 3 * i),
                };
                model.addRow(rowData);
            }
            System.out.println("Added " + model.getRowCount() + " rows to the table.");
        } else {
            System.out.println("No data to add to the table.");
        }
        table.revalidate(); // 重新验证组件树
        table.repaint();    // 重新绘制组件
    }

    @Override
    public JPanel getPanel() {
        return panel;
    }
}
