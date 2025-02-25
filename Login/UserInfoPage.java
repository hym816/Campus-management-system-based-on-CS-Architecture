package Server.Login;

import Server.MainWindow;
import Server.Public.*;
import Server.Login.passwordField;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import Server.Option2Panel;
import Server.Public.Button;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import java.awt.Font;
import java.io.IOException;
import java.time.LocalTime;

import static Server.MainWindow.stuid;


public class UserInfoPage implements ContentPanel {
    private JPanel panel;
    String id;
    String password;
    textField idField;
    passwordField passwordField;
    passwordField rePasswordField;
    JComboBox<String> identityField;
    JLabel imageUser;

    public UserInfoPage(ClientController clientController) throws IOException, ClassNotFoundException {
        //buttonFunc = new ButtonFunc(clientController);
        ClientController client = new ClientController(); // 初始化 Client 对象
        List<Serializable> queryInfo = new ArrayList<>();
        queryInfo.add(stuid);
        Message queryMessage = new Message(Message.MessageType.student_info_query, queryInfo);

        // 发送查询消息
        client.sendMessage(queryMessage);

        // 接收服务器响应
        Message receivedMessage = client.receiveMessage();
        List<Serializable> info = receivedMessage.getContent();



        panel = new JPanel();
        panel.setLayout(null); // Set absolute layout
        //添加头像
        //头像
        imageUser = new JLabel("");
        imageUser.setBounds(700,100,180,180);
        panel.add(imageUser);

        // 根据系统时间获取问候语
        JLabel greetingLabel = new JLabel(getGreetingMessage()+"，"+(String) info.get(0)+"!");
        greetingLabel.setBounds(20, 10, 300, 100); // 放在左上角
        greetingLabel.setFont(new Font("微软雅黑", Font.BOLD, 24)); // 设置字体为微软雅黑，并放大
        panel.add(greetingLabel);

        JLabel nameLabel = new JLabel((String) info.get(0));
        nameLabel.setBounds(20, 80, 300, 100); // 放在左上角
        nameLabel.setFont(new Font("微软雅黑", Font.BOLD, 36)); // 设置字体为微软雅黑，并放大
        panel.add(nameLabel);

        JLabel schoolLabel = new JLabel((String) info.get(5));
        schoolLabel.setBounds(20, 120, 300, 100); // 放在左上角
        schoolLabel.setFont(new Font("微软雅黑", Font.PLAIN, 20)); // 设置字体为微软雅黑，并放大
        panel.add(schoolLabel);

        JLabel classLabel = new JLabel((String) info.get(6)+"班");
        classLabel.setBounds(20, 150, 300, 100); // 放在左上角
        classLabel.setFont(new Font("微软雅黑", Font.PLAIN, 20)); // 设置字体为微软雅黑，并放大
        panel.add(classLabel);

        JLabel phoneLabel = new JLabel((String) info.get(8));
        phoneLabel.setBounds(20, 180, 300, 100); // 放在左上角
        phoneLabel.setFont(new Font("微软雅黑", Font.PLAIN, 20)); // 设置字体为微软雅黑，并放大
        panel.add(phoneLabel);

        JLabel emailLabel = new JLabel((String) info.get(9));
        emailLabel.setBounds(20, 210, 300, 100); // 放在左上角
        emailLabel.setFont(new Font("微软雅黑", Font.PLAIN, 20)); // 设置字体为微软雅黑，并放大
        panel.add(emailLabel);

        ClientController c = new ClientController();
        List<Serializable> temp = new ArrayList<>();
        temp.add(stuid);
        Message m = new Message(Message.MessageType.check_balance, temp);
        c.sendMessage(m);
        double balance = 0.00;
        Message r = c.receiveMessage();
        if (r.getType() == Message.MessageType.success)
            balance = (double) r.getContent().get(0);

        JLabel balanceLabel = new JLabel("账户余额：CN¥ "+String.valueOf(balance));
        balanceLabel.setBounds(20, 240, 300, 100); // 放在左上角
        balanceLabel.setFont(new Font("微软雅黑", Font.BOLD, 20)); // 设置字体为微软雅黑，并放大
        panel.add(balanceLabel);

        // 添加标签
        JLabel label2 = new JLabel("密  码");
        label2.setBounds(50, 250, 150, 30);
        panel.add(label2);


        JLabel label4 = new JLabel("确认密码");
        label4.setBounds(50, 300, 150, 30);
        panel.add(label4);



        //添加标签
        JLabel label3 = new JLabel("身  份");
        label3.setBounds(50, 300, 150, 30);
//        panel.add(label3);
        //panel.add(label3);
        //

        // 声明和添加 textField
        idField = new textField(40);
        idField.setBounds(150, 200, 300, 30); // 设置位置和大小
        idField.setEditable(false);

        // 声明和添加 passwordField
        passwordField = new passwordField(40);
        passwordField.setBounds(150, 250, 300, 30); // 设置位置和大小
        panel.add(passwordField);

        rePasswordField = new passwordField(40);
        rePasswordField.setBounds(150, 300, 300, 30); // 设置位置和大小
        panel.add(rePasswordField);

        //设置密码不可见
        setPasswordVisible(label2,label4,passwordField,rePasswordField);

        //声明和添加单选框
        identityField = new JComboBox<String>();
        identityField.addItem("学生");
        identityField.addItem("老师");
        identityField.addItem("管理员");
        identityField.setBounds(150,300,150,30);
        //identityField.setEnabled(false);
//        panel.add(identityField);

        //初始设置所有区域不可修改
        setAllEdible(identityField);

        // 添加返回按钮，点击后返回上一级页面
        TransparentButton backButton = new TransparentButton("");
        backButton.setBounds(0, 20, 120, 30);
        panel.add(backButton);
        //上传新头像
        TransparentButton imageButton = new TransparentButton("上传新头像");
        imageButton.setBounds(700, 300, 120, 30);
        panel.add(imageButton);
        //初始设置修改头像的按钮不可见
        imageButton.setVisible(false);
        imageButton.setEnabled(false);

        //注销
        TransparentButton logoffButton = new TransparentButton("注销...");
        logoffButton.setBounds(500,650,120,30);
        panel.add(logoffButton);
        // 修改
        Button revise = new Button("修  改", Color.BLUE, 30, 1, 1);
        revise.setBounds(100, 650, 140, 40);
        panel.add(revise);

        //重置密码
        TransparentButton resetPassword = new TransparentButton("重置密码...");
        resetPassword.setBounds(300, 650, 140, 40);
        panel.add(resetPassword);

        //保存
        Button save = new Button("保  存", Color.BLUE, 30, 1, 1);
        save.setBounds(100, 650, 140, 40);
        panel.add(save);
        //初始设置保存按钮不可见
        save.setVisible(false);
        save.setEnabled(false);

        //登出
        Button logout = new Button("登  出");
        logout.setBounds(300,350,140,40);

        //
        /*按钮功能*/
        //注销功能
        logoffButton.addActionListener(e->{
            int flag = JOptionPane.showConfirmDialog(null,"确定要注销吗？","注销警告",
                    JOptionPane.YES_NO_OPTION);
            //flag == 0 说明确定注销
            //flag == 1 说明取消注销
            if(flag == 0){
                int flag2 = logoffButtonFunc(clientController,idField,passwordField,identityField);
                if(flag2 > 0){
                    clearAllField(idField,passwordField);
                    switchToOption1();
                }
            }
        });
        //退出功能
        backButton.addActionListener(e -> {
            if(save.isVisible()){
                //设置按钮可见/不可见
                setBtnVisible(revise,save,imageButton,logoffButton,resetPassword,logout);
                resetPassword.setVisible(true);
                //设置区域不可编辑
                setAllEdible(identityField);
            }else if (resetPassword.getText().equals("完成")){
                resetPassword.setText("重置密码...");
                //隐藏密码区域
                setPasswordVisible(label2,label4,passwordField,rePasswordField);
                //显示其他区域
                setCombVisible(label3,identityField);
                //所有按钮显示
                revise.setVisible(false);
                setBtnVisible(revise,null,null,logoffButton,null,logout);
            }else{
                switchToOption1();
            }

        });
        //修改功能
        revise.addActionListener(e->{
            //设置按钮可见/不可见
            setBtnVisible(revise,save,imageButton,logoffButton,resetPassword,logout);
            resetPassword.setVisible(false);
            //设置区域可修改
            setAllEdible(identityField);
        });
        //保存功能
        save.addActionListener(e->{
            //保存，发消息到服务端
            int flag = 0;
            flag = saveButtonFunc(clientController,idField,identityField,imageUser);
            if(flag > 0){
                //设置按钮可见/不可见
                setBtnVisible(revise,save,imageButton,logoffButton,resetPassword,logout);
                resetPassword.setVisible(true);
                //设置区域不可编辑
                setAllEdible(identityField);
            }
        });
        //登出功能
        logout.addActionListener(e->{
            //清空所有区域
            clearAllField(idField,passwordField);
            //返回主界面
            switchToOption1();
        });
        //修改头像功能
        imageButton.addActionListener(e->{
            byte[] imageBytes = getImageLocal();
            //判断是否有选择图片
            if(imageBytes != null){
                setImageUser(imageBytes,imageUser);
            }

        });
        //重置密码
        resetPassword.addActionListener(e->{
            if(resetPassword.getText().equals("重置密码...")){
                resetPassword.setText("完成");
                passwordField.setText("");
                rePasswordField.setText("");
                //显示密码区域
                setPasswordVisible(label2,label4,passwordField,rePasswordField);
                //隐藏其他区域
                setCombVisible(label3,identityField);
                //所有按钮隐藏
                revise.setVisible(true);
                setBtnVisible(revise,null,null,logoffButton,null,logout);
            }else{
                int flag = resetPasswordButtonFunc(passwordField,rePasswordField);
                if(flag == 1){
                    resetPassword.setText("重置密码...");
                    //隐藏密码区域
                    setPasswordVisible(label2,label4,passwordField,rePasswordField);
                    //显示其他区域
                    setCombVisible(label3,identityField);
                    //所有按钮显示
                    revise.setVisible(false);
                    setBtnVisible(revise,null,null,logoffButton,null,logout);
                    //调用保存
                    saveButtonFunc(clientController,idField,identityField,imageUser);
                }
            }

        });



    }

    // 获取问候语的方法
    private String getGreetingMessage() {
        LocalTime now = LocalTime.now();
        if (now.isBefore(LocalTime.NOON)) {
            return "早上好";
        } else if (now.isBefore(LocalTime.of(13, 0))) {
            return "中午好";
        } else if (now.isBefore(LocalTime.of(18, 0))) {
            return "下午好";
        } else {
            return "晚上好";
        }
    }

    @Override
    public JPanel getPanel() {
        return panel;
    }
    //回调函数
    public void setId(String id){
        this.id = id;
    }
    // 返回上一级页面的方法
    private void switchToOption1() {
        // 获取 MainWindow 的实例并调用切换方法
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(panel);
        if (topFrame instanceof MainWindow) {
            ((MainWindow) topFrame).switchContent("首页");
        }
    }
    //设置部分区域可修改/不可修改
    public void setAllEdible(JComboBox<String> identityField){
        boolean isEnable = !identityField.isEnabled();
        identityField.setEnabled(isEnable);
    }
    //设置部分区域可见/不可见
    public void setPasswordVisible(JLabel label1, JLabel label2, passwordField passwordField, passwordField rePasswordField){
        boolean isVisible = label1.isVisible();
        label1.setVisible(!isVisible);
        label2.setVisible(!isVisible);
        passwordField.setVisible(!isVisible);
        rePasswordField.setVisible(!isVisible);
    }
    public void setCombVisible(JLabel label, JComboBox<String > identityField){
        boolean isVisible = label.isVisible();
        label.setVisible(!isVisible);
        identityField.setVisible(!isVisible);
    }
    //设置按钮是否可见/是否可用
    public void setBtnVisible(Button revise, Button save, TransparentButton imageButton, TransparentButton logoffBtn, TransparentButton resetP, Button logoutBtn){
        boolean isVisible = revise.isVisible();
        //修改按钮
        revise.setVisible(!isVisible);
        revise.setEnabled(!isVisible);
        if(logoffBtn != null){
            logoffBtn.setVisible(!isVisible);
        }
        if(logoutBtn != null){
            logoutBtn.setVisible(!isVisible);
        }
        if(resetP!=null){
            resetP.setVisible(!isVisible);
        }
        //保存按钮
        if(save!=null){
            save.setVisible(isVisible);
            save.setEnabled(isVisible);
        }
        //上传新图片
        if(imageButton!=null){
            imageButton.setVisible(isVisible);
            imageButton.setEnabled(isVisible);
        }

    }

    /*设置部分区域的值*/
    //按比例设置图片到JLabel里
    public void setImageUser(byte[] imageBytes, JLabel imageUser){
        ImageIcon imageIcon = new ImageIcon(imageBytes);
        //将头像固定大小
        BufferedImage scaledImage = null;
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes)) {
            // 从字节流中读取图像
            BufferedImage originalImage = ImageIO.read(inputStream);
            if (originalImage != null) {
                // 设定图片宽度和高度
                System.out.println(originalImage.getWidth()+".    "+originalImage.getHeight());
                double k = (double) originalImage.getWidth() /originalImage.getHeight();//宽高比
                System.out.println("宽高比："+k);
                int targetHeight = 200; // 图片高度固定
                int targetWidth = (int) (targetHeight*k); // 按比例设置图片宽度

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
    //设置所有区域的值
    public void setAllField(ClientController clientController,String id) throws IOException {
        List<Serializable> userInfo = null;

        try {
            userInfo = clientController.showUserInfo(id);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        //id
        idField.setText(userInfo.get(0).toString());
        //密码
        //passwordField.setText(userInfo.get(1).toString());
        password = userInfo.get(1).toString();
        //头像
        byte[] imageBytes = (byte[]) userInfo.get(3);
        if(imageBytes!=null){
            setImageUser(imageBytes,imageUser);
        }

        //身份
        switch (userInfo.get(2).toString()){
            case "student":
                identityField.setSelectedIndex(0);
                break;
            case "teacher":
                identityField.setSelectedIndex(1);
                break;
            case "manager":
                identityField.setSelectedIndex(2);
                break;
        }
    }
    //清空原有信息
    public void clearAllField(textField idField, passwordField passwordField){
        idField.setText("");
        passwordField.setText("");

    }

    /*点击保存按钮*/
    //将JLabel转化为byte[]类型
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
    }
    //保存按钮功能
    public int saveButtonFunc(ClientController clientController, textField idField, JComboBox<String> identityField, JLabel imageUser){
        String id = idField.getText();
        String password = this.password;
        String identity = null;
        byte[] imageBytes = getImageBytesFromJLabel(imageUser);
        switch (identityField.getSelectedItem().toString()){
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
        int flag = 0;
        try {
            //int flag = 0;
            flag = clientController.reviseTest(id,password,identity,imageBytes);
            switch (flag){
                case 1:
                    JOptionPane.showMessageDialog(null, "学生修改成功");
                    break;
                case 2:
                    JOptionPane.showMessageDialog(null, "老师修改成功");
                    break;
                case 3:
                    JOptionPane.showMessageDialog(null, "管理员修改成功");
                    break;
                case 0:
                    JOptionPane.showMessageDialog(null, "修改失败，用户名不能为空","警告",JOptionPane.ERROR_MESSAGE);
                    break;
                case -1:
                    JOptionPane.showMessageDialog(null, "修改失败，用户名已存在","警告",JOptionPane.ERROR_MESSAGE);
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "ERROR","错误",JOptionPane.ERROR_MESSAGE);
            }
            return flag;

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    /*点击注销按钮*/
    public int logoffButtonFunc(ClientController clientController, textField idField, passwordField passwordField, JComboBox<String> identityField){
        String id = idField.getText();
        String password = passwordField.getText();
        String identity = null;
        switch (identityField.getSelectedItem().toString()){
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
        int flag = 0;
        try {
            flag = clientController.logoffTest(id,password,identity);
            switch (flag){
                case 1:
                    JOptionPane.showMessageDialog(null, "学生注销成功");
                    break;
                case 2:
                    JOptionPane.showMessageDialog(null, "老师注销成功");
                    break;
                case 3:
                    JOptionPane.showMessageDialog(null, "管理员注销成功");
                    break;
                case 0:
                    JOptionPane.showMessageDialog(null, "注销失败，用户名不能为空","警告",JOptionPane.ERROR_MESSAGE);
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "ERROR","错误",JOptionPane.ERROR_MESSAGE);
            }
            return flag;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    /*点击重置密码*/
    public int resetPasswordButtonFunc(passwordField passwordField, passwordField rePasswordField){
        String password =new String(passwordField.getPassword());
        String rePassword = new String(rePasswordField.getPassword());
        if(password.equals(rePassword)){
            if(!password.isEmpty()){//password不为空
                this.password = toMD5(password);
                return 1;
            }else {
                JOptionPane.showMessageDialog(null, "密码不能为空！", "错误", JOptionPane.ERROR_MESSAGE);
                return 0;
            }
        }else {
            JOptionPane.showMessageDialog(null, "两次输入的密码不同！", "错误", JOptionPane.ERROR_MESSAGE);
            return 0;
        }

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