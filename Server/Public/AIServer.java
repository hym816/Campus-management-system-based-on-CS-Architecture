package Server.Public;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class AIServer {

    public static void main(String[] args) {
        // 创建并显示GUI窗口
        SwingUtilities.invokeLater(AIServer::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("AIServer 启动");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);
        frame.setLayout(new GridLayout(3, 1));

        try {
            // 获取本地网络IP地址
            String ipAddress = InetAddress.getLocalHost().getHostAddress();
            JLabel ipLabel = new JLabel("服务器IP地址: " + ipAddress);
            frame.add(ipLabel);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "无法获取本地IP地址", "错误", JOptionPane.ERROR_MESSAGE);
        }

        JTextField portField = new JTextField("11435");
        frame.add(portField);

        JButton startButton = new JButton("启动服务器");
        startButton.addActionListener(e -> {
            String portText = portField.getText();
            try {
                int port = Integer.parseInt(portText);
                startServer(port);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "请输入有效的端口号", "错误", JOptionPane.ERROR_MESSAGE);
            }
        });
        frame.add(startButton);

        frame.setVisible(true);
    }

    private static void startServer(int port) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);
            server.createContext("/api/chat", new ChatHandler());
            server.setExecutor(null);
            server.start();
            System.out.println("Server started on port " + port);
            JOptionPane.showMessageDialog(null, "服务器已启动，端口号：" + port, "信息", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "无法启动服务器: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    static class ChatHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                System.out.println("Received a POST request from: " + exchange.getRemoteAddress());

                InputStream inputStream = exchange.getRequestBody();
                String requestBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                System.out.println("Request body: " + requestBody);

                exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                exchange.sendResponseHeaders(200, 0); // 使用分块传输编码

                try (OutputStream os = exchange.getResponseBody()) {
                    callModelService(requestBody, os);
                }
            } else {
                exchange.sendResponseHeaders(405, -1);
                System.out.println("Received a non-POST request from: " + exchange.getRemoteAddress());
            }
        }

        private void callModelService(String requestBody, OutputStream os) throws IOException {
            URL url = new URL("http://localhost:11434/api/chat"); // 注意：替换为实际的模型服务URL
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setDoOutput(true);

            // 发送请求到模型服务
            try (OutputStream modelOutput = connection.getOutputStream()) {
                byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
                modelOutput.write(input, 0, input.length);
            }

            // 读取模型服务的响应并逐步发送给客户端
            try (InputStream responseStream = connection.getInputStream()) {
                byte[] buffer = new byte[1024]; // 每次读取1KB的数据
                int bytesRead;
                while ((bytesRead = responseStream.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                    os.flush(); // 刷新缓冲区以确保数据立即发送
                }
            }
        }
    }
}
