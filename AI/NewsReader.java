package Server.AI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.border.EmptyBorder;

public class NewsReader extends JFrame {
    private JTextArea newsContentArea;
    private JTextArea summaryArea;
    private JPanel summaryPanel;
    private JButton summaryButton;
    private JButton hideButton;
    private Llama llama = new Llama(); // 使用之前定义的 Llama 类

    // 构造函数，传入标题、作者、正文内容
    public NewsReader(String title, String author, String content) {
        setTitle("新闻阅读器");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 使用 DISPOSE_ON_CLOSE 关闭单独的阅读窗口
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 外层面板设置上下左右的间距
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20)); // 设置上下左右各20像素的边距
        add(mainPanel, BorderLayout.CENTER);

        // 顶部面板包含标题和作者
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(2, 1));

        JLabel titleLabel = new JLabel("新闻标题：" + title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(titleLabel);

        JLabel authorLabel = new JLabel("作者：" + author);
        authorLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        topPanel.add(authorLabel);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // 主面板包含新闻正文
        newsContentArea = new JTextArea(10, 40);
        newsContentArea.setText(content);
        newsContentArea.setLineWrap(true);
        newsContentArea.setWrapStyleWord(true);
        newsContentArea.setEditable(false);
        JScrollPane contentScrollPane = new JScrollPane(newsContentArea);
        mainPanel.add(contentScrollPane, BorderLayout.CENTER);

        // 右侧面板包含总结区域和隐藏按钮
        summaryPanel = new JPanel();
        summaryPanel.setLayout(new BorderLayout());
        summaryPanel.setPreferredSize(new Dimension(300, 0));

        // 总结显示区域
        summaryArea = new JTextArea();
        summaryArea.setEditable(false);
        summaryArea.setLineWrap(true);
        summaryArea.setWrapStyleWord(true);
        JScrollPane summaryScrollPane = new JScrollPane(summaryArea);
        summaryPanel.add(summaryScrollPane, BorderLayout.CENTER);
        summaryPanel.setVisible(false); // 初始隐藏

        mainPanel.add(summaryPanel, BorderLayout.EAST);

        // 底部面板包含按钮
        JPanel buttonPanel = new JPanel();
        summaryButton = new JButton("智能总结");
        hideButton = new JButton("隐藏");

        buttonPanel.add(summaryButton);
        buttonPanel.add(hideButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // 智能总结按钮的事件处理
        summaryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String articleContent = newsContentArea.getText();
                llama.generateSummary(articleContent, summary -> {
                    SwingUtilities.invokeLater(() -> {
                        summaryArea.setText("智能总结：\n" + summary);
                        summaryPanel.setVisible(true);
                    });
                });
            }
        });

        // 隐藏按钮的事件处理
        hideButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                summaryPanel.setVisible(false);
            }
        });
    }
}
