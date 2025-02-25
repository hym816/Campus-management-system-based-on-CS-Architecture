package Server.AI;

import Server.Public.ContentPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import Server.Public.textField;

public class ChatUI implements ContentPanel {
    private JPanel panel;
    private JTextArea chatArea;
    private textField inputField;
    private Llama llama;
    private boolean isFirstResponse;

    public ChatUI() {
        llama = new Llama();
        panel = new JPanel(new BorderLayout());
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        isFirstResponse = true;
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // 设置四周20像素的边距
        inputField = new textField(100);
        inputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userInput = inputField.getText();
                chatArea.append("You: " + userInput + "\n");
                inputField.setText("");
                isFirstResponse = true; // 重置标志以应对新的输入
                llama.sendMessage(userInput, content -> {
                    SwingUtilities.invokeLater(() -> {
                        if (isFirstResponse) {
                            chatArea.append("Llama: ");
                            isFirstResponse = false;
                        }
                        chatArea.replaceRange(content, chatArea.getText().lastIndexOf("Llama:") + 7, chatArea.getText().length());
                    });
                });
            }
        });

        // 新增的“开启新会话”按钮
        JButton newSessionButton = new JButton("开启新会话");
        newSessionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chatArea.setText(""); // 清空聊天区域
                isFirstResponse = true; // 重置首次响应标志
            }
        });

        // 将新按钮添加到面板顶部
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(newSessionButton, BorderLayout.WEST);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(chatArea), BorderLayout.CENTER);
        panel.add(inputField, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Llama Chat");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 300);
            frame.add(new ChatUI().getPanel());
            frame.setVisible(true);
        });
    }

    @Override
    public JPanel getPanel() {
        return panel;
    }
}
