/*package Server;

import Server.Public.ClientController;
import Server.Public.ContentPanel;
import Server.Public.Message;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ATMUI extends JPanel implements ContentPanel {
    private JFrame frame;
    private JPanel panelLogin, panelMain;
    private JTextField userText, depositText, withdrawText;
    private JPasswordField passwordText;
    private JLabel balanceLabel;
    private double balance = 0.0;
    private String cardId;

    public ATMUI() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame("ATMUI");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 设置 Login Panel，使用 GridBagLayout 布局
        panelLogin = new JPanel(new GridBagLayout());
        frame.getContentPane().add(panelLogin, BorderLayout.CENTER);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15); // 增加间距
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 用户名标签和输入框
        JLabel userLabel = new JLabel("CardId:");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelLogin.add(userLabel, gbc);

        userText = new JTextField(20);
        userText.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.ipady = 10; // 增加输入框高度
        panelLogin.add(userText, gbc);

        // 密码标签和输入框
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panelLogin.add(passwordLabel, gbc);

        passwordText = new JPasswordField(20);
        passwordText.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.ipady = 10; // 增加输入框高度
        panelLogin.add(passwordText, gbc);

        // 登录按钮
        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.ipady = 15; // 增加按钮高度
        gbc.ipadx = 30; // 增加按钮宽度
        panelLogin.add(loginButton, gbc);

        // 登录按钮的事件处理
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    if (authenticate(userText.getText(), new String(passwordText.getPassword()))) {
                        cardId = userText.getText();
                        getBalance();
                        showMainPanel();
                    } else {
                        JOptionPane.showMessageDialog(frame, "Invalid credentials", "Error", JOptionPane.ERROR_MESSAGE);
                    }

                } catch (IOException | ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        frame.setVisible(true);
    }

    private void getBalance() throws IOException, ClassNotFoundException {
        ClientController c = new ClientController();
        List<Serializable>temp=new ArrayList<>();
        temp.add(cardId);
        Message m = new Message(Message.MessageType.check_balance,temp);
        c.sendMessage(m);
        Message r = c.receiveMessage();
        if(r.getType()== Message.MessageType.success)
            balance= (double) r.getContent().get(0);
    }

    private boolean authenticate(String user, String password) throws IOException, ClassNotFoundException {

        ClientController c = new ClientController();
        List<Serializable>temp=new ArrayList<>();
        temp.add(user);
        temp.add(password);
        Message m = new Message(Message.MessageType.atm_login,temp);
        c.sendMessage(m);
        Message r = c.receiveMessage();
        if(r.getType()== Message.MessageType.success)
            return true;
        return false;
    }

    private void showMainPanel() {
        panelLogin.setVisible(false);

        // 主界面，包含存款和取款功能
        panelMain = new JPanel(new GridBagLayout());
        frame.getContentPane().add(panelMain, BorderLayout.CENTER);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15); // 增加间距
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 余额标签
        balanceLabel = new JLabel("Balance: $" + balance);
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.ipady = 20; // 增加标签高度
        panelMain.add(balanceLabel, gbc);

        // 存款输入框和按钮
        JLabel depositLabel = new JLabel("Deposit Amount:");
        depositLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panelMain.add(depositLabel, gbc);

        depositText = new JTextField(10);
        depositText.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.ipady = 10; // 增加输入框高度
        panelMain.add(depositText, gbc);

        JButton depositButton = new JButton("Deposit");
        depositButton.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.ipady = 15; // 增加按钮高度
        gbc.ipadx = 30; // 增加按钮宽度
        panelMain.add(depositButton, gbc);

        depositButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String amount = depositText.getText();
                if (amount != null && !amount.isEmpty()) {
                    try {
                        double deposit = Double.parseDouble(amount);
                        depositText.setText("");
                        if (deposit > 0) {
                            ClientController c = new ClientController();
                            List<Serializable>temp=new ArrayList<>();
                            temp.add(cardId);
                            temp.add(deposit);
                            Message m = new Message(Message.MessageType.deposit_2,temp);
                            c.sendMessage(m);
                            Message r = c.receiveMessage();
                            if(r.getType()==Message.MessageType.showQR)
                            {
                                byte[] qrCodeImage = (byte[]) r.getContent().get(0); // 获取字节数组
                                showQRCode(qrCodeImage,deposit);
                            }

                        } else {
                            JOptionPane.showMessageDialog(frame, "Enter a positive amount", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(frame, "Invalid amount", "Error", JOptionPane.ERROR_MESSAGE);
                    } catch (ClassNotFoundException ex) {
                        throw new RuntimeException(ex);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });

        // 取款输入框和按钮
        JLabel withdrawLabel = new JLabel("Withdraw Amount:");
        withdrawLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridx = 0;
        gbc.gridy = 2;
        panelMain.add(withdrawLabel, gbc);

        withdrawText = new JTextField(10);
        withdrawText.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.ipady = 10; // 增加输入框高度
        panelMain.add(withdrawText, gbc);

        JButton withdrawButton = new JButton("Withdraw");
        withdrawButton.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.ipady = 15; // 增加按钮高度
        gbc.ipadx = 30; // 增加按钮宽度
        panelMain.add(withdrawButton, gbc);

        withdrawButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String amount = withdrawText.getText();
                if (amount != null && !amount.isEmpty()) {
                    try {
                        double withdraw = Double.parseDouble(amount);
                        withdrawText.setText("");
                        if (withdraw > 0 && withdraw <= balance) {
                            ClientController c = new ClientController();
                            List<Serializable>temp=new ArrayList<>();
                            temp.add(cardId);
                            temp.add(withdraw);
                            Message m = new Message(Message.MessageType.withdraw,temp);
                            c.sendMessage(m);
                            Message r = c.receiveMessage();
                            if(r.getType()==Message.MessageType.success)
                            {
                                balance -= withdraw;
                                updateBalance();
                            }
                        } else {
                            JOptionPane.showMessageDialog(frame, "Enter a valid amount", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(frame, "Invalid amount", "Error", JOptionPane.ERROR_MESSAGE);
                    } catch (ClassNotFoundException ex) {
                        throw new RuntimeException(ex);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });

        // 修改密码按钮
        JButton changePasswordButton = new JButton("Change Password");
        changePasswordButton.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.ipady = 15; // 增加按钮高度
        gbc.ipadx = 30; // 增加按钮宽度
        panelMain.add(changePasswordButton, gbc);

        changePasswordButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JDialog changePasswordDialog = new JDialog(frame, "Change Password", true);
                changePasswordDialog.setSize(400, 300);
                changePasswordDialog.setLayout(new GridBagLayout());

                GridBagConstraints gbc = new GridBagConstraints();
                gbc.insets = new Insets(10, 10, 10, 10);
                gbc.fill = GridBagConstraints.HORIZONTAL;

                // 当前密码标签和文本框
                JLabel currentPasswordLabel = new JLabel("Current Password:");
                currentPasswordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                gbc.gridx = 0;
                gbc.gridy = 0;
                changePasswordDialog.add(currentPasswordLabel, gbc);

                JPasswordField currentPasswordText = new JPasswordField(20);
                currentPasswordText.setFont(new Font("Arial", Font.PLAIN, 14));
                gbc.gridx = 1;
                gbc.gridy = 0;
                gbc.ipady = 10; // 增加输入框高度
                changePasswordDialog.add(currentPasswordText, gbc);

                // 新密码标签和文本框
                JLabel newPasswordLabel = new JLabel("New Password:");
                newPasswordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                gbc.gridx = 0;
                gbc.gridy = 1;
                changePasswordDialog.add(newPasswordLabel, gbc);

                JPasswordField newPasswordText = new JPasswordField(20);
                newPasswordText.setFont(new Font("Arial", Font.PLAIN, 14));
                gbc.gridx = 1;
                gbc.gridy = 1;
                gbc.ipady = 10; // 增加输入框高度
                changePasswordDialog.add(newPasswordText, gbc);

                // 确认按钮
                JButton confirmButton = new JButton("Confirm");
                confirmButton.setFont(new Font("Arial", Font.PLAIN, 14));
                gbc.gridx = 0;
                gbc.gridy = 2;
                gbc.gridwidth = 2;
                gbc.ipady = 10; // 增加按钮高度
                gbc.ipadx = 30; // 增加按钮宽度
                changePasswordDialog.add(confirmButton, gbc);

                // 确认按钮的点击事件
                confirmButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        // 获取当前密码和新密码的值
                        String currentPassword = new String(currentPasswordText.getPassword());
                        String newPassword = new String(newPasswordText.getPassword());
                        if(currentPassword.equals(passwordText.getText())) {
                            ClientController c = null;
                            try {
                                c = new ClientController();
                                List<Serializable>temp=new ArrayList<>();
                                temp.add(cardId);
                                temp.add(newPassword);
                                Message m = new Message(Message.MessageType.change_password,temp);
                                c.sendMessage(m);
                                Message r = c.receiveMessage();
                                if(r.getType()== Message.MessageType.success)
                                    JOptionPane.showMessageDialog(changePasswordDialog, "Password changed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                                else
                                    JOptionPane.showMessageDialog(changePasswordDialog, "Failed to change password. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            } catch (ClassNotFoundException ex) {
                                throw new RuntimeException(ex);
                            }
                        }

                        // 关闭对话框
                        changePasswordDialog.dispose();
                    }
                });

                // 显示对话框
                changePasswordDialog.setLocationRelativeTo(frame);
                changePasswordDialog.setVisible(true);
            }
        });


        // 挂失按钮
        JButton reportLossButton = new JButton("Report Loss");
        reportLossButton.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.ipady = 15; // 增加按钮高度
        gbc.ipadx = 30; // 增加按钮宽度
        panelMain.add(reportLossButton, gbc);

        reportLossButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ClientController c = null;
                try {
                    c = new ClientController();
                    List<Serializable>temp=new ArrayList<>();
                    temp.add(cardId);
                    Message m = new Message(Message.MessageType.report_loss,temp);
                    c.sendMessage(m);
                    Message r = c.receiveMessage();
                    if(r.getType()== Message.MessageType.success)
                        JOptionPane.showMessageDialog(frame, "Success", "Report Loss", JOptionPane.INFORMATION_MESSAGE);
                    else
                        JOptionPane.showMessageDialog(frame, "Failure", "Report Loss", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                } catch (ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        // 设置可见性
        frame.setVisible(true);
    }

    private void showQRCode(byte[] qrCodeImage, double deposit) {
        try {
            // 将 byte[] 转换为 BufferedImage
            ByteArrayInputStream bis = new ByteArrayInputStream(qrCodeImage);
            BufferedImage bufferedImage = ImageIO.read(bis);

            // 创建一个 JFrame 用于显示二维码
            JFrame qrFrame = new JFrame("二维码");
            qrFrame.setSize(300, 300);
            qrFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            // 将 BufferedImage 放入 JLabel 并添加到 JFrame
            JLabel qrLabel = new JLabel(new ImageIcon(bufferedImage));
            qrFrame.getContentPane().add(qrLabel, BorderLayout.CENTER);

            qrFrame.addWindowListener(new WindowAdapter() {

                public void windowClosing(WindowEvent e) {
                    // 在窗口关闭时执行的操作
                    System.out.println("二维码窗口已关闭");
                    ClientController c;
                    try {
                        c = new ClientController();
                        Message m = new Message(Message.MessageType.deposit);
                        c.sendMessage(m);
                        Message r = c.receiveMessage();
                        if(r.getType()== Message.MessageType.success)
                        {
                            System.out.println("111");
                            balance+=deposit;
                            updateBalance();
                        }
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    } catch (ClassNotFoundException ex) {
                        throw new RuntimeException(ex);
                    }

                }

            });
            // 显示窗口
            qrFrame.setVisible(true);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "无法显示二维码", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void updateBalance() {
        balanceLabel.setText("Balance: $" + balance);
    }

    public static void main(String[] args) {
        new ATMUI();
    }

    @Override
    public JPanel getPanel() {
        return null;
    }
}*/


package Server;

import Server.Public.ClientController;
import Server.Public.ContentPanel;
import Server.Public.Message;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ATMUI extends JPanel implements ContentPanel {
    private JPanel panelLogin, panelMain;
    private JTextField userText, depositText, withdrawText;
    private JPasswordField passwordText;
    private JLabel balanceLabel;
    private double balance = 0.0;
    private String cardId;

    public ATMUI() {
        setLayout(new BorderLayout());
        initialize();
    }

    private void initialize() {
        // Setting up Login Panel with GridBagLayout
        panelLogin = new JPanel(new GridBagLayout());
        add(panelLogin, BorderLayout.CENTER);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15); // Padding
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username label and input field
        JLabel userLabel = new JLabel("CardId:");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelLogin.add(userLabel, gbc);

        userText = new JTextField(20);
        userText.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.ipady = 10; // Increase input field height
        panelLogin.add(userText, gbc);

        // Password label and input field
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panelLogin.add(passwordLabel, gbc);

        passwordText = new JPasswordField(20);
        passwordText.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.ipady = 10; // Increase input field height
        panelLogin.add(passwordText, gbc);

        // Login button
        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.ipady = 15; // Increase button height
        gbc.ipadx = 30; // Increase button width
        panelLogin.add(loginButton, gbc);

        // Login button event handling
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    if (authenticate(userText.getText(), new String(passwordText.getPassword()))) {
                        cardId = userText.getText();
                        getBalance();
                        showMainPanel();
                    } else {
                        JOptionPane.showMessageDialog(ATMUI.this, "Invalid credentials", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (IOException | ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    private void getBalance() throws IOException, ClassNotFoundException {
        ClientController c = new ClientController();
        List<Serializable> temp = new ArrayList<>();
        temp.add(cardId);
        Message m = new Message(Message.MessageType.check_balance, temp);
        c.sendMessage(m);
        Message r = c.receiveMessage();
        if (r.getType() == Message.MessageType.success)
            balance = (double) r.getContent().get(0);
    }

    private boolean authenticate(String user, String password) throws IOException, ClassNotFoundException {
        ClientController c = new ClientController();
        List<Serializable> temp = new ArrayList<>();
        temp.add(user);
        temp.add(password);
        Message m = new Message(Message.MessageType.atm_login, temp);
        c.sendMessage(m);
        Message r = c.receiveMessage();
        return r.getType() == Message.MessageType.success;
    }

    private void showMainPanel() {
        panelLogin.setVisible(false);

        // Main panel with deposit and withdraw functionalities
        panelMain = new JPanel(new GridBagLayout());
        add(panelMain, BorderLayout.CENTER);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15); // Padding
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Balance label
        balanceLabel = new JLabel("余额: CN¥ " + balance);
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.ipady = 20; // Increase label height
        panelMain.add(balanceLabel, gbc);

        // Deposit input and button
        JLabel depositLabel = new JLabel("Deposit Amount:");
        depositLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panelMain.add(depositLabel, gbc);

        depositText = new JTextField(10);
        depositText.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.ipady = 10; // Increase input field height
        panelMain.add(depositText, gbc);

        JButton depositButton = new JButton("Deposit");
        depositButton.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.ipady = 15; // Increase button height
        gbc.ipadx = 30; // Increase button width
        panelMain.add(depositButton, gbc);

        depositButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleDeposit();
            }
        });

        // Withdraw input and button
        JLabel withdrawLabel = new JLabel("Withdraw Amount:");
        withdrawLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridx = 0;
        gbc.gridy = 2;
        panelMain.add(withdrawLabel, gbc);

        withdrawText = new JTextField(10);
        withdrawText.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.ipady = 10; // Increase input field height
        panelMain.add(withdrawText, gbc);

        JButton withdrawButton = new JButton("Withdraw");
        withdrawButton.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.ipady = 15; // Increase button height
        gbc.ipadx = 30; // Increase button width
        panelMain.add(withdrawButton, gbc);

        withdrawButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleWithdraw();
            }
        });

        // Other buttons like Change Password and Report Loss can be added similarly

        panelMain.setVisible(true);
        revalidate();
        repaint();
    }

    private void handleDeposit() {
        String amount = depositText.getText();
        if (amount != null && !amount.isEmpty()) {
            try {
                double deposit = Double.parseDouble(amount);
                depositText.setText("");
                if (deposit > 0) {
                    ClientController c = new ClientController();
                    List<Serializable> temp = new ArrayList<>();
                    temp.add(cardId);
                    temp.add(deposit);
                    Message m = new Message(Message.MessageType.deposit_2, temp);
                    c.sendMessage(m);
                    Message r = c.receiveMessage();
                    if (r.getType() == Message.MessageType.showQR) {
                        byte[] qrCodeImage = (byte[]) r.getContent().get(0); // Get byte array
                        showQRCode(qrCodeImage, deposit);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Enter a positive amount", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid amount", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (ClassNotFoundException | IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private void handleWithdraw() {
        String amount = withdrawText.getText();
        if (amount != null && !amount.isEmpty()) {
            try {
                double withdraw = Double.parseDouble(amount);
                withdrawText.setText("");
                if (withdraw > 0 && withdraw <= balance) {
                    ClientController c = new ClientController();
                    List<Serializable> temp = new ArrayList<>();
                    temp.add(cardId);
                    temp.add(withdraw);
                    Message m = new Message(Message.MessageType.withdraw, temp);
                    c.sendMessage(m);
                    Message r = c.receiveMessage();
                    if (r.getType() == Message.MessageType.success) {
                        balance -= withdraw;
                        updateBalance();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Enter a valid amount", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid amount", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (ClassNotFoundException | IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private void showQRCode(byte[] qrCodeImage, double deposit) {
        try {
            // Convert byte[] to BufferedImage
            ByteArrayInputStream bis = new ByteArrayInputStream(qrCodeImage);
            BufferedImage bufferedImage = ImageIO.read(bis);

            // Create a JFrame to display QR code
            JFrame qrFrame = new JFrame("QR Code");
            qrFrame.setSize(300, 300);
            qrFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            // Add BufferedImage to JLabel and JFrame
            JLabel qrLabel = new JLabel(new ImageIcon(bufferedImage));
            qrFrame.getContentPane().add(qrLabel, BorderLayout.CENTER);

            qrFrame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    // Actions on window close
                    System.out.println("QR Code window closed");
                    ClientController c;
                    try {
                        c = new ClientController();
                        Message m = new Message(Message.MessageType.deposit);
                        c.sendMessage(m);
                        Message r = c.receiveMessage();
                        if (r.getType() == Message.MessageType.success) {
                            balance += deposit;
                            updateBalance();
                        }
                    } catch (IOException | ClassNotFoundException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });

            // Display window
            qrFrame.setVisible(true);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Unable to display QR code", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void updateBalance() {
        balanceLabel.setText("Balance: CN¥ " + balance);
    }

    @Override
    public JPanel getPanel() {
        return this;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("ATMUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setContentPane(new ATMUI());
        frame.setVisible(true);
    }
}

