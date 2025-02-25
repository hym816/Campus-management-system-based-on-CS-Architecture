
package Server.Library;

import Server.InfoTable;
import Server.Public.*;
import Server.Public.Button;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class popularBook implements ContentPanel {

    private JPanel panel;
    private ClientController client;
    public static List<Serializable> topBooks = new ArrayList<>();
    private InfoTable table;
    private DefaultTableModel model;
    private JScrollPane scrollPane;

    public static List<Serializable> info = new ArrayList<>();
public static String id_1;
    public popularBook(String id) {
        id_1=id;
        panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // 四周添加20像素的边距

        // 创建自定义的圆角容器
        RoundedPanel roundedPanel = new RoundedPanel(20, new Color(200, 200, 200));
        roundedPanel.setLayout(new GridBagLayout()); // 使用 GridBagLayout 居中内容
        roundedPanel.setPreferredSize(new Dimension(600, 80)); // 设置容器的大小

        // 添加“热门榜单Top10”的标签到圆角容器
        JLabel label = new JLabel("热门榜单Top10：");
        label.setFont(new Font("黑体", Font.PLAIN, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER; // 居中标签
        roundedPanel.add(label, gbc);

        panel.add(roundedPanel, BorderLayout.NORTH);

        // 初始化表格
        initializeTable();
        table.setRowHeight(100);
        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(50);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(50);
        table.getColumnModel().getColumn(5).setPreferredWidth(50);
        table.getColumnModel().getColumn(6).setPreferredWidth(100);
        table.getColumnModel().getColumn(7).setPreferredWidth(50);

        // 底部部分：刷新按钮
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        Button refreshButton = new Button("刷新");
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadTableDataFromServer();
                loadTableDataFromServer(); // 点击按钮时刷新表格数据
            }
        });
        bottomPanel.add(refreshButton);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        loadTableDataFromServer();
    }

    // 初始化表格的方法
    private void initializeTable() {
        Set<Integer> editableColumns = new HashSet<>();
        String[] columnNames = {"书籍编号", "书名", "作者", "库存", "出版社", "风格", "借阅次数", "操作"};
        model = new DefaultTableModel(columnNames, 0); // 初始化表格模型
        table = new InfoTable(0, 8, editableColumns);
        table.setModel(model);
        table.setRowHeight(30); // 设置表格的行高为30
        for (int i = 0; i < columnNames.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(100); // 调整每列宽度
        }
        scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
    }

    // 加载表格数据的方法
    private void loadTableDataFromServer() {
        // 从服务器获取数据
        List<Serializable> send = new ArrayList<>();
        send.add("0");
        Message topBook = new Message(Message.MessageType.topBooks, send);

        try {
            client = new ClientController(); // 初始化 client
            client.sendMessage(topBook);
            Message receivedMessage = client.receiveMessage();
            topBooks = receivedMessage.getContent();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "无法连接到服务器，请检查网络连接。", "连接错误", JOptionPane.ERROR_MESSAGE);
        }

        updateTableData(topBooks.size());
    }

    // 更新表格数据的方法
    private void updateTableData(int size) {
        model.setRowCount(0); // 清空现有数据

        if (size > 0) {
            for (int i = 0; i < size / 7; i++) {
                String buttonText = "借阅";
                Button button = new Button(buttonText, Color.WHITE, 30, 100, 30);
                table.getColumn("操作").setCellRenderer(new ButtonRenderer());
                table.getColumn("操作").setCellEditor(new ButtonEditor(new JCheckBox()));

                String bid = (String) topBooks.get(0 + 7 * i); // 获取当前书籍的编号
                String nums = (String) topBooks.get(3 + 7 * i); // 获取当前书籍的库存数量
                button.addActionListener(e -> performBorrow(bid,nums));

                Object[] rowData = {

                        topBooks.get(0 + 7 * i),
                        topBooks.get(1 + 7 * i),
                        topBooks.get(2 + 7 * i),
                        topBooks.get(3 + 7 * i),
                        topBooks.get(4 + 7 * i),
                        topBooks.get(5 + 7 * i),
                        topBooks.get(6 + 7 * i),
                        button
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

    private void performBorrow(String bid, String nums) {

        try {
            int num = Integer.parseInt(nums); // 将字符串类型的nums转换为int类型
            if (num == 0) {
                // 书籍数量不足，不可借阅
                JOptionPane.showMessageDialog(null, "书籍数量不足，不可借阅", "提示", JOptionPane.WARNING_MESSAGE);
            } else if(personalPanel.getLend()>=5){
                showDialog("您的借阅书目已经超过限额。请先还书");
            }
            else {
                // 弹出借阅时间输入窗口
                textField borrowTimeField = new textField(10);
                JPanel panel = new JPanel(new GridBagLayout());
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.insets = new Insets(10, 10, 10, 10);
                gbc.fill = GridBagConstraints.HORIZONTAL;

                // 设置借阅时间输入行
                gbc.gridx = 0;
                gbc.gridy = 0;
                panel.add(new JLabel("借阅时间："), gbc);

                gbc.gridx = 1;
                panel.add(borrowTimeField, gbc);

                JLabel unitLabel = new JLabel("个月");
                gbc.gridx = 2;
                panel.add(unitLabel, gbc);

                // 自定义确定和取消按钮
                Button okButton = new Button("确定", Color.WHITE, 30, 100, 30);
                Button cancelButton = new Button("取消", Color.WHITE, 30, 100, 30);

                // 创建一个自定义对话框
                JDialog dialog = new JDialog((Frame) null, "请输入借阅时间(单位/月)", true);
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

                // 确定按钮事件处理
                okButton.addActionListener(e -> {
                    String borrowTime = borrowTimeField.getText();
                    if (!borrowTime.isEmpty()) {
                        // 借阅时间已经设置
                        showSuccessDialog(dialog, borrowTime); // 显示成功提示框
                        dialog.dispose(); // 关闭对话框
                        // 后续逻辑
                        List<Serializable> borrow = new ArrayList<>();

                        borrow.add(bid);
                        borrow.add(id_1);
                        borrow.add(borrowTime);

                        try {
                            client = new ClientController();

                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                        Message borrowMessage = new Message(Message.MessageType.borrow, borrow);

                        // 发送查询消息
                        try {
                            client.sendMessage(borrowMessage);
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }

                        Message receivedMessage = null;
                        try {
                            receivedMessage = client.receiveMessage();
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        } catch (ClassNotFoundException ex) {
                            throw new RuntimeException(ex);
                        }
                        info = receivedMessage.getContent();
                    } else {
                        JOptionPane.showMessageDialog(dialog, "请输入借阅时间", "提示", JOptionPane.WARNING_MESSAGE);
                    }
                });

                // 取消按钮事件处理
                cancelButton.addActionListener(e -> dialog.dispose());

                // 显示对话框
                dialog.pack();
                dialog.setLocationRelativeTo(null);
                dialog.setVisible(true);
            }
        } catch (NumberFormatException e) {
            // 如果nums无法转换为整数，显示错误信息
            JOptionPane.showMessageDialog(null, "输入的数量格式不正确", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 自定义成功提示对话框
    private void showSuccessDialog(JDialog parent, String borrowTime) {
        JDialog successDialog = new JDialog(parent, "借阅成功", true);
        successDialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 添加成功提示标签
        gbc.gridx = 0;
        gbc.gridy = 0;
        successDialog.add(new JLabel("借阅时间已经设置为：" + borrowTime + "个月"), gbc);

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
        successDialog.setLocationRelativeTo(parent);
        successDialog.setVisible(true);
    }

    @Override
    public JPanel getPanel() {
        return panel;
    }

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
}
