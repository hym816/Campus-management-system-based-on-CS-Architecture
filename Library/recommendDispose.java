package Server.Library;

import Server.InfoTable;
import Server.Public.*;
import Server.Public.Button;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
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



public class recommendDispose implements ContentPanel {
    private JPanel panel;
    private InfoTable infoTable;
    private static ClientController client;
    private DefaultTableModel model;
    private JScrollPane scrollPane;
    private InfoTable table;


    public recommendDispose() {
        panel = new JPanel(new BorderLayout(10, 10));
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        Button update = new Button("刷新");
        topPanel.add(update);

        update.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshTableData(); // 点击按钮时刷新表格数据
            }
        });


        // 将按钮面板放置在顶部区域
        panel.add(topPanel, BorderLayout.NORTH);



        initializeTable();
        loadTableDataFromServer();

    }
    @Override
    public JPanel getPanel() {
        return panel;
    }



    private void initializeTable() {
        Set<Integer> editableColumns = new HashSet<>(); // 定义可编辑列
        String[] columnNames = {"书名", "作者", "出版社",  "答复","处理"};
        model = new DefaultTableModel(columnNames, 0); // 初始化表格模型
        table = new InfoTable(0, 5, editableColumns);
        table.setModel(model);
        table.setRowHeight(40); // 设置表格的行高为40，以便显示圆角标签
        for (int i = 0; i < columnNames.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(100); // 调整每列宽度
        }
        scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
    }
    private void updateTableData(List<Serializable> personalData) {
        int size = personalData.size();
        model.setRowCount(0); // 清空现有数据


        System.out.println("size"+size);

        if (size > 0) {




            for (int i = 0; i < size / 4; i++) {
                String buttonText = "答复";
                Button button = new Button(buttonText, Color.WHITE, 30, 100, 30);
                table.getColumn("处理").setCellRenderer(new ButtonRenderer());
                table.getColumn("处理").setCellEditor(new ButtonEditor(new JCheckBox()));
                String bookname1=(String) personalData.get(0 + 4 * i);;
                button.addActionListener(e -> performDispose(bookname1));

                Object[] rowData = {
                        personalData.get(0 + 4 * i),
                        personalData.get(1 + 4 * i),
                        personalData.get(2 + 4 * i),
                        personalData.get(3 + 4 * i),
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
    // 从服务器加载表格数据的方法
    private void loadTableDataFromServer() {
        // 从服务器获取数据
        List<Serializable> send = new ArrayList<>();
        send.add("0");
        List<Serializable> recInfo = null;  // 初始化为 null
        Message personalInfoMessage = new Message(Message.MessageType.getRecommend, send);

        try {
            client = new ClientController(); // 初始化 client
            client.sendMessage(personalInfoMessage);
            Message receivedMessage = client.receiveMessage();
            recInfo = receivedMessage.getContent();

            // 检查数据是否为 null
            if (recInfo != null) {
                updateTableData(recInfo);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "无法连接到服务器，请检查网络连接。", "连接错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshTableData() {
        loadTableDataFromServer(); // 重新加载表格数据
    }

    private void showRecommendDialog() {
        // 创建一个新的对话框
        JDialog dialog = new JDialog((Frame) null, "填写荐购信息", true);
        dialog.setLayout(new GridBagLayout());
        dialog.setSize(400, 300);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // 使用自定义的文本框
        textField bookNameField = new textField(20);
        textField authorField = new textField(20);
        textField publisherField = new textField(20);

        // 添加标签和文本框到对话框
        dialog.add(new JLabel("书名："), gbc);
        gbc.gridx = 1;
        dialog.add(bookNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        dialog.add(new JLabel("作者："), gbc);
        gbc.gridx = 1;
        dialog.add(authorField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        dialog.add(new JLabel("出版社："), gbc);
        gbc.gridx = 1;
        dialog.add(publisherField, gbc);

        // 确定按钮
        Button confirmButton = new Button("确定");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        dialog.add(confirmButton, gbc);

        // 为确定按钮添加事件监听器，点击时获取文本框内容
        confirmButton.addActionListener(e -> {
            String bookName = bookNameField.getText();
            String author = authorField.getText();
            String publisher = publisherField.getText();

            // 调用推荐方法并传递获取的值
            performRecommend(bookName, author, publisher);

            // 关闭对话框
            dialog.dispose();
        });

        // 显示对话框
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private void performRecommend(String a,String b,String c) {

        List<Serializable> send = new ArrayList<>();
        send.add(a);
        send.add(b);
        send.add(c);
        try {
            client=new ClientController();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Message queryMessage = new Message(Message.MessageType.recommend, send);

        // 发送查询消息
        try {
            client.sendMessage(queryMessage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
    private static void performDispose(String bookname) {
        // 创建弹出窗口
        JFrame popupFrame = new JFrame("弹出窗口");
        popupFrame.setSize(400, 200);
        popupFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // 设置布局
        popupFrame.setLayout(new BoxLayout(popupFrame.getContentPane(), BoxLayout.Y_AXIS));

        // 添加标签
        JLabel label = new JLabel("请开始答复");
        popupFrame.add(label);

        // 使用自定义的文本框
        TextField textField = new TextField();
        popupFrame.add(textField);

        // 使用自定义的按钮
        Button confirmButton = new Button("确定");
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 获取文本框中的值
                String inputA = textField.getText(); // 假设 textField 是您的自定义文本框，并有 getText() 方法
                // 调用 apply 方法
                apply(bookname,inputA);

                // 关闭弹出窗口
                popupFrame.dispose();
            }
        });

        popupFrame.add(confirmButton);
        popupFrame.setLocationRelativeTo(null);
        // 显示弹出窗口
        popupFrame.setVisible(true);
    }

    private static void apply(String a,String b) {

        List<Serializable> send = new ArrayList<>();
        send.add(a);
        send.add(b);

        try {
            client=new ClientController();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Message queryMessage = new Message(Message.MessageType.disposeRecommend, send);

        // 发送查询消息
        try {
            client.sendMessage(queryMessage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

