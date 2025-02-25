/**
 * reviseInfo 类表示图书管理系统中修改图书信息的面板。
 */
package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import Server.Library.ButtonEditor;
import Server.Library.ButtonRenderer;
import Server.Library.MainWindowStu;
import Server.Public.Button;
import Server.InfoTable;
import Server.Library.ComboBoxPanel;
import Server.Public.ClientController;
import Server.Public.ContentPanel;
import Server.Public.Message;
import Server.Public.textField;
import Server.SearchBox;

/**
 * reviseInfo 类实现了 ContentPanel 接口，用于展示图书信息的修改界面。
 */
public class reviseInfo implements ContentPanel {

    private JPanel panel;
    private JComboBox<String> comboBox;
    private SearchBox searchBox;
    private ClientController client;
    private InfoTable table;
    public static List<Serializable> info = new ArrayList<>();
    public DefaultTableModel model ; // 只创建一次


    /**
     * 构造方法，初始化修改图书信息面板。
     */
    public reviseInfo() {

        panel = new JPanel(new BorderLayout(10, 10)); // 使用BorderLayout布局，并设置水平和垂直间距

        // 创建顶部面板，用于放置下拉栏、搜索框和按钮
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10)); // FlowLayout布局，左对齐，组件间距10像素
        panel.add(topPanel, BorderLayout.NORTH); // 将顶部面板放置在主面板的北部

        ComboBoxPanel comboBoxPanel = new ComboBoxPanel();
        comboBox = comboBoxPanel.getComboBox();
        topPanel.add(comboBoxPanel);

        // 创建 SearchBox 实例
        searchBox = new SearchBox();
        topPanel.add(searchBox);

        // 创建 Button 实例
        Button searchButton = new Button("搜索", Color.WHITE, 30, 100, 30);
        topPanel.add(searchButton);
        searchButton.addActionListener(e -> performSearch());

        Set<Integer> editableColumns = new HashSet<>();
        table = new InfoTable(0, 8, editableColumns); // 修改列数为8
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER); // 将表格放在主面板的中部

        // 添加表头
        String[] columnNames = {"编号", "书名", "作者", "国籍", "库存", "出版社", "风格", "操作1", "操作2"};
        model = new DefaultTableModel(columnNames, 0); // 只创建一次
        table.setModel(model);

        table.setRowHeight(30);
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(80);
        table.getColumnModel().getColumn(4).setPreferredWidth(60);
        table.getColumnModel().getColumn(5).setPreferredWidth(200);
        table.getColumnModel().getColumn(6).setPreferredWidth(100);

        // 为主面板添加边距
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // 四周添加20像素的边距
    }

    /**
     * 实现 ContentPanel 接口的方法，返回面板。
     *
     * @return 返回面板。
     */
    @Override
    public JPanel getPanel() {
        return panel;
    }

    /**
     * 执行搜索操作的方法。
     */
    public void performSearch() {
        List<Serializable> queryInfo = new ArrayList<>();
        String bookName = searchBox.getText();
        String type = (String) comboBox.getSelectedItem();

        queryInfo.add(type);
        queryInfo.add(bookName);

        try {
            client = new ClientController();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Message queryMessage = new Message(Message.MessageType.search, queryInfo);

        // 发送查询消息
        try {
            client.sendMessage(queryMessage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Message receivedMessage = null;
        try {
            receivedMessage = client.receiveMessage();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        info = receivedMessage.getContent();

        int size = info.size();

        if (size == 0) {
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(panel);
            if (topFrame instanceof MainWindowStu) {
                ((MainWindowStu) topFrame).switchContent("修改失败");
            }
        } else {
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0); // 清空现有数据

            // 设置“操作1”和“操作2”列的渲染器和编辑器
            table.getColumn("操作1").setCellRenderer(new ButtonRenderer());
            table.getColumn("操作1").setCellEditor(new ButtonEditor(new JCheckBox()));
            table.getColumn("操作2").setCellRenderer(new ButtonRenderer());
            table.getColumn("操作2").setCellEditor(new ButtonEditor(new JCheckBox()));

            if (size > 0) {
                for (int i = 0; i < size / 7; i++) {

                    // 创建“修改”按钮
                    Button reviseButton = new Button("修改", Color.WHITE, 30, 100, 30);
                    reviseButton.addActionListener(e -> performRevise());

                    // 创建“删除”按钮
                    Button deleteButton = new Button("删除", Color.WHITE, 30, 100, 30);
                    String bid = (String) info.get(0 + 7 * i);//获取到当前借阅书籍的uid
                    deleteButton.addActionListener(e -> performDelete(bid));


                    Object[] rowData = {
                            info.get(0 + 7 * i),
                            info.get(1 + 7 * i),
                            info.get(2 + 7 * i),
                            info.get(3 + 7 * i),
                            info.get(4 + 7 * i),
                            info.get(5 + 7 * i),
                            info.get(6 + 7 * i),
                            reviseButton, // “操作1”的按钮
                            deleteButton  // “操作2”的按钮
                    };
                    model.addRow(rowData);

                }

            }
//            table.revalidate(); // 重新验证组件树
//            table.repaint();    // 重新绘制组件
        }
    }

    /**
     * 执行修改操作的方法。
     */
    public void performRevise() {
        // 获取当前选中的行索引
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(panel, "请先选择一行进行修改", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 获取表格中选中行的各列数据
        String bookId = (String) table.getValueAt(selectedRow, 0);
        String bookName = (String) table.getValueAt(selectedRow, 1);
        String author = (String) table.getValueAt(selectedRow, 2);
        String country = (String) table.getValueAt(selectedRow, 3);
        String stock = (String) table.getValueAt(selectedRow, 4);
        String publisher = (String) table.getValueAt(selectedRow, 5);
        String type = (String) table.getValueAt(selectedRow, 6);

        // 创建修改信息的窗口
        JDialog reviseDialog = new JDialog((Frame) null, "修改信息", true);
        reviseDialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 添加标签
        gbc.gridx = 0;
        gbc.gridy = 0;
        reviseDialog.add(new JLabel("修改信息"), gbc);

        // 创建文本框
        textField bookIdField = new textField(20);
        bookIdField.setText(bookId);
        bookIdField.setEditable(false);  // 设置书籍编号为不可修改
        gbc.gridx = 0;
        gbc.gridy = 1;
        reviseDialog.add(new JLabel("编号："), gbc);
        gbc.gridx = 1;
        reviseDialog.add(bookIdField, gbc);

        textField bookNameField = new textField(20);
        bookNameField.setText(bookName);
        gbc.gridx = 0;
        gbc.gridy = 2;
        reviseDialog.add(new JLabel("书名："), gbc);
        gbc.gridx = 1;
        reviseDialog.add(bookNameField, gbc);

        textField authorField = new textField(20);
        authorField.setText(author);
        gbc.gridx = 0;
        gbc.gridy = 3;
        reviseDialog.add(new JLabel("作者："), gbc);
        gbc.gridx = 1;
        reviseDialog.add(authorField, gbc);

        textField countryField = new textField(20);
        countryField.setText(country);
        gbc.gridx = 0;
        gbc.gridy = 4;
        reviseDialog.add(new JLabel("国籍："), gbc);
        gbc.gridx = 1;
        reviseDialog.add(countryField, gbc);

        textField stockField = new textField(20);
        stockField.setText(stock);
        gbc.gridx = 0;
        gbc.gridy = 5;
        reviseDialog.add(new JLabel("库存："), gbc);
        gbc.gridx = 1;
        reviseDialog.add(stockField, gbc);

        textField publisherField = new textField(20);
        publisherField.setText(publisher);
        gbc.gridx = 0;
        gbc.gridy = 6;
        reviseDialog.add(new JLabel("出版社："), gbc);
        gbc.gridx = 1;
        reviseDialog.add(publisherField, gbc);

        textField typeField = new textField(20);
        typeField.setText(type);
        gbc.gridx = 0;
        gbc.gridy = 7;
        reviseDialog.add(new JLabel("风格："), gbc);
        gbc.gridx = 1;
        reviseDialog.add(typeField, gbc);

        // 创建确定按钮
        Button confirmButton = new Button("确定", Color.WHITE, 30, 100, 30);
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        reviseDialog.add(confirmButton, gbc);

        // 确定按钮的事件处理
        confirmButton.addActionListener(evt -> {
            // 验证所有字段是否已填写
            if (bookNameField.getText().isEmpty() || authorField.getText().isEmpty() ||
                    countryField.getText().isEmpty() || stockField.getText().isEmpty() ||
                    publisherField.getText().isEmpty() || typeField.getText().isEmpty()) {

                // 显示错误提示
                showErrorDialog(reviseDialog);
            } else {
                // 执行修改逻辑
                confirmRevise(bookIdField, bookNameField, authorField, countryField, stockField, publisherField, typeField, reviseDialog);
            }
        });

        // 设置对话框的大小和可见性
        reviseDialog.pack();
        reviseDialog.setLocationRelativeTo(panel);
        reviseDialog.setVisible(true);
    }

    /**
     * 弹出错误信息的界面
     * @param parent 父窗口
     */
    public void showErrorDialog(JDialog parent) {
        JDialog errorDialog = new JDialog(parent, "错误", true);
        errorDialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 添加错误提示标签
        gbc.gridx = 0;
        gbc.gridy = 0;
        errorDialog.add(new JLabel("信息未填写完整！"), gbc);

        // 创建自定义确定按钮
        Button okButton = new Button("确定", Color.WHITE, 30, 100, 30);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        errorDialog.add(okButton, gbc);

        // 确定按钮的事件处理，点击后关闭错误对话框
        okButton.addActionListener(evt -> errorDialog.dispose());

        // 设置对话框的大小和可见性
        errorDialog.pack();
        errorDialog.setLocationRelativeTo(parent);
        errorDialog.setVisible(true);
    }

    /**
     * 弹出成功信息的界面
     * @param parent 父窗口
     */
    public void showSuccessDialog(JDialog parent) {
        JDialog successDialog = new JDialog(parent, "成功", true);
        successDialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 添加成功提示标签
        gbc.gridx = 0;
        gbc.gridy = 0;
        successDialog.add(new JLabel("操作成功！"), gbc);

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

    /**
     * 执行删除操作的方法。
     *
     * @param bid 要删除的图书编号。
     */
    public void performDelete(String bid) {
        // 准备要发送的数据
        List<Serializable> send = new ArrayList<>();
        send.add(bid);

        // 创建 ClientController 实例
        try {
            client = new ClientController();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(panel, "客户端连接失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            return; // 连接失败，终止操作
        }

        // 创建删除消息
        Message deleteMessage = new Message(Message.MessageType.delete, send);


        // 发送删除请求消息
        try {
            client.sendMessage(deleteMessage);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(panel, "消息发送失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
        showDialog("操作成功！");
        model.setRowCount(0);
    }

    /**
     * 确认修改操作的方法。
     *
     * @param bookIdField 图书编号文本框。
     * @param bookNameField 图书名称文本框。
     * @param authorField 作者文本框。
     * @param countryField 国籍文本框。
     * @param stockField 库存文本框。
     * @param publisherField 出版社文本框。
     * @param typeField 风格文本框。
     * @param reviseDialog 修改对话框。
     */
    public void confirmRevise(textField bookIdField, textField bookNameField, textField authorField, textField countryField,
                              textField stockField, textField publisherField, textField typeField, JDialog reviseDialog) {
        // 获取文本框中的新内容
        String newBookId = bookIdField.getText().trim();
        String newBookName = bookNameField.getText().trim();
        String newAuthor = authorField.getText().trim();
        String newCountry = countryField.getText().trim();
        String newStock = stockField.getText().trim();
        String newPublisher = publisherField.getText().trim();
        String newType = typeField.getText().trim();

        List<Serializable> send = new ArrayList<>();
        send.add(newBookId);
        send.add(newBookName);
        send.add(newAuthor);
        send.add(newCountry);
        send.add(newStock);
        send.add(newPublisher);
        send.add(newType);

        try {
            client = new ClientController();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        Message reviseMessage = new Message(Message.MessageType.revise, send);

        // 发送查询消息
        try {
            client.sendMessage(reviseMessage);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }


        // 显示修改成功的信息
        showSuccessDialog(reviseDialog);
        reviseDialog.dispose(); // 关闭对话框
    }

    /**
     * 显示自定义提示对话框的方法。
     *
     * @param message 提示信息。
     */
    public void showDialog(String message) {
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
