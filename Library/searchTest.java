package Server.Library;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import Server.Public.*;
import Server.InfoTable;
import Server.Public.Button;
import Server.SearchBox;


public class searchTest implements ContentPanel {

    private JPanel panel;
    private JComboBox<String> comboBox;
    private SearchBox searchBox;
    private ClientController client;
    private InfoTable table;
    public static List<Serializable> info = new ArrayList<>();
    private static String id_1;


    public searchTest(String id) {
        id_1=id;

        panel = new JPanel(new BorderLayout(10, 10)); // 使用BorderLayout布局，并设置水平和垂直间距
        // 创建顶部面板，用于放置下拉栏、搜索框和按钮
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10)); // FlowLayout布局，左对齐，组件间距10像素
//        panel.add(topPanel, BorderLayout.NORTH); // 将顶部面板放置在主面板的北部

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



        JPanel centrolPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5)); // FlowLayout布局，左对齐，组件间距10像素


        JLabel typeLabel = new JLabel("类型:    ");
        centrolPanel.add(typeLabel);

        String[] bookTypes = {"武侠", "爱情", "悬疑","古典","社会","童话","青春","科幻"};
        for (String type : bookTypes) {
            Button button = new Button(type,Color.WHITE, 30, 70, 30);
            button.addActionListener(e -> typeBorrow(type));
            centrolPanel.add(button);
        }

        JPanel lastPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10)); // FlowLayout布局，左对齐，组件间距10像素
        JLabel professtionLabel = new JLabel("专业课:");
        lastPanel.add(professtionLabel);


        String[] bookTypes1 = {"计算机", "土木", "电子","医学","机械","建筑"};
        for (String type : bookTypes1) {
            Button button = new Button(type,Color.WHITE, 30, 80, 30);
            button.addActionListener(e -> typeBorrow(type));
            lastPanel.add(button);
        }

        JPanel gridPanel = new JPanel(new GridLayout(3, 1, 0, 5)); // 3行，1列，行间距2像素
        gridPanel.add(topPanel);
        gridPanel.add(centrolPanel);
        gridPanel.add(lastPanel);

        panel.add(gridPanel, BorderLayout.NORTH);


        Set<Integer> editableColumns = new HashSet<>();
        table = new InfoTable(0, 9, editableColumns);

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER); // 将表格放在主面板的中部

        // 添加表头
        String[] columnNames = {"封面","编号", "书名", "作者", "国籍", "库存", "出版社", "风格","操作"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0); // 只创建一次
        table.setModel(model);

        table.setRowHeight(100);
        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(50);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(50);
        table.getColumnModel().getColumn(5).setPreferredWidth(50);
        table.getColumnModel().getColumn(6).setPreferredWidth(100);
        table.getColumnModel().getColumn(7).setPreferredWidth(50);
        table.getColumnModel().getColumn(8).setPreferredWidth(50);

        // 为主面板添加边距
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // 四周添加10像素的边距


    }

    @Override
    public JPanel getPanel() {
        return panel;
    }

    private void performSearch() {
        List<Serializable> queryInfo = new ArrayList<>();
        String bookName=searchBox.getText();
        String type = (String) comboBox.getSelectedItem();

        System.out.println(type);
        System.out.println(bookName);

        queryInfo.add(type);
        queryInfo.add(bookName);

        try {
            client=new ClientController();

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
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        info = receivedMessage.getContent();



        int size=info.size();


        if (size==0) {
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(panel);
            if (topFrame instanceof MainWindowStu) {
                ((MainWindowStu) topFrame).switchContent("搜索失败");
            }
        } else {
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0); // 清空现有数据

            table.getColumn("操作").setCellRenderer(new ButtonRenderer());
            table.getColumn("操作").setCellEditor(new ButtonEditor(new JCheckBox()));


            if(size>0){
                for (int i = 0; i < size / 7; i++) {

                    String buttonText = "借阅";
                    Button button = new Button(buttonText, Color.WHITE, 30, 100, 30);


                    String bid = (String) info.get(0 + 7 * i);//获取到当前借阅书籍的uid
                    String nums = (String) info.get(4 + 7 * i);//获取到当前借阅书籍的数量



                    button.addActionListener(e -> performBorrow(bid,nums));


                    //获取封面
                    List<Serializable> queryCover = new ArrayList<>();
                    queryCover.add(bid);

                    try {
                        client=new ClientController();

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Message Cover = new Message(Message.MessageType.getCover, queryCover);

                    try {
                        client.sendMessage(Cover);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Message received = null;
                    try {
                        received = client.receiveMessage();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                    List<Serializable> cover = received.getContent();
                    byte[] imageBytes = (byte[]) cover.get(0);
                    ImageIcon imageIcon = createImageIconFromBytes(imageBytes);

                    Object[] rowData = {
                            imageIcon,
                            info.get(0 + 7 * i),
                            info.get(1 + 7 * i),
                            info.get(2 + 7 * i),
                             info.get(3 + 7 * i),
                             info.get(4 + 7 * i),
                             info.get(5 + 7 * i),
                             info.get(6 + 7 * i),
                            button
                    };
                    model.addRow(rowData);

                }System.out.println("Added " + model.getRowCount() + " rows to the table.");
            } else {
                System.out.println("No data to add to the table.");
            }
            table.revalidate(); // 重新验证组件树
            table.repaint();    // 重新绘制组件}
        }
    }

    private void performBorrow(String bid, String nums) {

        try {
            int num = Integer.parseInt(nums); // 将字符串类型的nums转换为int类型
            if (num == 0) {
                // 书籍数量不足，不可借阅
                JOptionPane.showMessageDialog(null, "书籍数量不足，不可借阅", "提示", JOptionPane.WARNING_MESSAGE);
            }
            else if(personalPanel.getLend()>=5){
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


   //点击类型按钮跳转到书籍界面
    private void typeBorrow(String type) {
        List<Serializable> queryInfo = new ArrayList<>();
        queryInfo.add("类型");
        queryInfo.add(type);
        try {
            client=new ClientController();

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
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        info = receivedMessage.getContent();



        int size=info.size();
        System.out.println(size);

        if (size==0) {
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(panel);
            if (topFrame instanceof MainWindowStu) {
                ((MainWindowStu) topFrame).switchContent("搜索失败");
            }
        } else {
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0); // 清空现有数据

            table.getColumn("操作").setCellRenderer(new ButtonRenderer());
            table.getColumn("操作").setCellEditor(new ButtonEditor(new JCheckBox()));


            if(size>0){
                for (int i = 0; i < size / 7; i++) {

                    String buttonText = "借阅";
                    Button button = new Button(buttonText, Color.WHITE, 30, 100, 30);


                    String bid = (String) info.get(0 + 7 * i);//获取到当前借阅书籍的uid
                    String nums = (String) info.get(4 + 7 * i);//获取到当前借阅书籍的数量

                    button.addActionListener(e -> performBorrow(bid,nums));

                    table.getColumnModel().getColumn(0).setCellRenderer(new PaddedImageRenderer());


                    //获取封面
                    List<Serializable> queryCover = new ArrayList<>();
                    queryCover.add(bid);

                    try {
                        client=new ClientController();

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Message Cover = new Message(Message.MessageType.getCover, queryCover);

                    // 发送查询消息
                    try {
                        client.sendMessage(Cover);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Message received = null;
                    try {
                        received = client.receiveMessage();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                    List<Serializable> cover = received.getContent();
                    byte[] imageBytes = (byte[]) cover.get(0);
                    ImageIcon imageIcon = createImageIconFromBytes(imageBytes);
                    Object[] rowData = {
                            imageIcon,
                            info.get(0 + 7 * i),
                            info.get(1 + 7 * i),
                            info.get(2 + 7 * i),
                            info.get(3 + 7 * i),
                            info.get(4 + 7 * i),
                            info.get(5 + 7 * i),
                            info.get(6 + 7 * i),
                            button
                    };
                    model.addRow(rowData);

                }System.out.println("Added " + model.getRowCount() + " rows to the table.");
            } else {
                System.out.println("No data to add to the table.");
            }
            table.revalidate(); // 重新验证组件树
            table.repaint();    // 重新绘制组件}
        }
    }

        //弹出成功的窗口
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

    private static ImageIcon createImageIconFromBytes(byte[] imageBytes) {
        System.out.println("tushuguan" + imageBytes);
        if (imageBytes != null && imageBytes.length > 0) {
            if (imageBytes != null && imageBytes.length >= 4) {
                // 打印前四个字节的十六进制值
                System.out.printf("Header bytes: %02X %02X %02X %02X%n",
                        imageBytes[0], imageBytes[1], imageBytes[2], imageBytes[3]);
            } else {
                System.out.println("Image bytes are too short to determine header.");
            }
            try {
                BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageBytes));
                if (img == null) {
                    System.out.println("Failed to decode image: ImageIO.read returned null. Please check the image format or data integrity.");
                    return null;
                }
                return new ImageIcon(img);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Image bytes are null or empty.");
        }
        return null;
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
// 自定义渲染器类，用于在表格单元格中显示图片
class PaddedImageRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        JLabel label = new JLabel();
        label.setHorizontalAlignment(JLabel.CENTER); // 居中对齐
        if (value instanceof ImageIcon) {
            ImageIcon icon = (ImageIcon) value;

            // 调整图像大小，使其适应表格单元格并保留一定的空隙
            Image image = icon.getImage();
            int labelWidth = table.getColumnModel().getColumn(column).getWidth() - 20; // 预留空隙
            int labelHeight = table.getRowHeight(row) - 20; // 预留空隙
            Image scaledImage = image.getScaledInstance(labelWidth, labelHeight, Image.SCALE_SMOOTH);
            icon = new ImageIcon(scaledImage);

            label.setIcon(icon);
            label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // 设置边距
        }
        return label;
    }
}