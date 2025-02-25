package Server.Store;

import Server.Public.ClientController;
import Server.Public.Global;
import Server.Public.Message;
import Server.Public.Product;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartUI extends JFrame {
    private JPanel cartPanel;
    private JButton checkoutButton;
    private JButton updateButton; // 总更新按钮
    private JLabel totalPriceLabel;
    private List<Product> parsedProducts; // 假设所有商品存在这里
    private Map<Integer, JSpinner> quantitySpinners; // 存储每个商品对应的数量选择器
    private double totalPrice; // 总价作为数据成员

    public CartUI(List<Product> parsedProducts) {
        this.parsedProducts = parsedProducts; // 初始化商品列表
        this.quantitySpinners = new HashMap<>(); // 初始化数量选择器映射
        this.totalPrice = 0.0; // 初始化总价

        setTitle("购物车");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        cartPanel = new JPanel();
        cartPanel.setLayout(new BoxLayout(cartPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(cartPanel);
        add(scrollPane, BorderLayout.CENTER);

        checkoutButton = new JButton("结算");
        checkoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 检查购物车中的每个商品数量是否超过库存
                if (checkInventory()) {
                    JOptionPane.showMessageDialog(cartPanel, "库存不足，请调整购物车数量。", "错误", JOptionPane.ERROR_MESSAGE);
                } else {
                    // 结算逻辑，由用户自行实现
                    showPaymentDialog();
                    Global.clearCart(); // 清空购物车
                    refreshCart(); // 刷新购物车显示
                }
            }
        });

        updateButton = new JButton("更新"); // 创建总更新按钮
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateCartQuantities(); // 调用方法更新购物车中所有商品的数量
                refreshCart(); // 刷新购物车显示
            }
        });

        totalPriceLabel = new JLabel("总价: CN¥0.0");
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.add(updateButton); // 将总更新按钮添加到左侧
        bottomPanel.add(leftPanel, BorderLayout.WEST); // 将左侧面板添加到底部面板的西侧
        bottomPanel.add(totalPriceLabel, BorderLayout.CENTER); // 添加总价标签到底部面板
        bottomPanel.add(checkoutButton, BorderLayout.EAST); // 将结算按钮添加到底部面板的右侧

        add(bottomPanel, BorderLayout.SOUTH);

        refreshCart(); // 显示购物车内容
    }

    // 刷新购物车界面，显示购物车中的商品编号和数量
    public void refreshCart() {
        cartPanel.removeAll();
        quantitySpinners.clear(); // 清空现有的数量选择器映射
        Map<Integer, Integer> cartItems = Global.cart;
        totalPrice = 0.0; // 重置总价

        for (Map.Entry<Integer, Integer> entry : cartItems.entrySet()) {
            int productId = entry.getKey();
            int quantity = entry.getValue();

            // 通过商品编号从 parsedProducts 中查找价格
            double price = getProductPrice(productId);
            double itemTotal = price * quantity;
            totalPrice += itemTotal; // 更新总价

            // 获取对应的商品库存
            int stock = getProductStock(productId);

            JPanel itemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            itemPanel.add(new JLabel("商品编号: " + productId + " 数量: " + quantity + " 单价: CN¥" + price + " 小计: CN¥" + itemTotal));

            // 数量选择器
            JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(quantity, 1, stock, 1));
            quantitySpinners.put(productId, quantitySpinner); // 将数量选择器存储到映射中
            itemPanel.add(quantitySpinner);

            cartPanel.add(itemPanel);
        }

        totalPriceLabel.setText("总价: CN¥" + totalPrice); // 更新总价标签显示
        cartPanel.revalidate();
        cartPanel.repaint();
    }

    // 更新购物车中所有商品的数量
    private void updateCartQuantities() {
        for (Map.Entry<Integer, JSpinner> entry : quantitySpinners.entrySet()) {
            int productId = entry.getKey();
            int newQuantity = (int) entry.getValue().getValue();
            Global.cart.put(productId, newQuantity); // 更新购物车中的数量
        }
    }

    // 检查购物车中商品的数量是否超过库存
    private boolean checkInventory() {
        for (Map.Entry<Integer, Integer> entry : Global.cart.entrySet()) {
            int productId = entry.getKey();
            int quantity = entry.getValue();
            int stock = getProductStock(productId);

            if (quantity > stock) {
                return true; // 有商品数量超过库存
            }
        }
        return false; // 所有商品数量均在库存范围内
    }

    // 通过商品编号从 parsedProducts 列表中获取价格
    private double getProductPrice(int productId) {
        for (Product product : parsedProducts) {
            if (product.getProduct_Id() == productId) {
                return product.getPrice();
            }
        }
        return 0.0; // 如果没有找到商品，则返回 0.0
    }

    // 通过商品编号从 parsedProducts 列表中获取库存
    private int getProductStock(int productId) {
        for (Product product : parsedProducts) {
            if (product.getProduct_Id() == productId) {
                return product.getCount();
            }
        }
        return 0; // 如果没有找到商品，则返回 0
    }

    // 显示支付对话框
    private void showPaymentDialog() {
        JDialog paymentDialog = new JDialog(this, "支付", true);
        paymentDialog.setSize(300, 200);
        paymentDialog.setLayout(new GridLayout(4, 1));
        paymentDialog.setLocationRelativeTo(this);

        // 账号输入
        JPanel accountPanel = new JPanel(new FlowLayout());
        JLabel accountLabel = new JLabel("账号:");
        JTextField accountField = new JTextField(15);
        accountPanel.add(accountLabel);
        accountPanel.add(accountField);

        // 密码输入
        JPanel passwordPanel = new JPanel(new FlowLayout());
        JLabel passwordLabel = new JLabel("密码:");
        JPasswordField passwordField = new JPasswordField(15);
        passwordPanel.add(passwordLabel);
        passwordPanel.add(passwordField);

        // 支付按钮
        JButton payButton = new JButton("支付");
        payButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 执行支付逻辑，例如验证账号和密码
                String account = accountField.getText();
                String password = new String(passwordField.getPassword());

                // 简单支付逻辑验证（实际应用中需要更严格的验证）
                if (account.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(paymentDialog, "账号或密码不能为空！", "错误", JOptionPane.ERROR_MESSAGE);
                } else {

                    Message res1,res2;
                    try {
                        ClientController c = new ClientController();
                        List<Serializable> te = new ArrayList<>();
                        te.add(account);
                        te.add(password);
                        c.sendMessage(new Message(Message.MessageType.determinePassword,te));
                        res1 = c.receiveMessage();
                        List<Serializable> te2 = new ArrayList<>();
                        te2.add(account);
                        te2.add(totalPrice);
                        c.sendMessage(new Message(Message.MessageType.withdraw,te2));
                        res2 = c.receiveMessage();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    } catch (ClassNotFoundException ex) {
                        throw new RuntimeException(ex);
                    }


                    if (res1.getType() == Message.MessageType.success &&
                            res2.getType() == Message.MessageType.success)
                    {
                        JOptionPane.showMessageDialog(paymentDialog, "支付成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                        // 支付成功后，遍历购物车，减少库存
                        Map<Integer, Integer> cartItems = Global.cart;
                        for (Map.Entry<Integer, Integer> entry : cartItems.entrySet()) {
                            int productId = entry.getKey();
                            int quantity = entry.getValue();

                            try {
                                ClientController c = new ClientController();
                                List<Serializable> temp=new ArrayList<>();
                                temp.add(productId);
                                temp.add(quantity);
                                Message message =new Message(Message.MessageType.purchaseProduct,temp);
                                c.sendMessage(message);
                                Message r =c.receiveMessage();
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            } catch (ClassNotFoundException ex) {
                                throw new RuntimeException(ex);
                            }


                        }
                        Global.clearCart(); // 清空购物车
                        refreshCart(); // 刷新购物车显示
                        paymentDialog.dispose(); // 关闭支付对话框
                    } else {
                        JOptionPane.showMessageDialog(paymentDialog, "支付失败，请检查账号，密码和余额。", "错误", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        // 将组件添加到对话框
        paymentDialog.add(accountPanel);
        paymentDialog.add(passwordPanel);
        paymentDialog.add(payButton);

        paymentDialog.setVisible(true);
    }
}


