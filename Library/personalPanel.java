package Server.Library;

import Server.InfoTable;
import Server.Public.ClientController;
import Server.Public.ContentPanel;
import Server.Public.Message;
import Server.Public.textField;
import Server.Public.Button;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
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

public class personalPanel implements ContentPanel {
    private JPanel panel;
    private ClientController client;
    private InfoTable table;
    private DefaultTableModel model;
    private JScrollPane scrollPane;
    private String id_1;

    private static int lendNum=0;

    public personalPanel(String id) {
        id_1 = id;
        panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // 四周添加20像素的边距

        // 顶部部分：标签
        RoundedPanel roundedPanel = new RoundedPanel(20, new Color(200, 200, 200));
        roundedPanel.setLayout(new GridBagLayout()); // 使用 GridBagLayout 居中内容
        roundedPanel.setPreferredSize(new Dimension(600, 80)); // 设置容器的大小
        JLabel label = new JLabel("个人借阅信息查询");
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

        //创建圆角容器
        RoundedPanel infoPanel = new RoundedPanel(20, new Color(220, 220, 220));
        infoPanel.setLayout(new GridBagLayout()); // 使用 GridBagLayout 居中内容
        infoPanel.setPreferredSize(new Dimension(300, 40)); // 设置容器的大小
        JLabel infoLabel = new JLabel("当前借阅最大限额5本");
        infoLabel.setFont(new Font("黑体", Font.PLAIN, 14));
        GridBagConstraints infoGbc = new GridBagConstraints();
        infoGbc.gridx = 0;
        infoGbc.gridy = 0;
        infoGbc.anchor = GridBagConstraints.CENTER; // 居中标签
        infoPanel.add(infoLabel, infoGbc);

        //将按钮和圆角容器加到底部的面板中
        bottomPanel.add(refreshButton);
        bottomPanel.add(infoPanel);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        // 初次加载表格数据
        loadTableDataFromServer();
    }

    // 初始化表格的方法
    private void initializeTable() {
        Set<Integer> editableColumns = new HashSet<>(); // 定义可编辑列
        String[] columnNames = {"借阅编号", "书籍编号", "书名", "开始时间", "截止时间", "状态", "归还","续借"};
        model = new DefaultTableModel(columnNames, 0); // 初始化表格模型
        table = new InfoTable(0, 8, editableColumns);
        table.setModel(model);
        table.setRowHeight(40); // 设置表格的行高为40，以便显示圆角标签
        for (int i = 0; i < columnNames.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(100); // 调整每列宽度
        }

        // 设置自定义圆角标签渲染器到“状态”列
        table.getColumnModel().getColumn(5).setCellRenderer(new RoundedLabelRenderer());

        scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
    }

    private void updateTableData(List<Serializable> personalData) {
        int size = personalData.size();
        model.setRowCount(0); // 清空现有数据
        lendNum=0;
        if (size > 0) {
            for (int i = 0; i < size / 6; i++) {
                String buttonText1 = "归还";
                String buttonText2 = "续借";
                Button button1 = new Button(buttonText1);
                Button button2 = new Button(buttonText2);


                String hid = (String) personalData.get(0 + 6 * i);//获取到当前借阅书籍的hid
                String end = (String) personalData.get(4 + 6 * i);//获取到当前借阅书籍的状态
                String status = (String) personalData.get(5 + 6 * i);//获取到当前借阅书籍的状态

                button1.addActionListener(e -> performBack(hid,status));

                button2.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (status.equals("0")) {
                            showDialog("你已归还，无需操作");
                        } else {
                            // 调用 performRenew 方法处理逻辑
                            performRenew(hid);
                        }
                    }
                });
                table.getColumn("归还").setCellRenderer(new ButtonRenderer());
                table.getColumn("归还").setCellEditor(new ButtonEditor(new JCheckBox()));

                table.getColumn("续借").setCellRenderer(new ButtonRenderer());
                table.getColumn("续借").setCellEditor(new ButtonEditor(new JCheckBox()));


                if(personalData.get(5 + 6 * i).equals("1")) {
                    lendNum++;
                }

                Object[] rowData = {
                        personalData.get(0 + 6 * i),
                        personalData.get(1 + 6 * i),
                        personalData.get(2 + 6 * i),
                        personalData.get(3 + 6 * i),
                        personalData.get(4 + 6 * i),
                        personalData.get(5 + 6 * i),
                        button1,
                        button2
                };
                model.addRow(rowData);
            }
            System.out.println("正在借阅"+lendNum);

        }
        table.revalidate(); // 重新验证组件树
        table.repaint();    // 重新绘制组件
    }
    // 从服务器加载表格数据的方法
    private void loadTableDataFromServer() {
        // 从服务器获取数据
        List<Serializable> send = new ArrayList<>();
        send.add(id_1);
        List<Serializable> personalData = null;  // 初始化为 null
        Message personalInfoMessage = new Message(Message.MessageType.borrowInfo, send);

        try {
            client = new ClientController(); // 初始化 client
            client.sendMessage(personalInfoMessage);
            Message receivedMessage = client.receiveMessage();
            personalData = receivedMessage.getContent();

            // 检查数据是否为 null
            if (personalData != null) {
                updateTableData(personalData);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "无法连接到服务器，请检查网络连接。", "连接错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public JPanel getPanel() {
        return panel;
    }

    private void performBack(String hid, String status) {
        try {
            // 使用 equals 方法来比较字符串内容
            if (status.equals("0")) {
                // 书籍已经归还，显示警告信息
                showDialog("你已归还，无需操作");
            } else {
                List<Serializable> send = new ArrayList<>();
                send.add(hid);


                try {
                    client = new ClientController();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                Message borrowMessage = new Message(Message.MessageType.returnBooks, send);

                // 发送归还消息
                try {
                    client.sendMessage(borrowMessage);
                    // 归还成功后弹出成功消息
                    showDialog("归还成功！");
                    lendNum--;
                    // 在成功对话框关闭后，从服务器重新加载表格数据
                    loadTableDataFromServer();

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                loadTableDataFromServer();
            }
        } catch (NumberFormatException e) {
            // 如果 hid 无法处理，显示错误信息
            showDialog("输入的格式不正确");

            // 在错误对话框关闭后可以更新表格数据
            loadTableDataFromServer();
        }
        loadTableDataFromServer();
    }
    public  void performRenew(String a) {

        // 弹出借阅时间输入窗口
        textField borrowTimeField = new textField(10);
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 设置借阅时间输入行
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("续借时间："), gbc);

        gbc.gridx = 1;
        panel.add(borrowTimeField, gbc);


        JLabel unitLabel = new JLabel("个月");
        gbc.gridx = 2;
        panel.add(unitLabel, gbc);

        // 自定义确定和取消按钮
        Button okButton = new Button("确定", Color.WHITE, 30, 100, 30);
        Button cancelButton = new Button("取消", Color.WHITE, 30, 100, 30);

        // 创建一个自定义对话框
        JDialog dialog = new JDialog((Frame) null, "请输入续借时间(单位/月)", true);
        dialog.setLayout(new GridBagLayout());

        // 添加输入面板
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        dialog.add(panel, gbc);

        // 添加按钮
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        dialog.add(buttonPanel, gbc);


        // 取消按钮事件处理
        cancelButton.addActionListener(e -> dialog.dispose());
        okButton.addActionListener(e -> {
            String n=borrowTimeField.getText();
            showDialog("续借成功！\n已续借"+n+"个月");
            dialog.dispose(); // 关闭对话框
            List<Serializable> send = new ArrayList<>();
            send.add(a);
            send.add(n);


            try {
                client = new ClientController();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

            Message renewMessage = new Message(Message.MessageType.renew, send);
            try {
                client.sendMessage(renewMessage);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            loadTableDataFromServer();

        });
        // 显示对话框
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }


    // 显示自定义提示对话框
    private void showDialog(String message) {
        JDialog successDialog = new JDialog((Frame) null, "提示", true);
        successDialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 添加成功提示标签
        gbc.gridx = 0;
        gbc.gridy = 0;
        successDialog.add(new JLabel(message), gbc);

        // 创建自定义确定按钮
        Button okButton = new Button("确定", Color.WHITE, 30, 100, 30);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        successDialog.add(okButton, gbc);

        // 确定按钮的事件处理，点击后关闭成功对话框
        okButton.addActionListener(evt -> successDialog.dispose());

        // 设置对话框的大小和可见性
        successDialog.pack();
        successDialog.setLocationRelativeTo(null);
        successDialog.setVisible(true);
    }

    public static int getLend(){
        return lendNum;
    }
}
// 自定义圆角标签渲染器类
class RoundedLabelRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel label = new JLabel((String) value, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 设置标签背景颜色和圆角
                if ("1".equals(value)) {
                    g2.setColor(new Color(255, 229, 204)); // 淡橙色背景
                } else if ("0".equals(value)) {
                    g2.setColor(new Color(204, 255, 204)); // 淡绿色背景
                } else if ("2".equals(value)) {
                    g2.setColor(new Color(255, 204, 204)); // 淡红色背景
                } else {
                    g2.setColor(getBackground());
                }

                g2.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 15, 15); // 绘制圆角矩形

                // 设置字体颜色为黑色
                g2.setColor(Color.BLACK);
                super.paintComponent(g);
            }
        };
        label.setFont(new Font("微软雅黑", Font.PLAIN, 14)); // 设置字体
        label.setOpaque(false); // 背景不透明
        label.setForeground(Color.BLACK); // 设置文字颜色为黑色

        // 根据状态设置文字内容
        switch ((String) value) {
            case "1":
                label.setText("借阅中");
                break;
            case "0":
                label.setText("已归还");
                break;
            case "2":
                label.setText("已逾期");
                break;
            default:
                label.setText("");
                break;
        }

        return label;
    }
}



