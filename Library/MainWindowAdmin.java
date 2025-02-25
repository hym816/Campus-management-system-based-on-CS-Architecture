package Server.Library;


import Server.MenuButton;
import Server.Public.ContentPanel;
import ui.reviseInfo;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class MainWindowAdmin extends JFrame {
    private JPanel contentPanel;
    private Map<String, ContentPanel> panels; // 存储各个内容面板的映射



    public MainWindowAdmin(String id) {

        // 设置窗口标题和大小
        setTitle("图书馆");
        setSize(1000,    600);
        setResizable(true); // 可以调整窗口大小
        setLayout(new BorderLayout());

        // 创建左侧菜单面板
        JPanel menuPanel = new JPanel();
        menuPanel.setPreferredSize(new Dimension(200, getHeight()));
        menuPanel.setLayout(new BorderLayout());
        menuPanel.setBackground(new Color(220, 220, 220)); // 设置左侧菜单区背景为浅灰色

        // 创建按钮容器，并设置为垂直布局
        JPanel buttonContainer = new JPanel();
        buttonContainer.setLayout(new BoxLayout(buttonContainer, BoxLayout.Y_AXIS));
        buttonContainer.setOpaque(false); // 使按钮容器透明，使用父面板的背景颜色

        // 创建右侧内容面板
        contentPanel = new JPanel(new CardLayout()); // 使用 CardLayout 切换内容


        // 初始化内容面板映射
        panels = new HashMap<>();

        panels.put("管理主页", new homePageAdmin());
        panels.put("搜索书籍", new searchTest(id));
        panels.put("我的借阅", new personalPanel(id));
        panels.put("热门书籍", new popularBook(id));
        panels.put("最佳读者", new bestReader());
        panels.put("读者荐购", new recommendPanel());
        panels.put("搜索失败", new searchFailure());
        panels.put("添加书籍", new addBook());
        panels.put("修改信息", new reviseInfo());
        panels.put("荐购处理", new recommendDispose());
        panels.put("修改失败", new reviseFailure());



        MenuButton button3 = createMenuButton("热门书籍");
        MenuButton button4 = createMenuButton("最佳读者");

        MenuButton button6 = createMenuButton("添加书籍");
        MenuButton button7 = createMenuButton("荐购处理");
        MenuButton button8 = createMenuButton("管理主页");
        MenuButton button9 = createMenuButton("修改信息");



        // 将按钮添加到按钮容器

        buttonContainer.add(Box.createVerticalStrut(20)); // 添加20像素的空白
        buttonContainer.add(button8);

        buttonContainer.add(Box.createVerticalStrut(20)); // 添加间隔
        buttonContainer.add(button9);

        buttonContainer.add(Box.createVerticalStrut(20)); // 添加间隔
        buttonContainer.add(button3);
        buttonContainer.add(Box.createVerticalStrut(20)); // 添加间隔
        buttonContainer.add(button4);

        buttonContainer.add(Box.createVerticalStrut(20)); // 添加间隔
        buttonContainer.add(button6);
        buttonContainer.add(Box.createVerticalStrut(20)); // 添加间隔
        buttonContainer.add(button7);




        // 将按钮容器添加到左侧菜单面板并居中对齐.
        menuPanel.add(buttonContainer, BorderLayout.NORTH);
        // 将每个内容面板添加到右侧内容区域
        for (Map.Entry<String, ContentPanel> entry : panels.entrySet()) {
            contentPanel.add(entry.getValue().getPanel(), entry.getKey());
        }

        // 将菜单面板和内容面板添加到主窗口
        add(menuPanel, BorderLayout.WEST);       //将menuPanel放在左侧
        add(contentPanel, BorderLayout.CENTER);  //将contentPanel放在中间

        switchContent("管理主页");          //默认点击进去的是搜索书籍的界面
        setVisible(true);                        //界面可视
    }


    // 工具方法：创建菜单按钮并设置固定宽度
    private MenuButton createMenuButton(String text) {
        MenuButton button = new MenuButton(text,null);
        button.setMaximumSize(new Dimension(175, 50)); // 设置按钮宽度为175，高度为50
        button.setAlignmentX(Component.CENTER_ALIGNMENT); // 水平方向居中对齐

        // 添加按钮的点击事件
        button.addActionListener(e -> switchContent(text));
        return button;
    }

    // 切换内容面板，切换到option面板
    public void switchContent(String option) {
        CardLayout layout = (CardLayout) contentPanel.getLayout();
        layout.show(contentPanel, option);
    }

    // 程序入口
    public static void main(String[] args) {
        // 创建窗口实例，并传入一个示例ID（可以根据实际需求修改）
        MainWindowAdmin mainWindow = new MainWindowAdmin("userID123");

        // 设置窗口可见
        mainWindow.setVisible(true);
    }

}
