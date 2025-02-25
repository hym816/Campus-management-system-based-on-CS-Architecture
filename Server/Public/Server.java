package Server.Public;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final int PORT = 8082;
    private ExecutorService executor;
    public static String sqlip = "172.20.10.10";
    public static String user = "usr";
    public static String password = "000000-Aa";

    public Server() {
        executor = Executors.newCachedThreadPool();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started. Waiting for clients...");

            while (!Thread.currentThread().isInterrupted()) {
                Socket socket = serverSocket.accept();
                System.out.println("Client connected: " + socket.getInetAddress().getHostAddress());

                // 为每个客户端连接创建一个新的线程
                executor.execute(new ClientHandler(socket));
            }
        } catch (IOException e) {
            if (!Thread.currentThread().isInterrupted()) {
                e.printStackTrace();
            }
        } finally {
            executor.shutdown();
        }
    }

    // 连接数据库的方法
    public boolean connectDatabase(String ip, String user, String password) {
        String url = "jdbc:mysql://" + ip + ":3306/my_database2"; // 修改为实际的数据库URL
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            System.out.println("Database connected successfully!");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 创建GUI
    private void createAndShowGUI() {
        JFrame frame = new JFrame("Database Connection");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);
        frame.setLayout(new GridLayout(5, 2));

        JTextField ipField = new JTextField(sqlip);
        JTextField userField = new JTextField(user);
        JPasswordField passwordField = new JPasswordField(password);
        JTextField portField = new JTextField(String.valueOf(PORT));

        frame.add(new JLabel("SQL IP:"));
        frame.add(ipField);
        frame.add(new JLabel("User:"));
        frame.add(userField);
        frame.add(new JLabel("Password:"));
        frame.add(passwordField);
        frame.add(new JLabel("Port:"));
        frame.add(portField);

        JButton connectButton = new JButton("Connect");
        frame.add(connectButton);

        JLabel statusLabel = new JLabel("Not connected");
        frame.add(statusLabel);

        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String ip = ipField.getText();
                String user = userField.getText();
                String password = new String(passwordField.getPassword());

                // 在新线程中进行数据库连接和服务器启动
                new Thread(() -> {
                    if (connectDatabase(ip, user, password)) {
                        SwingUtilities.invokeLater(() -> statusLabel.setText("Connected successfully"));
                        // 在新线程中启动服务器，避免阻塞 GUI
                        new Thread(Server.this::start).start();
                    } else {
                        SwingUtilities.invokeLater(() -> statusLabel.setText("Connection failed"));
                    }
                }).start();
            }
        });

        frame.setVisible(true);
    }

    public static void main(String[] args) {
        Server server = new Server();
        SwingUtilities.invokeLater(server::createAndShowGUI); // 启动GUI
    }
}
