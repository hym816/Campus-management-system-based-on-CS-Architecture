package Server.Store;

import Server.Public.ClientController;
import Server.Public.ContentPanel;
import Server.Public.Message;
import Server.Public.Product;
import Server.SearchBox;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class ShopManageUI implements ContentPanel {

    private JPanel panel;
    private JTextField addNameField;
    private JTextField addPriceField;
    private JTextField addQuantityField;
    private JTextField deleteIdField;
    private JTextField deleteNameField;
    private JTextField restockIdField;
    private JTextField restockNameField;
    private JTextField restockQuantityField;
    private JButton button1;
    private JButton button2;
    private JButton button3;
    private JButton refreshButton;
    private JTable productTable;
    private DefaultTableModel tableModel;

    private List<Product> parsedProducts;
    private JButton selectImageButton;
    private JLabel imagePreviewLabel;
    private byte[] selectedImageBytes;

    public ShopManageUI() throws IOException, ClassNotFoundException {
        // 初始化主面板
        panel = new JPanel(new BorderLayout());

        // 表格模型和表格
        String[] columnNames = {"编号", "名字", "价格", "数量"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        productTable = new JTable(tableModel);

        // 左侧的表格面板
        JPanel leftPanel = new JPanel(new BorderLayout());
        JScrollPane tableScrollPane = new JScrollPane(productTable);
        tableScrollPane.setPreferredSize(new Dimension(300, 500));

        SearchBox searchBox = new SearchBox();


        productTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) { // 避免重复触发事件
                int selectedRow = productTable.getSelectedRow();
                if (selectedRow != -1) {
                    // 获取模型中的行索引（由于排序，可能与视图中的行索引不同）
                    int modelRow = productTable.convertRowIndexToModel(selectedRow);

                    // 从选中的行中获取数据
                    Object productId = tableModel.getValueAt(modelRow, 0);
                    Object productName = tableModel.getValueAt(modelRow, 1);

                    // 将数据显示在对应的文本字段中
                    deleteIdField.setText(productId.toString());
                    deleteNameField.setText(productName.toString());
                    restockIdField.setText(productId.toString());
                    restockNameField.setText(productName.toString());
                }
            }
        });




        ClientController clientController = new ClientController();

        // 创建刷新按钮
        refreshButton = new JButton("刷新");

        refreshButton.addActionListener(e -> {
            try {
                clientController.sendMessage(new Message(Message.MessageType.all_product));
                Message response = clientController.receiveMessage();
                List<Serializable> temp = response.getContent();
                parsedProducts = new ArrayList<>();

                if (temp != null && !temp.isEmpty() && temp.get(0) instanceof List<?>) {
                    List<?> tempList = (List<?>) temp.get(0);
                    if (!tempList.isEmpty() && tempList.get(0) instanceof Product) {
                        parsedProducts = (List<Product>) tempList;
                    }
                }
                displayProducts(parsedProducts);
            } catch (IOException | ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        });

        refreshButton.doClick();
        leftPanel.add(searchBox, BorderLayout.NORTH);
        leftPanel.add(tableScrollPane, BorderLayout.CENTER);
        leftPanel.add(refreshButton, BorderLayout.SOUTH);
        searchBox.addSearchActionListener(e -> filterTable(searchBox.getText().trim()));

        // 右侧的操作面板，使用BoxLayout进行垂直布局
        JPanel operationPanel = new JPanel();
        operationPanel.setLayout(new BoxLayout(operationPanel, BoxLayout.Y_AXIS));
        operationPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 添加商品模块
        operationPanel.add(createAddProductPanel());
        operationPanel.add(Box.createVerticalStrut(20)); // 添加空白间隔

        // 删除商品模块
        operationPanel.add(createDeleteProductPanel());
        operationPanel.add(Box.createVerticalStrut(20)); // 添加空白间隔

        // 补货模块
        operationPanel.add(createRestockProductPanel());

        // 使用JSplitPane分隔表格和操作面板
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, operationPanel);
        splitPane.setDividerLocation(0.6);
        splitPane.setOneTouchExpandable(true);
        splitPane.setResizeWeight(0.5);
        panel.add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createAddProductPanel() {
        JPanel addPanel = new JPanel(new GridBagLayout());
        addPanel.setBorder(BorderFactory.createTitledBorder("添加商品"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel addNameLabel = new JLabel("名字");
        gbc.gridx = 0;
        gbc.gridy = 0;
        addPanel.add(addNameLabel, gbc);

        addNameField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 0;
        addPanel.add(addNameField, gbc);

        JLabel addPriceLabel = new JLabel("价格");
        gbc.gridx = 0;
        gbc.gridy = 1;
        addPanel.add(addPriceLabel, gbc);

        addPriceField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 1;
        addPanel.add(addPriceField, gbc);

        JLabel addQuantityLabel = new JLabel("数量");
        gbc.gridx = 0;
        gbc.gridy = 2;
        addPanel.add(addQuantityLabel, gbc);

        addQuantityField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 2;
        addPanel.add(addQuantityField, gbc);

        selectImageButton = new JButton("选择图片");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        addPanel.add(selectImageButton, gbc);

        imagePreviewLabel = new JLabel();
        gbc.gridy = 4;
        addPanel.add(imagePreviewLabel, gbc);

        selectImageButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(panel);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                try {
                    selectedImageBytes = Files.readAllBytes(selectedFile.toPath());
                    ImageIcon icon = new ImageIcon(selectedImageBytes);
                    Image scaledImage = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                    imagePreviewLabel.setIcon(new ImageIcon(scaledImage));
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(panel, "无法读取图片，请重试。", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        button1 = new JButton("提交");
        gbc.gridx = 1;
        gbc.gridy = 5;
        addPanel.add(button1, gbc);

        button1.addActionListener(e -> {
            String name = addNameField.getText();
            String price = addPriceField.getText();
            String quantity = addQuantityField.getText();

            if (name.isEmpty() || price.isEmpty() || quantity.isEmpty() || selectedImageBytes == null) {
                JOptionPane.showMessageDialog(panel, "请填写所有字段并选择图片。", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                double parsedPrice = Double.parseDouble(price);
                int parsedQuantity = Integer.parseInt(quantity);

                Product product = new Product(0, name, parsedPrice, parsedQuantity, selectedImageBytes);
                List<Serializable> temp = new ArrayList<>();
                temp.add(product);

                ClientController clientController = new ClientController();
                clientController.sendMessage(new Message(Message.MessageType.addProduct, temp));
                Message response = clientController.receiveMessage();

                if (response.getType() == Message.MessageType.success) {
                    JOptionPane.showMessageDialog(panel, "商品添加成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                    addNameField.setText("");
                    addPriceField.setText("");
                    addQuantityField.setText("");
                    imagePreviewLabel.setIcon(null);
                    selectedImageBytes = null;
                } else {
                    JOptionPane.showMessageDialog(panel, "商品添加失败，请重试。", "失败", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "价格和数量必须为数字。", "输入错误", JOptionPane.ERROR_MESSAGE);
            } catch (IOException | ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        });

        return addPanel;
    }

    private JPanel createDeleteProductPanel() {
        JPanel deletePanel = new JPanel(new GridBagLayout());
        deletePanel.setBorder(BorderFactory.createTitledBorder("删除商品"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel deleteIdLabel = new JLabel("编号");
        gbc.gridx = 0;
        gbc.gridy = 0;
        deletePanel.add(deleteIdLabel, gbc);

        deleteIdField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 0;
        deletePanel.add(deleteIdField, gbc);

        JLabel deleteNameLabel = new JLabel("名字");
        gbc.gridx = 0;
        gbc.gridy = 1;
        deletePanel.add(deleteNameLabel, gbc);

        deleteNameField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 1;
        deletePanel.add(deleteNameField, gbc);

        button2 = new JButton("提交");
        gbc.gridx = 1;
        gbc.gridy = 2;
        deletePanel.add(button2, gbc);

        button2.addActionListener(e -> {
            String id = deleteIdField.getText();
            String name = deleteNameField.getText();

            List<Serializable> temp = new ArrayList<>();
            if (!name.isEmpty()) {
                temp.add(name);
            } else if (!id.isEmpty()) {
                try {
                    temp.add(Integer.parseInt(id));
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(panel, "编号必须为数字。", "输入错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                JOptionPane.showMessageDialog(panel, "请输入编号或名字。", "输入错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                ClientController clientController = new ClientController();
                clientController.sendMessage(new Message(Message.MessageType.removeProduct, temp));
                Message response = clientController.receiveMessage();

                if (response.getType() == Message.MessageType.success) {
                    JOptionPane.showMessageDialog(panel, "商品删除成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                    deleteIdField.setText("");
                    deleteNameField.setText("");
                } else {
                    JOptionPane.showMessageDialog(panel, "商品删除失败，请重试。", "失败", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException | ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        });

        return deletePanel;
    }

    private JPanel createRestockProductPanel() {
        JPanel restockPanel = new JPanel(new GridBagLayout());
        restockPanel.setBorder(BorderFactory.createTitledBorder("补货"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel restockIdLabel = new JLabel("编号");
        gbc.gridx = 0;
        gbc.gridy = 0;
        restockPanel.add(restockIdLabel, gbc);

        restockIdField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 0;
        restockPanel.add(restockIdField, gbc);

        JLabel restockNameLabel = new JLabel("名字");
        gbc.gridx = 0;
        gbc.gridy = 1;
        restockPanel.add(restockNameLabel, gbc);

        restockNameField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 1;
        restockPanel.add(restockNameField, gbc);

        JLabel restockQuantityLabel = new JLabel("补货数量");
        gbc.gridx = 0;
        gbc.gridy = 2;
        restockPanel.add(restockQuantityLabel, gbc);

        restockQuantityField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 2;
        restockPanel.add(restockQuantityField, gbc);

        button3 = new JButton("提交");
        gbc.gridx = 1;
        gbc.gridy = 3;
        restockPanel.add(button3, gbc);

        button3.addActionListener(e -> {
            String id = restockIdField.getText();
            String name = restockNameField.getText();
            String quantity = restockQuantityField.getText();

            List<Serializable> temp = new ArrayList<>();
            try {
                if (!name.isEmpty()) {
                    temp.add(name);
                } else if (!id.isEmpty()) {
                    temp.add(Integer.parseInt(id));
                } else {
                    JOptionPane.showMessageDialog(panel, "请输入编号或名字。", "输入错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                temp.add(Integer.parseInt(quantity));

                ClientController clientController = new ClientController();
                Message message = new Message(Message.MessageType.increaseProductCount, temp);
                clientController.sendMessage(message);
                Message response = clientController.receiveMessage();

                if (response.getType() == Message.MessageType.success) {
                    JOptionPane.showMessageDialog(panel, "商品数量增加成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                    restockIdField.setText("");
                    restockNameField.setText("");
                    restockQuantityField.setText("");
                } else {
                    JOptionPane.showMessageDialog(panel, "补货失败，请重试。", "失败", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "编号和数量必须为数字。", "输入错误", JOptionPane.ERROR_MESSAGE);
            } catch (IOException | ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        });

        return restockPanel;
    }

    private void displayProducts(List<Product> products) {
        tableModel.setRowCount(0);
        for (Product product : products) {
            tableModel.addRow(new Object[]{
                    product.getProduct_Id(),
                    product.getName(),
                    product.getPrice(),
                    product.getCount()
            });
        }
    }

    private void filterTable(String searchText) {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        productTable.setRowSorter(sorter);
        if (searchText.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            try {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText, 0, 1));
            } catch (java.util.regex.PatternSyntaxException e) {
                JOptionPane.showMessageDialog(panel, "无效的搜索输入", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public JPanel getPanel() {
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Shop Manage UI");
            ShopManageUI shopManageUI = null;
            try {
                shopManageUI = new ShopManageUI();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            frame.setContentPane(shopManageUI.getPanel());
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 600);
            frame.setVisible(true);
        });
    }
}
