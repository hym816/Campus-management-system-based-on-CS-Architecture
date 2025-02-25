package Server.Store;

import Server.Public.Global;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ProductComponent extends JPanel {
    private String productId;
    private String productName;
    private byte[] productImage;
    private int stock;
    private double price;

    public ProductComponent(String productId, String productName, byte[] productImage, int stock, double price) {
        this.productId = productId;
        this.productName = productName;
        this.productImage = productImage;
        this.stock = stock;
        this.price = price;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setSize(new Dimension(100, 100));
        // 创建商品图片区域
        JLabel imageLabel = new JLabel();
        ImageIcon icon = new ImageIcon(productImage);
        Image scaledImage = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        icon = new ImageIcon(scaledImage);
        imageLabel.setIcon(icon);
        imageLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        add(imageLabel, BorderLayout.NORTH);

        // 创建信息和操作区域
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(1, 2));

        // 左侧信息区域
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        // 设置字体为微软雅黑，粗体，稍大
        Font nameFont = new Font("Microsoft YaHei", Font.BOLD, 16);
        JLabel nameLabel = new JLabel(productName);
        nameLabel.setFont(nameFont);
        detailsPanel.add(nameLabel);

        detailsPanel.add(new JLabel("CN¥" + price));
        detailsPanel.add(new JLabel("库存: " + stock));
        infoPanel.add(detailsPanel);

        // 右侧操作区域
        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.Y_AXIS));
        actionPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        // 数量选择器
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(1, 1, stock, 1);
        JSpinner quantitySpinner = new JSpinner(spinnerModel);

        // 设置数量选择器不可编辑，只能通过按钮增加减少
        JFormattedTextField spinnerTextField = ((JSpinner.DefaultEditor) quantitySpinner.getEditor()).getTextField();
        spinnerTextField.setEditable(false);
        actionPanel.add(quantitySpinner);

        // 加入购物车按钮
        JButton addToCartButton = new JButton("加入购物车");// 使用指定的图片路径
        addToCartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 将商品编号和数量添加到 Global 购物车列表中
                int id = Integer.parseInt(productId);
                int quantity = (int) quantitySpinner.getValue();
                Global.cart.put(id, Global.cart.getOrDefault(id, 0) + quantity);
            }
        });
        actionPanel.add(addToCartButton);

        infoPanel.add(actionPanel);
        add(infoPanel, BorderLayout.CENTER);

        setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));
    }

}
