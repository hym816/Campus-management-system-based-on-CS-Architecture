package Server;

import Server.AI.LANChatApp;
import Server.Class.*;
import Server.Login.UserInfoPage;
import Server.Public.ContentPanel;
import Server.Public.ClientController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import Server.AI.ChatUI;
import Server.AI.NewsListPanel;
import Server.Public.Message;
import Server.Store.ShopManageUI;
import Server.Store.ShopUI;
import Server.Library.EnterLibPanel;


public class MainWindow extends JFrame /*implements LoginCallbackFlag*/{


    private JPanel contentPanel;
    private JPanel buttonContainer; // 存储按钮的容器
    private Map<String, ContentPanel> panels; // 存储各个内容面板的映射
    public static int isloggedin = 4; // 0-未登录 1-学生 2-老师 3-管理员 4-开发者
    ClientController clientController;
    String id;
    UserInfoPage userInfoPage;
    public static String stuid;

    public MainWindow(String id, int flag) throws IOException, ClassNotFoundException {
        //客户端创建
        try {
            clientController = new ClientController();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.id = id;
        stuid = id;
        // 设置窗口标题和大小
        setTitle("Main Window");
        setSize(1100, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true); // 可以调整窗口大小
        setLayout(new BorderLayout());

        // 添加自定义窗口关闭操作
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    handleWindowClosing(); // 自定义关闭响应
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        // 创建左侧菜单面板
        JPanel menuPanel = new JPanel();
        menuPanel.setPreferredSize(new Dimension(200, getHeight()));
        menuPanel.setLayout(new BorderLayout());
        menuPanel.setBackground(new Color(220, 220, 220)); // 设置左侧菜单区背景为浅灰色

        // 创建按钮容器，并设置为垂直布局
        buttonContainer = new JPanel();
        buttonContainer.setLayout(new BoxLayout(buttonContainer, BoxLayout.Y_AXIS));
        buttonContainer.setOpaque(false); // 使按钮容器透明，使用父面板的背景颜色

        // 初始化按钮（默认未登录）
        updateMenuButtons(0);

        // 将按钮容器添加到左侧菜单面板并居中对齐
        menuPanel.add(buttonContainer, BorderLayout.NORTH);

        // 创建右侧内容面板
        contentPanel = new JPanel(new CardLayout()); // 使用 CardLayout 切换内容
        // 初始化内容面板映射
        panels = new HashMap<>();
        //创建登陆页面（也可以将loginPage声明在构造函数外）
        //LoginPage loginPage = new LoginPage(clientController);
        //创建用户信息界面
        try {
            userInfoPage = new UserInfoPage(clientController);
            userInfoPage.setAllField(clientController,id);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }


        //panels.put("首页", new homePage());
        //panels.put("首页（未登录）", new homePage());
        Option2Panel option2Panel = new Option2Panel();
        panels.put("个人信息概览", option2Panel);
        panels.put("成绩查询", new Option3Panel());

        panels.put("学生信息管理", new StuInfo());
        //panels.put("loginPage", loginPage); // 添加二级页面
        //panels.put("registerPage", new RegisterPage(clientController));
        panels.put("首页", userInfoPage);
        panels.put("FakeGPT", new ChatUI());
        panels.put("交朋友", new LANChatApp());
        panels.put("News", new NewsListPanel());
        try {
            panels.put("商店", new ShopUI());
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
            panels.put("商店（管理员）", new ShopManageUI());
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        panels.put("ATM",new ATMUI());

        panels.put("图书馆", new EnterLibPanel(stuid, flag));
        panels.put("选课系统", new CourseSelectedPage(stuid));
        panels.put("选课系统教师端", new TeacherSelectedPage(stuid));
        panels.put("选课系统教务端", new ManagerSelectedPage(this));

        //调用回调函数，获取登陆信息（回调函数原理不是太懂···
        //setLoginPage(loginPage);

        // 将每个内容面板添加到右侧内容区域
        for (Map.Entry<String, ContentPanel> entry : panels.entrySet()) {
            contentPanel.add(entry.getValue().getPanel(), entry.getKey());
        }

        // 设置初始显示页面为“首页”
        CardLayout layout = (CardLayout) contentPanel.getLayout();
        layout.show(contentPanel, "首页"); // 显示“首页”页面

        // 将菜单面板和内容面板添加到主窗口
        add(menuPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        updateMenuButtons(flag);


        setVisible(true);
    }

    private void handleWindowClosing() throws IOException {
        ClientController clientController1=new ClientController();
        clientController1.sendMessage(new Message(Message.MessageType.end));
        System.out.println("正在关闭应用程序...");
        dispose();
    }


    // 工具方法：创建菜单按钮并设置固定宽度
    private MenuButton createMenuButton(String text, String iconPath) {
        // 使用带有图标路径的构造器
        MenuButton button = new MenuButton(text, iconPath);
        button.setMaximumSize(new Dimension(175, 50)); // 设置按钮宽度为175，高度为50
        button.setAlignmentX(Component.CENTER_ALIGNMENT); // 水平方向居中对齐

        // 添加按钮的点击事件
        button.addActionListener(e -> switchContent(text));
        return button;
    }

    // 更新菜单按钮
    public void updateMenuButtons(int userType) {
        // 根据用户类型更新 isloggedin
        isloggedin = userType;

        // 清空现有按钮
        buttonContainer.removeAll();
        // 在顶部添加 20 像素的间隙
        buttonContainer.add(Box.createVerticalStrut(10));
        // 根据用户类型添加对应的按钮
        switch (isloggedin) {
            case 1: // 学生
                buttonContainer.add(createMenuButton("首页", "Icon/home.png"));
                buttonContainer.add(Box.createVerticalStrut(10));
                buttonContainer.add(createMenuButton("成绩查询", "Icon/grade.png"));
                buttonContainer.add(Box.createVerticalStrut(10));
                buttonContainer.add(createMenuButton("FakeGPT", "Icon/AI.png"));
                buttonContainer.add(Box.createVerticalStrut(10));
                buttonContainer.add(createMenuButton("News", "Icon/news.png"));
                buttonContainer.add(Box.createVerticalStrut(10));
                buttonContainer.add(createMenuButton("商店", "Icon/store.png"));
                buttonContainer.add(Box.createVerticalStrut(10));
                buttonContainer.add(createMenuButton("ATM", "Icon/bank.png"));
                buttonContainer.add(Box.createVerticalStrut(10));
                buttonContainer.add(createMenuButton("图书馆", "Icon/library.png"));
                buttonContainer.add(Box.createVerticalStrut(10));
                buttonContainer.add(createMenuButton("选课系统", "Icon/lesson.png"));
                buttonContainer.add(Box.createVerticalStrut(10));
                buttonContainer.add(createMenuButton("交朋友", "Icon/friends.png"));

;
                break;
            case 2: // 老师
                buttonContainer.add(createMenuButton("首页", "Icon/home.png"));
                buttonContainer.add(Box.createVerticalStrut(10));
                buttonContainer.add(createMenuButton("FakeGPT", "Icon/AI.png"));
                buttonContainer.add(Box.createVerticalStrut(10));
                buttonContainer.add(createMenuButton("News", "Icon/news.png"));
                buttonContainer.add(Box.createVerticalStrut(10));
                buttonContainer.add(createMenuButton("商店", "Icon/store.png"));
                buttonContainer.add(Box.createVerticalStrut(10));
                buttonContainer.add(createMenuButton("ATM", "Icon/bank.png"));
                buttonContainer.add(Box.createVerticalStrut(10));
                buttonContainer.add(createMenuButton("图书馆", "Icon/library.png"));
                buttonContainer.add(Box.createVerticalStrut(10));
                buttonContainer.add(createMenuButton("选课系统教师端", "Icon/teacher.png"));
                break;
            case 3: // 管理员
                buttonContainer.add(createMenuButton("首页", "Icon/home.png"));
                buttonContainer.add(Box.createVerticalStrut(10));
                buttonContainer.add(createMenuButton("学生信息管理", "Icon/infoManaging.png"));
                buttonContainer.add(Box.createVerticalStrut(10));
                buttonContainer.add(createMenuButton("FakeGPT", "Icon/AI.png"));
                buttonContainer.add(Box.createVerticalStrut(10));
                buttonContainer.add(createMenuButton("News", "Icon/news.png"));
                buttonContainer.add(Box.createVerticalStrut(10));
                buttonContainer.add(createMenuButton("商店", "Icon/store.png"));
                buttonContainer.add(Box.createVerticalStrut(10));
                buttonContainer.add(createMenuButton("ATM", "Icon/bank.png"));
                buttonContainer.add(Box.createVerticalStrut(10));
                buttonContainer.add(createMenuButton("商店（管理员）", "Icon/store.png"));
                buttonContainer.add(Box.createVerticalStrut(10));
                buttonContainer.add(createMenuButton("图书馆", "Icon/library.png"));
                buttonContainer.add(Box.createVerticalStrut(10));
                buttonContainer.add(createMenuButton("选课系统教务端", "Icon/manager.png"));
                break;
            case 4: // 开发者
                buttonContainer.add(createMenuButton("首页", "Icon/home.png"));
                buttonContainer.add(Box.createVerticalStrut(10));
//                buttonContainer.add(createMenuButton("首页（未登录）", "Icon/home.png"));
//                buttonContainer.add(Box.createVerticalStrut(10));
                buttonContainer.add(createMenuButton("个人信息概览", "Icon/personalInfo.png"));
                buttonContainer.add(Box.createVerticalStrut(10));
                buttonContainer.add(createMenuButton("成绩查询", "Icon/grade.png"));
                buttonContainer.add(Box.createVerticalStrut(10));
                buttonContainer.add(createMenuButton("学生信息管理", "Icon/infoManaging.png"));
                buttonContainer.add(Box.createVerticalStrut(10));
                buttonContainer.add(createMenuButton("FakeGPT", "Icon/AI.png"));
                buttonContainer.add(Box.createVerticalStrut(10));
                buttonContainer.add(createMenuButton("News", "Icon/news.png"));
                buttonContainer.add(Box.createVerticalStrut(10));
                buttonContainer.add(createMenuButton("商店", "Icon/store.png"));
                buttonContainer.add(Box.createVerticalStrut(10));
                buttonContainer.add(createMenuButton("ATM", "Icon/bank.png"));
                buttonContainer.add(Box.createVerticalStrut(10));
                buttonContainer.add(createMenuButton("商店（管理员）", "Icon/store.png"));
                buttonContainer.add(Box.createVerticalStrut(10));
                buttonContainer.add(createMenuButton("图书馆", "Icon/library.png"));
                buttonContainer.add(Box.createVerticalStrut(10));
                buttonContainer.add(createMenuButton("选课系统", "Icon/lesson.png"));
                buttonContainer.add(Box.createVerticalStrut(10));
                buttonContainer.add(createMenuButton("选课系统教师端", "Icon/teacher.png"));
                buttonContainer.add(Box.createVerticalStrut(10));
                buttonContainer.add(createMenuButton("选课系统教务端", "Icon/manager.png"));
                buttonContainer.add(Box.createVerticalStrut(10));
                buttonContainer.add(createMenuButton("交朋友", "Icon/friends.png"));

                break;

            default: // 未登录或未知身份
                buttonContainer.add(createMenuButton("首页（未登录）", "Icon/home.png"));
                break;
        }

        // 重新布局并刷新界面
        buttonContainer.revalidate();
        buttonContainer.repaint();
    }

    // 切换内容面板
    public void switchContent(String option) {
        CardLayout layout = (CardLayout) contentPanel.getLayout();
        layout.show(contentPanel, option);
    }

    // 登录成功后的处理，更新界面
    public void onLoginSuccess(int userType) {
        // 传入用户类型，例如 1-学生, 2-老师, 3-管理员
        updateMenuButtons(userType);
    }

    public static void setStatus(int status) {
        isloggedin = status;
    }

    /*回调函数*/
    //这里可以获取登陆信息，flag表示登陆成功后的身份，id就是一卡通号
//    @Override
//    public void onLoginSuccessFlag(int flag,String id) {
//        isloggedin = flag;
//        this.id = id;
//        stuid = id;
//        System.out.println("赋值");
//        System.out.println(stuid);
//        //panels.put("首页", userInfoPage);
//        PersonalInfoTable.fetchDataAndFillTable(stuid);
//        // 在这里处理flag
//        System.out.println("Login successful, flag: " + flag);
//        System.out.println("Login successful, id: " + id);
//        // 可以根据flag的值进行页面切换等操作
//        try {
//            userInfoPage.setAllField(clientController,id);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        //表示登陆成功后切换到用户信息界面
//
//        switchContent("首页");
//
//    }

//    public void setLoginPage(LoginPage loginPage) {
//        // 假设你有一个loginPage的实例，并且你可以访问它
//        loginPage.setCallback(this);
//    }

    // 程序入口
    public static void main(String[] args) {
        //SwingUtilities.invokeLater(MainWindow::new);
    }
}