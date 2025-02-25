package Server.Login;

import Server.MainWindow;
import Server.Public.ClientController;
import Server.Public.ContentPanel;
import Server.Public.TransparentButton;
import Server.Public.Button;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginPage extends JFrame implements ContentPanel {
    private JPanel panel;

    public LoginPage(ClientController clientController) {
        // 创建主面板并设置布局
        panel = new JPanel(new BorderLayout());

        // 创建中间面板用于居中显示所有组件
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS)); // 使用垂直BoxLayout布局

        // 创建一个Box用于垂直居中对齐
        Box verticalBox = Box.createVerticalBox();

        // 创建图片标签并加载图片
        JLabel imageLabel = new JLabel();
        ImageIcon imageIcon = loadIcon("seu.jpeg", 535); // 使用loadIcon方法加载图片，指定高度为100像素
        imageLabel.setIcon(imageIcon);
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // 水平居中

        // 将图片标签添加到垂直Box中
        verticalBox.add(imageLabel);
        verticalBox.add(Box.createVerticalStrut(20)); // 添加垂直间距

        // 创建并添加带浮动标签的用户名输入框
        FloatingLabelTextComponent idField = new FloatingLabelTextComponent("用户名", false);
        idField.setMaximumSize(new Dimension(300, 60)); // 固定宽度和高度
        idField.setAlignmentX(Component.CENTER_ALIGNMENT); // 水平居中
        verticalBox.add(idField);
        verticalBox.add(Box.createVerticalStrut(20)); // 增加间距，防止元素拥挤

        // 创建并添加带浮动标签的密码输入框
        FloatingLabelTextComponent passwordField = new FloatingLabelTextComponent("密码", true);
        passwordField.setMaximumSize(new Dimension(300, 60));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        verticalBox.add(passwordField);
        verticalBox.add(Box.createVerticalStrut(20)); // 增加间距

        // 创建并添加按钮
        TransparentButton backButton = new TransparentButton("< 注册");
        backButton.addActionListener(e -> switchToRegister(clientController));
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        verticalBox.add(backButton);
        verticalBox.add(Box.createVerticalStrut(20)); // 添加垂直间距

        // 增加登录按钮的高度
        Button login = new Button("登  录", Color.BLUE, 10, 10, 10); // 设置更大的高度
        login.setAlignmentX(Component.CENTER_ALIGNMENT);
        verticalBox.add(login);
        verticalBox.add(Box.createVerticalStrut(20)); // 添加垂直间距

        Button reset = new Button("重  置", Color.WHITE, 10, 10, 10); // 保持按钮高度一致
        reset.setAlignmentX(Component.CENTER_ALIGNMENT);
        verticalBox.add(reset);

        // 将垂直Box添加到中间面板
        centerPanel.add(verticalBox);

        // 将中间面板添加到主面板的中心
        panel.add(centerPanel, BorderLayout.CENTER);

        // 设置窗口的属性
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int sw = screenSize.width;
        int sh = screenSize.height;
        int width = 800;
        int height = 1000; // 增加整体窗口高度以适应更高的按钮和间距
        this.setBounds((sw - width) / 2, (sh - height) / 2, width, height);
        this.add(panel);
        this.setVisible(true);

        // 功能：登入
        login.addActionListener(e -> {
            int flag = loginTipsShow(clientController, idField, passwordField);
            if (flag > 0) {
                try {
                    new MainWindow(idField.getText(), flag);
                } catch (IOException | ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
                this.dispose();
            }
        });

        // 功能：重置
        reset.addActionListener(e -> {
            clearAllField(idField, passwordField);
        });
    }

    @Override
    public JPanel getPanel() {
        return panel;
    }

    private void switchToRegister(ClientController clientController) {
        this.dispose();
        new RegisterPage(clientController);
    }

    // 登陆提示
    public int loginTipsShow(ClientController clientController, FloatingLabelTextComponent idField, FloatingLabelTextComponent passwordField) {
        System.out.println("id: " + idField.getText());
        System.out.println("password: " + passwordField.getText());
        String id = idField.getText();
        String password = toMD5(passwordField.getText());
        try {
            int flag = clientController.loginTest(id, password);
            switch (flag) {
                case 1:
//                    JOptionPane.showMessageDialog(null, "学生登录成功");
                    break;
                case 2:
//                    JOptionPane.showMessageDialog(null, "老师登录成功");
                    break;
                case 3:
//                    JOptionPane.showMessageDialog(null, "管理员登录成功");
                    break;
                case 0:
                    JOptionPane.showMessageDialog(null, "账号或密码错误", "登录失败", JOptionPane.ERROR_MESSAGE);
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "ERROR", "错误", JOptionPane.ERROR_MESSAGE);
            }
            return flag;

        } catch (IOException | ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    // 清空原有信息
    public void clearAllField(FloatingLabelTextComponent idField, FloatingLabelTextComponent passwordField) {
        idField.setText("");
        passwordField.setText("");
    }

    // 将密码进行MD5码加密
    public String toMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(input.getBytes());
            byte[] digest = md.digest();

            StringBuilder hexString = new StringBuilder();
            for (byte b : digest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    // 图片加载方法，指定高度并保留原始长宽比
    private ImageIcon loadIcon(String path, int targetHeight) {
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(path));
            Image img = icon.getImage();
            int originalWidth = icon.getIconWidth();
            int originalHeight = icon.getIconHeight();
            double scale = (double) targetHeight / originalHeight;
            int targetWidth = (int) (originalWidth * scale);
            Image resizedImg = img.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
            return new ImageIcon(resizedImg);
        } catch (Exception e) {
            System.out.println("图片加载失败: " + e.getMessage());
            return null;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new LoginPage(new ClientController());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
