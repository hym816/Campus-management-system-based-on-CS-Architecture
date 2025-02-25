package Server.Library;

import Server.Public.ClientController;
import Server.Public.ContentPanel;
import Server.Public.Message;
import Server.Public.textField;
import Server.Public.Button;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class addBook implements ContentPanel {
    private JPanel panel;
    private static ClientController client;
    private static byte[] imageBytes;
    JFrame frame = new JFrame("上传图片示例");
    public addBook() {
        // 设置面板的布局
        panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // 创建自定义文本框
        textField bookIdField = new textField(20);
        textField bookNameField = new textField(20);
        textField authorField = new textField(20);
        textField countryField = new textField(20);
        textField stockField = new textField(20);
        textField publisherField = new textField(20);
        textField typeField = new textField(20);

        // 添加组件到面板
        panel.add(new JLabel("书籍编号："), gbc);
        gbc.gridx = 1;
        panel.add(bookIdField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("书名："), gbc);
        gbc.gridx = 1;
        panel.add(bookNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("作者："), gbc);
        gbc.gridx = 1;
        panel.add(authorField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("国家："), gbc);
        gbc.gridx = 1;
        panel.add(countryField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("库存："), gbc);
        gbc.gridx = 1;
        panel.add(stockField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("出版社："), gbc);
        gbc.gridx = 1;
        panel.add(publisherField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(new JLabel("类型："), gbc);
        gbc.gridx = 1;
        panel.add(typeField, gbc);



        // 上传图片按钮
        Button picture = new Button("上传图片");
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 1;
        panel.add(new JLabel("图片："), gbc);
        gbc.gridx = 1;
        panel.add(picture, gbc);


        picture.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {addCover(frame);
            }
        });


        // 确定按钮
        Button confirmButton = new Button("确定");
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(confirmButton, gbc);

        // 确定按钮的事件处理
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addBook(bookIdField, bookNameField, authorField, countryField, stockField, publisherField, typeField);
            }
        });
    }

    private static void addBook(textField bookIdField, textField bookNameField, textField authorField,
                                textField countryField, textField stockField, textField publisherField, textField typeField) {

        System.out.println("尝试获取图片"+imageBytes);
        // 检查所有文本框是否有输入
        if (bookIdField.getText().trim().isEmpty() ||
                bookNameField.getText().trim().isEmpty() ||
                authorField.getText().trim().isEmpty() ||
                countryField.getText().trim().isEmpty() ||
                stockField.getText().trim().isEmpty() ||
                publisherField.getText().trim().isEmpty() ||
                typeField.getText().trim().isEmpty()||(imageBytes==null)) {

            // 弹出警告对话框
            showWarningDialog("信息未填写完整");
        } else {
            String bookId = bookIdField.getText().trim();
            String bookName = bookNameField.getText().trim();
            String author = authorField.getText().trim();
            String country = countryField.getText().trim();
            String stock = stockField.getText().trim();
            String publisher = publisherField.getText().trim();
            String type = typeField.getText().trim();

            List<Serializable> send = new ArrayList<>();
            send.add(bookId);
            send.add(bookName);
            send.add(author);
            send.add(country);
            send.add(stock);
            send.add(publisher);
            send.add(type);
            send.add(imageBytes);

            try {
                client = new ClientController();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            Message queryMessage = new Message(Message.MessageType.add, send);

            // 发送查询消息
            try {
                client.sendMessage(queryMessage);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // 弹出成功添加书籍的对话框
            showSuccessDialog("书籍添加成功！");

            // 清空所有文本框内容
            bookIdField.setText("");
            bookNameField.setText("");
            authorField.setText("");
            countryField.setText("");
            stockField.setText("");
            publisherField.setText("");
            typeField.setText("");

            // 示例：处理成功后的逻辑
            System.out.println("所有信息已填写完毕！");
            // 在这里添加处理逻辑，例如保存数据到数据库
        }
    }

    // 显示自定义警告对话框
    private static void showWarningDialog(String message) {
        JDialog warningDialog = new JDialog((Frame) null, "警告", true);
        warningDialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 添加警告提示标签
        gbc.gridx = 0;
        gbc.gridy = 0;
        warningDialog.add(new JLabel(message), gbc);

        // 创建自定义确定按钮
        Button okButton = new Button("确定", Color.WHITE, 30, 100, 30);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        warningDialog.add(okButton, gbc);

        // 确定按钮的事件处理，点击后关闭警告对话框
        okButton.addActionListener(evt -> warningDialog.dispose());

        // 设置对话框的大小和可见性
        warningDialog.pack();
        warningDialog.setLocationRelativeTo(null);
        warningDialog.setVisible(true);
    }

    // 显示自定义成功对话框
    private static void showSuccessDialog(String message) {
        JDialog successDialog = new JDialog((Frame) null, "成功", true);
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


    private static void addCover(Frame frame) {
        FileDialog fileDialog = new FileDialog(frame, "选择图片", FileDialog.LOAD);
        // 设置文件过滤器，只允许选择图片文件
        fileDialog.setFilenameFilter(new java.io.FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                String filename = name.toLowerCase();
                return filename.endsWith(".png") || filename.endsWith(".jpg") || filename.endsWith(".jpeg") || filename.endsWith(".gif");
            }
        });

        fileDialog.setVisible(true);
        String directory = fileDialog.getDirectory();
        String filename = fileDialog.getFile();

        if (filename != null) {
            // 获取选中文件的完整路径
            String filePath = directory + filename;
            File selectedFile = new File(filePath);

            try {
                // 将文件读取为字节数组并存储到全局变量
                imageBytes = Files.readAllBytes(selectedFile.toPath());
                System.out.println("获取到的字节数组长度: " + imageBytes.length);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("读取文件时发生错误！");
            }
        }
    }

    @Override
    public JPanel getPanel() {
        return panel;
    }
}
