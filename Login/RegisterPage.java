package Server.Login;

import Server.MainWindow;
import Server.Public.ClientController;
import Server.Public.ContentPanel;
import Server.Public.TransparentButton;
import Server.Public.textField;
import Server.Public.Button;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RegisterPage extends JFrame {

    private JPanel panel;
    public RegisterPage(ClientController clientController){
        JPanel panel;
        panel = new JPanel();
        panel.setLayout(null); // Set absolute layout

        //添加头像
        JLabel imageUser;
        imageUser = new JLabel("");
        imageUser.setBounds(250,50,100,100);
        panel.add(imageUser);

        // 添加标签
        JLabel label1 = new JLabel("用户名");
        label1.setBounds(50, 200, 150, 30);
        panel.add(label1);

        // 添加标签
        JLabel label2 = new JLabel("密  码");
        label2.setBounds(50, 250, 150, 30);
        panel.add(label2);
        JLabel label4 = new JLabel("确认密码");
        label4.setBounds(50, 300, 150, 30);
        panel.add(label4);

        //添加标签
        JLabel label3 = new JLabel("身  份");
        label3.setBounds(50, 350,150,30);
        panel.add(label3);


        // 添加返回按钮，点击后返回上一级页面
        TransparentButton backButton = new TransparentButton("< 返回");
        backButton.addActionListener(e -> switchToLogin(clientController));
        backButton.setBounds(0, 20, 120, 30);
        panel.add(backButton);

        // 声明和添加 textField
        textField idField = new textField(40);
        idField.setBounds(150, 200, 300, 30); // 设置位置和大小
        panel.add(idField);

        // 声明和添加 passwordField
        passwordField passwordField = new passwordField(40);
        passwordField.setBounds(150, 250, 300, 30); // 设置位置和大小
        panel.add(passwordField);

        passwordField rePasswordField = new passwordField(40);
        rePasswordField.setBounds(150, 300, 300, 30); // 设置位置和大小
        panel.add(rePasswordField);

        //上传新头像
        TransparentButton imageButton = new TransparentButton("上传新头像");
        imageButton.setBounds(230, 150, 120, 30);
        panel.add(imageButton);

        //声明和添加单选框
        JComboBox<String> identityField = new JComboBox<String>();
        identityField.addItem("学生");
        identityField.addItem("老师");
        identityField.addItem("管理员");
        identityField.setBounds(150,350,150,30);
        panel.add(identityField);

        // 注册
        Button register = new Button("注  册", Color.BLUE,30,1,1);
        register.setBounds(110,400,140,40);
        panel.add(register);

        // 重置
        Button reset = new Button("重  置");
        reset.setBounds(300,400,140,40);
        panel.add(reset);

        /*功能*/
        //注册
        register.addActionListener(e->{
            int flag = registerButtonFunc(clientController,idField,passwordField,rePasswordField,identityField,imageUser);
            //注册成功，返回主界面，清空区域
            if(flag > 0){
                clearAllField(imageUser,idField,passwordField,rePasswordField);
                switchToLogin(clientController);
            }
            //注册失败，清空区域，不返回主界面
            else {
                clearAllField(imageUser,idField,passwordField,rePasswordField);
            }
        });
        //重置
        reset.addActionListener(e->{
            clearAllField(imageUser,idField,passwordField,rePasswordField);
        });
        //修改头像功能
        imageButton.addActionListener(e->{
            byte[] imageBytes = getImageLocal();
            if(imageBytes != null){
                setImageUser(imageBytes,imageUser);
            }
        });

        //      获取显示屏的大小
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int sw = screenSize.width;
        int sh = screenSize.height;
        //      设置窗口的位置
        int width = 500;
        int height = 600;
        this.setBounds((sw - width) / 2, (sh - height) / 2, width, height);
        this.add(panel);
        this.setVisible(true);
    }

    //@Override
    public JPanel getPanel() {
        return panel;
    }
    //清空原有信息
    public void clearAllField(JLabel imageUser, textField idField, passwordField passwordField, passwordField rePasswordField){
        imageUser.setIcon(null);
        idField.setText("");
        passwordField.setText("");
        rePasswordField.setText("");
    }
    // 返回上一级页面的方法
//    private void switchToOption1() {
//        // 获取 MainWindow 的实例并调用切换方法
//        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(panel);
//        if (topFrame instanceof MainWindow) {
//            ((MainWindow) topFrame).switchContent("首页");
//        }
//    }
    private void switchToLogin(ClientController clientController){
        this.dispose();
        new LoginPage(clientController);
    }
    //注册按钮
    public int registerButtonFunc(ClientController clientController, textField idField, passwordField passwordField, passwordField rePasswordField, JComboBox<String> identityField, JLabel imageUser) {
        /*初始化数据*/
        //id
        String id = idField.getText();
        //密码
        String password = toMD5(passwordField.getText());
        String rePassword = toMD5(rePasswordField.getText());
        //身份
        String identity = null;
        switch (identityField.getSelectedItem().toString()) {
            case "学生":
                identity = "student";
                break;
            case "老师":
                identity = "teacher";
                break;
            case "管理员":
                identity = "manager";
                break;
            default:
                identity = "student";
        }
        //头像图片
        byte[] imageBytes = getImageBytesFromJLabel(imageUser);
        System.out.println("id: " + id);
        System.out.println("password: " + password);
        System.out.println("identity: " + identity);
        int flag = 0;
        if(password.equals(rePassword))
        {
            try {
                flag = clientController.registerTest(id, password, identity, imageBytes);
                switch (flag) {
                    case 1:
                        JOptionPane.showMessageDialog(null, "学生注册成功");
                        break;
                    case 2:
                        JOptionPane.showMessageDialog(null, "老师注册成功");
                        break;
                    case 3:
                        JOptionPane.showMessageDialog(null, "管理员注册成功");
                        break;
                    case 0:
                        JOptionPane.showMessageDialog(null, "注册失败，用户名不能为空", "警告", JOptionPane.ERROR_MESSAGE);
                        break;
                    case -1:
                        JOptionPane.showMessageDialog(null, "注册失败，用户名已存在", "警告", JOptionPane.ERROR_MESSAGE);
                        break;
                    default:
                        JOptionPane.showMessageDialog(null, "ERROR", "错误", JOptionPane.ERROR_MESSAGE);
                }
                //返回标识符
                return flag;
            } catch (IOException | ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        }
        else {
            JOptionPane.showMessageDialog(null, "两次输入的密码不同！", "错误", JOptionPane.ERROR_MESSAGE);
            return flag;
        }
    }
    //获取本地图片
    public byte[] getImageLocal(){
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("选择图片文件");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        // 可以在这里添加文件过滤器，以限制用户只能选择图片文件
        fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "图片文件 (*.jpg, *.jpeg, *.png, *.gif)",
                "jpg", "jpeg", "png", "gif"));
        fileChooser.addChoosableFileFilter(fileChooser.getAcceptAllFileFilter());


        int result = fileChooser.showOpenDialog(this.getPanel());
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                // 读取图片文件
                BufferedImage image = ImageIO.read(selectedFile);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                if (image != null) {
                    // 将BufferedImage写入到ByteArrayOutputStream中
                    ImageIO.write(image, "jpg", baos); // 注意：这里假设你想保存为JPG格式，你也可以选择其他格式如"png"
                    // 返回ByteArrayOutputStream中的字节数组
                    return baos.toByteArray();
                } else {
                    JOptionPane.showMessageDialog(null, "读取的图片文件为空或无效。");
                    return null; // 或者抛出一个异常，取决于你的错误处理策略
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "读取图片文件时发生错误：" + ex.getMessage());
            }
        }
        return null;
    }
    //转换图片为字符
    public byte[] getImageBytesFromJLabel(JLabel label) {
        // 假设label的图标是ImageIcon
        Icon icon = label.getIcon();
        if (icon instanceof ImageIcon) {
            ImageIcon imageIcon = (ImageIcon) icon;
            Image image = imageIcon.getImage();

            // 将Image转换为BufferedImage（如果需要的话，比如直接来源于网络或某些特定来源的Image可能不是BufferedImage）
            BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
            bufferedImage.getGraphics().drawImage(image, 0, 0, null);

            // 将BufferedImage写入ByteArrayOutputStream
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                // 指定格式，例如"jpg"或"png"
                ImageIO.write(bufferedImage, "jpg", baos);
                return baos.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
                return null; // 或抛出异常，取决于你的错误处理策略
            }
        }
        return null; // 如果没有找到ImageIcon或ImageIcon为空，则返回null
    }//将JLabel转化为byte[]类型
    //设置图片
    public void setImageUser(byte[] imageBytes, JLabel imageUser){
        ImageIcon imageIcon = new ImageIcon(imageBytes);
        //将头像固定大小
        BufferedImage scaledImage = null;
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes)) {
            // 从字节流中读取图像
            BufferedImage originalImage = ImageIO.read(inputStream);
            if (originalImage != null) {
                // 设定你想要的目标宽度和高度
                System.out.println(originalImage.getWidth()+".    "+originalImage.getHeight());
                double k = (double) originalImage.getWidth() /originalImage.getHeight();//宽高比
                System.out.println("宽高比："+k);
                int targetHeight = 100; // 示例高度
                int targetWidth = (int) (targetHeight*k); // 示例宽度

                // 缩放图像
                scaledImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = scaledImage.createGraphics();
                g2d.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
                g2d.dispose();

                // 封装缩放后的图像为ImageIcon
                imageIcon = new ImageIcon(scaledImage);
            }
        } catch (IOException e) {
            e.printStackTrace(); // 或者处理异常，例如通过日志记录
        }
        imageUser.setIcon(imageIcon);
    }
    //将密码进行MD5码加密
    public String toMD5(String input) {
        try {
            // 创建一个MD5 Hash
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 计算hash值
            md.update(input.getBytes());
            byte[] digest = md.digest();

            // 将字节转换为十六进制表示形式
            StringBuilder hexString = new StringBuilder();
            for (byte b : digest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            // 返回哈希值
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
