package Server.Library;

import Server.Public.ContentPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import Server.Public.IconUtils;

// EnterLibPanel 实现了 ContentPanel 接口
public class EnterLibPanel implements ContentPanel {

    private JPanel panel;

    public EnterLibPanel(String id, int flag) {
        // 创建主面板
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20)); // 设置四周20像素的安全区域

        // 创建标题标签
        JLabel titleLabel = new JLabel("图书馆入口");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 16)); // 设置字体样式和大小
        titleLabel.setHorizontalAlignment(SwingConstants.LEFT); // 左对齐

        // 添加标题到页面左上角
        panel.add(titleLabel, BorderLayout.NORTH);

        // 创建“进入图书馆”按钮
        JButton enterButton = new JButton("进入图书馆");
        enterButton.setFont(new Font("微软雅黑", Font.PLAIN, 14)); // 设置按钮字体样式和大小

        // 添加按钮的点击事件监听
        enterButton.addActionListener(e -> {

            if (flag == 1 || flag == 2) {
                // 当按钮被点击时，显示 MainWindowStu 窗口
                MainWindowStu mainWindow = new MainWindowStu(id);
                mainWindow.setVisible(true);
            } else {
                MainWindowAdmin mainWindow = new MainWindowAdmin(id);
                mainWindow.setVisible(true);
            }
        });

        // 创建图片标签
        ImageIcon imageIcon = loadIcon("0.jpg", 800);

        JLabel imageLabel = new JLabel(imageIcon);

        // 创建一个中间面板，将图片标签添加到面板中央
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(imageLabel, BorderLayout.CENTER);

        // 将按钮添加到中心面板的下方
        centerPanel.add(enterButton, BorderLayout.SOUTH);

        // 将中间面板添加到主面板中心
        panel.add(centerPanel, BorderLayout.CENTER);
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
    // 实现 ContentPanel 接口的方法，返回主面板
    @Override
    public JPanel getPanel() {
        return panel;
    }
}
