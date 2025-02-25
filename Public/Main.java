package Server.Public;


import Server.Login.LoginPage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;

public class Main {
    private Socket socket;

    // 连接到服务器的方法
    public boolean connectToServer(String serverIp, int port) {
        try {
            socket = new Socket(serverIp, port);
            System.out.println("Connected to server successfully!");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 创建GUI
    private void createAndShowGUI() {
        JFrame frame = new JFrame("Server Connection");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 150);
        frame.setLayout(new GridLayout(3, 2));

        JTextField ipField = new JTextField("127.0.0.1"); // 默认IP地址
        JTextField portField = new JTextField("8082"); // 默认端口

        frame.add(new JLabel("Server IP:"));
        frame.add(ipField);
        frame.add(new JLabel("Port:"));
        frame.add(portField);

        JButton connectButton = new JButton("Connect");
        frame.add(connectButton);

        JLabel statusLabel = new JLabel("Not connected");
        frame.add(statusLabel);

        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String serverIp = ipField.getText();
                int port = Integer.parseInt(portField.getText());

                // 在新线程中进行服务器连接，避免阻塞 GUI
                new Thread(() -> {
                    if (connectToServer(serverIp, port)) {
                        SwingUtilities.invokeLater(() -> {
                            statusLabel.setText("Connected successfully");
                            // 连接成功后初始化 LoginPage
                            try {
                                new LoginPage(new ClientController());
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                        });
                    } else {
                        SwingUtilities.invokeLater(() -> statusLabel.setText("Connection failed"));
                    }
                }).start();
            }
        });

        frame.setVisible(true);
    }

    public static void main(String[] args) {
        ClientController client = null;
        try {
            client = new ClientController();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        SwingUtilities.invokeLater(client::createAndShowGUI); // 启动GUI
    }
}
