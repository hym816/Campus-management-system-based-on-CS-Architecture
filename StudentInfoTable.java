package Server;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import Server.Public.Message;
import Server.Public.Message.MessageType;
import Server.Public.ClientController;

public class StudentInfoTable extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> gradeComboBox;
    private JComboBox<String> collegeComboBox;
    private JComboBox<String> classComboBox;
    private JButton resetButton;
    private SearchBox searchBox;
    private List<Serializable> studentIds;
    private Object[][] data;  // 原始数据保存
    private JPanel detailPanel;  // 显示详细信息的面板
    private JTextField[] detailFields;  // 用于编辑学生详细信息的文本框数组
    private JLabel avatarLabel; // 显示头像的标签
    private JButton toggleButton; // 收起/展开按钮

    private ClientController client;

    private byte[] avatarData;
    public StudentInfoTable() {
        // 初始化组件
        setLayout(new BorderLayout());
        initializeClient();
        loadDataFromServer();  // 从服务器加载数据

        // 初始化表格
        String[] columnNames = {"年级", "ID", "专业", "班级", "姓名"};
        tableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 表格不可编辑
            }
        };
        table = new JTable(tableModel);
        customizeTableAppearance();

        // 初始化下拉菜单
        gradeComboBox = createComboBox(0); // 根据第1列数据初始化下拉菜单
        collegeComboBox = createComboBox(2);   // 根据第3列数据初始化下拉菜单
        classComboBox = createComboBox(3);     // 根据第4列数据初始化下拉菜单

        // 初始化搜索框
        searchBox = new SearchBox();
        searchBox.addActionListener(e -> searchTable());

        // 初始化重置按钮
        resetButton = new JButton("重置");
        resetButton.addActionListener(e -> resetFilters());

        // 为下拉菜单添加监听器
        gradeComboBox.addActionListener(e -> filterTable());
        collegeComboBox.addActionListener(e -> filterTable());
        classComboBox.addActionListener(e -> filterTable());

        // 创建筛选面板
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); // 使用FlowLayout居左
        filterPanel.add(new JLabel("年级:"));
        filterPanel.add(gradeComboBox);
        filterPanel.add(new JLabel("学院:"));
        filterPanel.add(collegeComboBox);
        filterPanel.add(new JLabel("班级:"));
        filterPanel.add(classComboBox);
        filterPanel.add(searchBox); // 添加搜索框
        filterPanel.add(resetButton);

        // 添加筛选面板和表格到面板
        add(filterPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // 初始化详情面板
        detailPanel = new JPanel(new BorderLayout());
        detailPanel.setBorder(BorderFactory.createTitledBorder("学生详细信息"));
        add(detailPanel, BorderLayout.SOUTH);

        // 初始化头像标签
        avatarLabel = new JLabel();
        avatarLabel.setPreferredSize(new Dimension(150, 150)); // 设置头像大小
        detailPanel.add(avatarLabel, BorderLayout.WEST);

        // 右侧详细信息面板
        JPanel infoPanel = new JPanel(new GridLayout(0, 2));
        detailPanel.add(infoPanel, BorderLayout.CENTER);

        // 初始化文本框数组用于详细信息编辑
        detailFields = new JTextField[13]; // 假设有13个字段需要编辑

        // 添加收起/展开按钮
        toggleButton = new JButton("收起");
        toggleButton.addActionListener(e -> toggleDetailPanel());
        detailPanel.add(toggleButton, BorderLayout.NORTH); // 将按钮添加到详细信息面板的顶部

        // 设置双击事件，展示详细信息
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // 双击检测
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow != -1) {
                        String studentId = table.getValueAt(selectedRow, 1).toString(); // 获取选中行的学生ID
                        showStudentDetails(studentId, infoPanel); // 使用学生ID查询详细信息
                    }
                }
            }
        });
    }

    // 初始化客户端连接
    private void initializeClient() {
        try {
            client = new ClientController();
        } catch (IOException e) {
            throw new RuntimeException("初始化客户端时发生错误", e);
        }
    }

    // 从服务器加载数据
    private void loadDataFromServer() {
        List<List<Serializable>> stuInfoList = new ArrayList<>();

        try {
            // 获取学生 ID 列表
            Message requestMessage = new Message(MessageType.everyone_id);
            client.sendMessage(requestMessage);
            Message responseMessage = client.receiveMessage();

            if (responseMessage != null && responseMessage.getType() == MessageType.everyone_id) {
                studentIds = responseMessage.getContent();

                // 遍历每个学生 ID，获取详细信息
                for (Serializable studentId : studentIds) {
                    List<Serializable> queryInfo = new ArrayList<>();
                    queryInfo.add(studentId);
                    Message queryMessage = new Message(MessageType.student_info_query, queryInfo);
                    client.sendMessage(queryMessage);
                    Message receivedMessage = client.receiveMessage();

                    if (receivedMessage != null && receivedMessage.getType() == MessageType.student_info_query) {
                        List<Serializable> receivedList = receivedMessage.getContent();
                        stuInfoList.add(receivedList); // 添加到学生信息列表
                    }
                }
            } else {
                System.out.println("获取学生 ID 失败或无响应");
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        // 将学生信息列表转换为二维数组
        data = new Object[stuInfoList.size()][5];
        for (int i = 0; i < stuInfoList.size(); i++) {
            List<Serializable> receivedList = stuInfoList.get(i);
            // 将 receivedList 的 1, 4, 5, 7, 8 元素映射到表格的 5 列
            data[i][0] = receivedList.get(4); // 对应表格的第一列
            data[i][1] = studentIds.get(i); // 对应表格的第二列
            data[i][2] = receivedList.get(5); // 对应表格的第三列
            data[i][3] = receivedList.get(6); // 对应表格的第四列
            data[i][4] = receivedList.get(0); // 对应表格的第五列
        }
    }

    // 自定义表格外观
    private void customizeTableAppearance() {
        table.setRowHeight(30); // 设置行高

        // 设置字体和颜色
        table.setFont(new Font("宋体", Font.PLAIN, 16));
        table.getTableHeader().setFont(new Font("宋体", Font.BOLD, 16));

        // 自定义渲染器
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    if (row % 2 == 0) {
                        c.setBackground(Color.WHITE); // 白色
                    } else {
                        c.setBackground(new Color(240, 240, 240)); // 浅灰色
                    }
                }
                return c;
            }
        };
        table.setDefaultRenderer(Object.class, renderer);
    }

    // 创建下拉菜单
    private JComboBox<String> createComboBox(int columnIndex) {
        Set<String> uniqueValues = new HashSet<>();
        for (Object[] row : data) {
            uniqueValues.add(row[columnIndex].toString());
        }
        ArrayList<String> sortedValues = new ArrayList<>(uniqueValues);
        sortedValues.sort(String::compareTo); // 排序
        sortedValues.add(0, "全部"); // 添加默认选项
        return new JComboBox<>(sortedValues.toArray(new String[0]));
    }

    // 筛选表格内容
    private void filterTable() {
        String grade = (String) gradeComboBox.getSelectedItem();
        String college = (String) collegeComboBox.getSelectedItem();
        String className = (String) classComboBox.getSelectedItem();

        tableModel.setRowCount(0); // 清空表格内容
        for (Object[] row : data) {
            if ((grade.equals("全部") || row[0].equals(grade)) &&
                    (college.equals("全部") || row[2].equals(college)) &&
                    (className.equals("全部") || row[3].equals(className))) {
                tableModel.addRow(row);
            }
        }
    }

    // 搜索表格内容
    private void searchTable() {
        String searchText = searchBox.getText().trim().toLowerCase();

        tableModel.setRowCount(0); // 清空表格内容
        for (Object[] row : data) {
            for (Object cell : row) {
                if (cell.toString().toLowerCase().contains(searchText)) {
                    tableModel.addRow(row);
                    break; // 找到匹配项后停止检查本行
                }
            }
        }
    }

    // 重置筛选
    private void resetFilters() {
        gradeComboBox.setSelectedIndex(0);
        collegeComboBox.setSelectedIndex(0);
        classComboBox.setSelectedIndex(0);
        searchBox.setText("");
        filterTable(); // 显示所有数据
    }

    // 显示学生详细信息方法
    private void showStudentDetails(String studentId, JPanel infoPanel) {
        // 清空之前的信息
        infoPanel.removeAll();

        // 创建请求消息，查询详细信息
        List<Serializable> queryInfo = new ArrayList<>();
        queryInfo.add(studentId); // 确保学生ID是String类型
        Message queryMessage = new Message(MessageType.student_info_query, queryInfo);

        List<Serializable> studentDetails = new ArrayList<>();
        try {
            client.sendMessage(queryMessage); // 向服务器发送查询请求
            Message receivedMessage = client.receiveMessage(); // 接收服务器的响应
            if (receivedMessage != null && receivedMessage.getType() == MessageType.student_info_query) {
                studentDetails = receivedMessage.getContent(); // 获取详细信息内容
            } else {
                JOptionPane.showMessageDialog(this, "无法获取详细信息", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "查询失败，请重试", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 动态生成文本框并添加到详细信息面板
        String[] labels = {"ID", "姓名", "性别", "出生日期", "身份证号", "入学时间", "学院", "班级", "状态",
                "Email", "电话号码", "家庭住址"};

        // 填充ID字段
        infoPanel.add(new JLabel(labels[0] + ":"));
        detailFields[0] = new JTextField(studentId); // ID直接使用表中的值
        infoPanel.add(detailFields[0]);

        // 填充其余字段
        for (int i = 1; i < labels.length; i++) {
            infoPanel.add(new JLabel(labels[i] + ":"));
            String value = (i - 1 < studentDetails.size() && studentDetails.get(i - 1) != null)
                    ? studentDetails.get(i - 1).toString()
                    : ""; // 设置默认值为空字符串以防止NullPointerException
            detailFields[i] = new JTextField(value);
            infoPanel.add(detailFields[i]);
        }

        // 显示头像，如果有的话
        if (studentDetails.size() > 11 && studentDetails.get(11) instanceof byte[]) {
            avatarData = (byte[]) studentDetails.get(11); // 获取头像二进制数据
            ImageIcon avatarIcon = null; // 从字节数组加载图标
            try {
                avatarIcon = loadIconFromBytes(avatarData, 150, 150);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            avatarLabel.setIcon(avatarIcon);
        } else {
            System.out.println("未找到有效的头像数据");
        }

        // 添加保存按钮
        JButton saveButton = new JButton("保存");
        saveButton.addActionListener(e -> saveStudentDetails(studentId));
        infoPanel.add(saveButton);

        // 添加学籍变动按钮
        JButton addEnrollmentChangeButton = new JButton("学籍变动");
        addEnrollmentChangeButton.addActionListener(e -> new EnrollmentChangeDialog());
        infoPanel.add(addEnrollmentChangeButton);

        // 添加奖惩记录按钮
        JButton addRewardPunishmentButton = new JButton("奖惩记录");
        addRewardPunishmentButton.addActionListener(e -> new RewardPunishmentDialog());
        infoPanel.add(addRewardPunishmentButton);

        detailPanel.setVisible(true); // 确保详细信息面板可见
        toggleButton.setText("收起"); // 确保按钮显示为“收起”
        infoPanel.revalidate();
        infoPanel.repaint();
    }

    // 从字节数组加载图标
    private ImageIcon loadIconFromBytes(byte[] imageBytes, int width, int height) throws IOException {
        if (imageBytes == null) {
            return null;
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
        ImageIcon icon = new ImageIcon(bais.readAllBytes());
        Image image = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(image);
    }

    // 切换详细信息面板的可见状态
    private void toggleDetailPanel() {
        if (detailPanel.isVisible()) {
            detailPanel.setVisible(false);
            toggleButton.setText("展开");
        } else {
            detailPanel.setVisible(true);
            toggleButton.setText("收起");
        }
    }

    // 保存学生详细信息方法
    private void saveStudentDetails(String id) {
        // 创建一个列表来存储更新的信息
        List<Serializable> updateInfo = new ArrayList<>();

        updateInfo.add(id);

        // 按顺序读取除ID外的所有文本框中的信息
        for (int i = 1; i < 12; i++) { // 从索引1开始，跳过ID
            String value = detailFields[i].getText(); // 获取每个文本框中的值
            updateInfo.add(value); // 将值添加到更新信息列表
        }

        updateInfo.add(avatarData);

        // 打印输出验证信息
        System.out.println("更新的信息: " + updateInfo);

        // 创建更新学生信息的消息
        Message updateMessage = new Message(Message.MessageType.student_info_update, updateInfo);

        // 发送消息到服务器
        try {
            client.sendMessage(updateMessage);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        // 接收服务器的响应
        Message response = null;
        try {
            response = client.receiveMessage();
        } catch (IOException | ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
        if (response != null) {
            JOptionPane.showMessageDialog(this, "学生信息已保存！", "保存成功", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}





// 学籍变动输入对话框类
class EnrollmentChangeDialog extends JDialog {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField studentIdField;
    private JComboBox<String> changeTypeComboBox;
    private JTextField dateField;
    private JTextField newStateField;

    public EnrollmentChangeDialog() {
        setTitle("学籍变动");
        setSize(600, 400);
        setLayout(new BorderLayout());

        // 初始化表格
        String[] columnNames = {"编号", "变动类型", "变动日期", "原来状态", "现在状态"};
        Object[][] data = {}; // 初始化空数据，可以通过查询数据库填充
        tableModel = new DefaultTableModel(data, columnNames);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // 下侧面板：添加学籍变动
        JPanel inputPanel = new JPanel(new GridLayout(5, 2));
        inputPanel.setBorder(BorderFactory.createTitledBorder("添加学籍变动"));

        inputPanel.add(new JLabel("学生ID:"));
        studentIdField = new JTextField();
        inputPanel.add(studentIdField);

        inputPanel.add(new JLabel("变动类型:"));
        changeTypeComboBox = new JComboBox<>(new String[]{"Program Change", "Class Change", "Suspension", "Reinstatement", "Withdrawal"});
        inputPanel.add(changeTypeComboBox);

        inputPanel.add(new JLabel("变动日期:"));
        dateField = new JTextField();
        inputPanel.add(dateField);

        inputPanel.add(new JLabel("新的状态:"));
        newStateField = new JTextField();
        inputPanel.add(newStateField);

        JButton saveButton = new JButton("保存");
        saveButton.addActionListener(e -> saveEnrollmentChange());
        inputPanel.add(saveButton);

        // 添加删除按钮
        JButton deleteButton = new JButton("删除");
        deleteButton.addActionListener(e -> deleteSelectedEnrollmentChange());
        inputPanel.add(deleteButton);

        add(inputPanel, BorderLayout.SOUTH);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void saveEnrollmentChange() {
        // 保存学籍变动逻辑
        String studentId = studentIdField.getText();
        String changeType = (String) changeTypeComboBox.getSelectedItem();
        String date = dateField.getText();
        String newState = newStateField.getText();

        // 添加到表格
        tableModel.addRow(new Object[]{tableModel.getRowCount() + 1, changeType, date, "原状态", newState});
        JOptionPane.showMessageDialog(this, "学籍变动已保存！", "保存成功", JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteSelectedEnrollmentChange() {
        // 删除选中的学籍变动记录
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            tableModel.removeRow(selectedRow);
            JOptionPane.showMessageDialog(this, "选中的学籍变动已删除！", "删除成功", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "请先选择一条记录进行删除！", "删除失败", JOptionPane.WARNING_MESSAGE);
        }
    }
}

// 奖惩记录输入对话框类
class RewardPunishmentDialog extends JDialog {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField studentIdField;
    private JComboBox<String> typeComboBox;
    private JTextField dateField;
    private JTextField descriptionField;

    public RewardPunishmentDialog() {
        setTitle("奖惩记录");
        setSize(600, 400);
        setLayout(new BorderLayout());

        // 初始化表格
        String[] columnNames = {"编号", "类型", "描述", "变动日期"};
        Object[][] data = {}; // 初始化空数据，可以通过查询数据库填充
        tableModel = new DefaultTableModel(data, columnNames);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // 下侧面板：添加奖惩记录
        JPanel inputPanel = new JPanel(new GridLayout(5, 2));
        inputPanel.setBorder(BorderFactory.createTitledBorder("添加奖惩记录"));

        inputPanel.add(new JLabel("学生ID:"));
        studentIdField = new JTextField();
        inputPanel.add(studentIdField);

        inputPanel.add(new JLabel("类型:"));
        typeComboBox = new JComboBox<>(new String[]{"奖励", "惩罚"});
        inputPanel.add(typeComboBox);

        inputPanel.add(new JLabel("变动日期:"));
        dateField = new JTextField();
        inputPanel.add(dateField);

        inputPanel.add(new JLabel("描述:"));
        descriptionField = new JTextField();
        inputPanel.add(descriptionField);

        JButton saveButton = new JButton("保存");
        saveButton.addActionListener(e -> saveRewardPunishment());
        inputPanel.add(saveButton);

        // 添加删除按钮
        JButton deleteButton = new JButton("删除");
        deleteButton.addActionListener(e -> deleteSelectedRewardPunishment());
        inputPanel.add(deleteButton);

        add(inputPanel, BorderLayout.SOUTH);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void saveRewardPunishment() {
        // 保存奖惩记录逻辑
        String studentId = studentIdField.getText();
        String type = (String) typeComboBox.getSelectedItem();
        String date = dateField.getText();
        String description = descriptionField.getText();

        // 添加到表格
        tableModel.addRow(new Object[]{tableModel.getRowCount() + 1, type, description, date});
        JOptionPane.showMessageDialog(this, "奖惩记录已保存！", "保存成功", JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteSelectedRewardPunishment() {
        // 删除选中的奖惩记录
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            tableModel.removeRow(selectedRow);
            JOptionPane.showMessageDialog(this, "选中的奖惩记录已删除！", "删除成功", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "请先选择一条记录进行删除！", "删除失败", JOptionPane.WARNING_MESSAGE);
        }
    }
}
