package Server.Library;

import javax.swing.*;
import java.awt.*;
import Server.Public.ContentPanel;

public class homePageAdmin implements ContentPanel {

    private JPanel panel;

    public homePageAdmin() {


        panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10)); // 使用 BorderLayout 并设置间距

        // 创建自定义圆角面板
        RoundedPanel containerPanel = new RoundedPanel(20, new Color(200, 200, 200)); // 设置圆角和灰度颜色
        containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.Y_AXIS)); // 使用 BoxLayout 以垂直布局
        containerPanel.setPreferredSize(new Dimension(680, 130)); // 调整尺寸以留出边距
        containerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // 设置内边距
        // 创建标签并设置换行
        JLabel greetingLabel1 = new JLabel("您好!");
        JLabel greetingLabel2 = new JLabel("<html>欢迎使用图书管理系统。<br>您的身份是：管理员</html>"); // 使用 HTML 进行换行
        greetingLabel1.setFont(new Font("黑体", Font.PLAIN, 30));
        greetingLabel2.setFont(new Font("黑体", Font.PLAIN, 20));

        // 添加标签到圆角面板
        containerPanel.add(greetingLabel1);
        containerPanel.add(greetingLabel2);

        // 在主面板的顶部添加圆角面板，并设置合适的边距
        panel.add(containerPanel, BorderLayout.NORTH);

        // 创建透明的容器放置新的版块，使用 BoxLayout 垂直排列
        JPanel transparentPanel = new JPanel();
        transparentPanel.setLayout(new BoxLayout(transparentPanel, BoxLayout.Y_AXIS)); // 使用 BoxLayout 垂直排列
        transparentPanel.setOpaque(false); // 设为透明

        // 添加各个模块并设置间距
        transparentPanel.add(createSectionPanel("修改信息", "根据已知信息检索书籍，并进行修改和删除操作"));
        transparentPanel.add(Box.createRigidArea(new Dimension(0, 15))); // 增加模块间的间隔

        transparentPanel.add(createSectionPanel("热门书籍", "可以查询到借阅次数最多的书籍，并在此界面进行借阅"));
        transparentPanel.add(Box.createRigidArea(new Dimension(0, 15))); // 增加模块间的间隔

        transparentPanel.add(createSectionPanel("最佳读者", "展示借阅次数最多的读者信息"));
        transparentPanel.add(Box.createRigidArea(new Dimension(0, 15))); // 增加模块间的间隔

        transparentPanel.add(createSectionPanel("荐购处理", "处理学生提交的荐购信息"));

        // 在主面板的中间添加透明面板
        JScrollPane scrollPane = new JScrollPane(transparentPanel); // 使用 JScrollPane 来确保边界始终可见
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // 去除滚动面板的边框
        panel.add(scrollPane, BorderLayout.CENTER); // 添加到面板中

        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // 设置主面板的内边距
        panel.setVisible(true);


    }

    @Override
    public JPanel getPanel() {
        return panel;
    }


    private static JPanel createSectionPanel(String title, String content) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(new Color(230, 230, 230)); // 设置浅灰色背景
        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1)); // 设置灰色矩形边框

        // 创建标题标签
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("黑体", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 0)); // 设置标题与左边的间距
        panel.add(titleLabel, BorderLayout.NORTH);

        // 创建内容标签
        JTextArea contentArea = new JTextArea(content);
        contentArea.setFont(new Font("黑体", Font.PLAIN, 15)); // 调小字体
        contentArea.setOpaque(false); // 设为透明
        contentArea.setEditable(false); // 不允许编辑
        contentArea.setLineWrap(true); // 自动换行
        contentArea.setWrapStyleWord(true); // 只在单词边界处换行
        contentArea.setMargin(new Insets(5, 10, 5, 10)); // 设置内边距

        panel.add(contentArea, BorderLayout.CENTER); // 将内容区域添加到面板中

        return panel;
    }

}

