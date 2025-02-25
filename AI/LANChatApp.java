package Server.AI;

import Server.Public.ContentPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

public class LANChatApp extends JPanel implements ContentPanel {
    private JList<String> userList; // 左侧用户列表
    private DefaultListModel<String> listModel;
    private LlamaComponent llamaComponent; // 右侧聊天窗口组件
    private JTextArea chatArea; // 显示聊天记录
    private String selectedIP = null; // 当前选择的IP地址
    private ServerSocket serverSocket;
    private int serverPort; // 动态分配的服务器端口
    private final Set<String> discoveredUsers = new HashSet<>(); // 记录已发现的用户
    private static final int BROADCAST_PORT = 8888; // UDP广播端口
    private JPanel panel; // 定义panel

    public LANChatApp() {
        // 初始化主面板
        panel = new JPanel(new BorderLayout());
        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);

        // 初始化用户列表
        listModel = new DefaultListModel<>();
        userList = new JList<>(listModel);
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane userScrollPane = new JScrollPane(userList);
        panel.add(userScrollPane, BorderLayout.WEST);
        userScrollPane.setPreferredSize(new Dimension(200, 0));

        // 初始化聊天窗口组件
        llamaComponent = new LlamaComponent();
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        JPanel chatPanel = new JPanel(new BorderLayout());
        chatPanel.add(chatScrollPane, BorderLayout.CENTER);
        chatPanel.add(llamaComponent, BorderLayout.SOUTH);
        panel.add(chatPanel, BorderLayout.CENTER);

        // 双击用户列表项以开始聊天
        userList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    selectedIP = userList.getSelectedValue();
                    if (selectedIP != null) {
                        chatArea.append("开始与 " + selectedIP + " 的聊天...\n");
                    }
                }
            }
        });

        // 发送消息
        JButton sendButton = new JButton("发送");
        llamaComponent.add(sendButton, BorderLayout.EAST);
        sendButton.addActionListener(e -> sendMessage());

        // 启动服务器以接收消息
        startServer();

        // 启动发现和广播用户的线程
        startBroadcastListener();
        startBroadcastSender();
    }

    // 启动服务器以接收消息
    private void startServer() {
        try {
            serverSocket = new ServerSocket(0); // 动态分配一个可用端口
            serverPort = serverSocket.getLocalPort(); // 获取动态分配的端口号
            System.out.println("服务器已启动，使用端口: " + serverPort);

            new Thread(() -> {
                while (true) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        String message = in.readLine();
                        chatArea.append("收到消息：" + message + "\n");
                        clientSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 发送消息到选定的IP地址
    private void sendMessage() {
        if (selectedIP == null) {
            JOptionPane.showMessageDialog(this, "请选择一个用户以发送消息。");
            return;
        }

        // 拆分 IP 和端口
        String[] ipPort = selectedIP.split(":");
        if (ipPort.length != 2) {
            chatArea.append("无效的 IP 格式: " + selectedIP + "\n");
            return;
        }

        String ipAddress = ipPort[0];
        int port;
        try {
            port = Integer.parseInt(ipPort[1]);
        } catch (NumberFormatException e) {
            chatArea.append("无效的端口号: " + ipPort[1] + "\n");
            return;
        }

        String message = llamaComponent.getTextArea().getText();
        if (message.isEmpty()) return;

        try (Socket socket = new Socket(ipAddress, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            out.println(message);
            chatArea.append("发送至 " + ipAddress + ": " + message + "\n");
            llamaComponent.getTextArea().setText("");
        } catch (IOException e) {
            e.printStackTrace();
            chatArea.append("发送失败: " + e.getMessage() + "\n");
        }
    }

    // 启动广播监听器以接收来自其他客户端的广播
    private void startBroadcastListener() {
        new Thread(() -> {
            try (DatagramSocket socket = new DatagramSocket(BROADCAST_PORT, InetAddress.getByName("0.0.0.0"))) {
                socket.setBroadcast(true);
                while (true) {
                    byte[] buffer = new byte[256];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);
                    String message = new String(packet.getData(), 0, packet.getLength());
                    if (!discoveredUsers.contains(message) && !message.equals(InetAddress.getLocalHost().getHostAddress() + ":" + serverPort)) {
                        discoveredUsers.add(message);
                        SwingUtilities.invokeLater(() -> {
                            listModel.clear();
                            discoveredUsers.forEach(listModel::addElement);
                        });
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    // 启动广播发送器以广播当前客户端的信息
    private void startBroadcastSender() {
        new Thread(() -> {
            try {
                DatagramSocket socket = new DatagramSocket();
                socket.setBroadcast(true);

                while (true) {
                    // 每隔1秒广播一次
                    Thread.sleep(1000);

                    // 获取所有的网络接口
                    Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
                    while (interfaces.hasMoreElements()) {
                        NetworkInterface networkInterface = interfaces.nextElement();

                        // 跳过不活动或环回接口
                        if (!networkInterface.isUp() || networkInterface.isLoopback()) {
                            continue;
                        }

                        // 获取接口的所有IP
                        for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                            InetAddress broadcast = interfaceAddress.getBroadcast();

                            // 检查是否有有效的广播地址
                            if (broadcast == null) {
                                continue;
                            }

                            InetAddress localAddress = interfaceAddress.getAddress();

                            // 构造广播消息，只使用局域网 IP 地址，而不是回环地址
                            if (localAddress instanceof Inet4Address && !localAddress.isLoopbackAddress()) {
                                String message = localAddress.getHostAddress() + ":" + serverPort;
                                byte[] buffer = message.getBytes();
                                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, broadcast, BROADCAST_PORT);

                                // 发送广播包
                                socket.send(packet);
                                System.out.println("广播消息: " + message + " 到 " + broadcast.getHostAddress());
                            }
                        }
                    }
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("局域网聊天");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new LANChatApp());
            frame.setVisible(true);
        });
    }

    @Override
    public JPanel getPanel() {
        return panel;
    }
}
