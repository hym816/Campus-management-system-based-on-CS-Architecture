package Server;

import Server.Public.ClientController;
import Server.Public.Message;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import java.awt.Button;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class PersonalInfoTable {

    private JTable table;
    public static DefaultTableModel tableModel;
    private boolean isEditable = false;
    private java.awt.Button editButton;
    private JPanel tablePanel;
    private JScrollPane scrollPane;
    public static ClientController client; // 用于与服务器通信的 Client 对象
    public static byte[] avatar;
    public static String name;
    // 构造函数，传入学生ID和编辑权限
    public PersonalInfoTable(String studentId, boolean canEdit) {
        // 初始化面板和布局
        tablePanel = new JPanel(new BorderLayout());
        System.out.println(MainWindow.stuid);
        // 创建表格模型
        tableModel = new DefaultTableModel(new String[]{"名称", "内容"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                if (column == 1 && isEditable) {
                    String fieldName = (String) getValueAt(row, 0);
                    // 仅允许特定字段可编辑
                    return fieldName.equals("姓名") || fieldName.equals("性别") ||
                            fieldName.equals("出生日期") || fieldName.equals("Email") ||
                            fieldName.equals("电话号码") || fieldName.equals("家庭住址");
                }
                return false;
            }
        };

        // 创建表格
        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(30);

        // 设置单元格编辑器并添加验证逻辑
        DefaultCellEditor editor = new DefaultCellEditor(new JTextField()) {
            @Override
            public boolean stopCellEditing() {
                String value = (String) getCellEditorValue();
                int row = table.getEditingRow();
                if (!validateInput(row, value)) {
                    return false; // 验证失败，不停止编辑
                }
                return super.stopCellEditing(); // 验证通过，停止编辑
            }
        };
        table.setDefaultEditor(Object.class, editor);

        // 添加单元格编辑停止时的监听器来验证输入
        editor.addCellEditorListener(new CellEditorListener() {
            @Override
            public void editingStopped(ChangeEvent e) {
                // 已在 stopCellEditing 中处理，无需再验证
            }

            @Override
            public void editingCanceled(ChangeEvent e) {
                // 可选处理编辑取消事件
            }
        });

        // 自定义渲染器以实现交替行颜色和选中行圆角矩形效果
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (row % 2 == 0) {
                    cell.setBackground(Color.WHITE);
                } else {
                    cell.setBackground(new Color(230, 230, 230));
                }

                if (isSelected) {
                    cell.setBackground(new Color(0, 120, 215)); // 蓝色背景
                    cell.setForeground(Color.WHITE); // 白色文字
                    setBorder(BorderFactory.createLineBorder(new Color(0, 120, 215), 2, true)); // 圆角矩形边框
                } else {
                    cell.setForeground(Color.BLACK);
                }
                return cell;
            }
        });

        // 创建滚动面板
        scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);

        // 添加鼠标悬停提示
        table.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row > -1) {

                    table.setToolTipText(table.getValueAt(row, 1).toString());
                }
            }
        });

        // 创建编辑按钮
        editButton = new Button("编辑");
        editButton.setEnabled(canEdit); // 根据传入的参数控制按钮的启用状态
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isEditable) {
                    // 保存逻辑
                    List<Serializable> updatedInfo = new ArrayList<>();
                    updatedInfo.add((String) table.getValueAt(0, 1));  // 学号 (Primary Key)
                    updatedInfo.add((String) table.getValueAt(1, 1));  // 新的姓名
                    updatedInfo.add((String) table.getValueAt(2, 1));  // 新的性别
                    updatedInfo.add((String) table.getValueAt(3, 1));  // 新的出生日期
                    updatedInfo.add((String) table.getValueAt(4, 1));  // 新的身份证号
                    updatedInfo.add((String) table.getValueAt(5, 1));  // 新的入学日期
                    updatedInfo.add((String) table.getValueAt(6, 1));  // 新的专业
                    updatedInfo.add((String) table.getValueAt(7, 1));  // 新的班级
                    updatedInfo.add((String) table.getValueAt(8, 1));  // 新的状态
                    updatedInfo.add((String) table.getValueAt(9, 1));  // 新的邮箱
                    updatedInfo.add((String) table.getValueAt(10, 1)); // 新的电话号码
                    updatedInfo.add((String) table.getValueAt(11, 1)); // 新的地址
                    updatedInfo.add(avatar);                            // 新的头像（可以传递字节数组）

                    // 创建更新学生信息的消息
                    Message updateMessage = new Message(Message.MessageType.student_info_update, updatedInfo);

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
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    } catch (ClassNotFoundException ex) {
                        throw new RuntimeException(ex);
                    }
                    if (response != null) {
                        JOptionPane.showMessageDialog(tablePanel, "保存成功！", "提示", JOptionPane.INFORMATION_MESSAGE);                    }

                }

                isEditable = !isEditable;
                editButton.setLabel(isEditable ? "保存" : "编辑");
                tableModel.fireTableDataChanged(); // 更新表格
            }
        });

        // 从服务器获取数据并填充表格
        fetchDataAndFillTable(studentId);

        // 将表格和按钮添加到主面板
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        tablePanel.add(editButton, BorderLayout.SOUTH);
    }

    // 获取包含表格的 JPanel
    public JPanel getTablePanel() {
        return tablePanel;
    }

    // 从服务器获取数据并填充表格的方法
    public static void fetchDataAndFillTable(String studentId) {
        if (studentId == null) return;
        try {
            client = new ClientController(); // 初始化 Client 对象
            List<Serializable> queryInfo = new ArrayList<>();
            queryInfo.add(studentId);
            Message queryMessage = new Message(Message.MessageType.student_info_query, queryInfo);

            // 发送查询消息
            client.sendMessage(queryMessage);

            // 接收服务器响应
            Message receivedMessage = client.receiveMessage();
            List<Serializable> info = receivedMessage.getContent();

            // 填充表格数据
            Object[][] tableData = {
                    {"ID", studentId},
                    {"姓名", info.get(0)},
                    {"性别", info.get(1)},
                    {"出生日期", info.get(2)},
                    {"身份证号", info.get(3)},
                    {"入学时间", info.get(4)},
                    {"专业", info.get(5)},
                    {"班级", info.get(6)},
                    {"状态", info.get(7)},
                    {"Email", info.get(8)},
                    {"电话号码", info.get(9)},
                    {"家庭住址", info.get(10)},
            };


            avatar = (byte[]) info.get(11);

            // 使用 tableModel 填充数据
            for (Object[] row : tableData) {
                tableModel.addRow(row);
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // 验证输入的方法
    private boolean validateInput(int row, String value) {
        String fieldName = (String) table.getValueAt(row, 0);
        if ("性别".equals(fieldName) && !value.matches("M|F")) {
            showErrorDialog("性别必须为 M 或 F，请重新输入！");
            return false; // 验证失败
        } else if ("出生日期".equals(fieldName) && !isValidDate(value)) {
            showErrorDialog("出生日期格式必须为 yyyy-MM-dd，请重新输入！");
            return false; // 验证失败
        }
        return true; // 验证通过
    }

    // 检查日期格式的方法
    private boolean isValidDate(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        try {
            sdf.parse(dateStr);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    // 显示错误对话框的方法
    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(tablePanel, message, "输入错误", JOptionPane.ERROR_MESSAGE);
    }

    // 主函数，用于测试
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Editable Table with Input Validation");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(500, 400);
            frame.setLocationRelativeTo(null);

            // 创建并显示表格，直接传递 studentId
            PersonalInfoTable editableTable = new PersonalInfoTable("09022118", true);
            frame.add(editableTable.getTablePanel());

            frame.setVisible(true);
        });
    }
}
