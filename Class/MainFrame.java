package Server.Class;

import Server.Public.ContentPanel;

import javax.swing.*;
import java.io.IOException;










public class MainFrame extends JFrame implements ContentPanel {
    private JFrame frame;

    String teacher = "Ren GUOguo";

    public MainFrame() {
        frame =this;
        setTitle("课程选择页面");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // 窗口居中

        try {
            //CourseSelectedPage courseSelectedPage = new CourseSelectedPage();
            ManagerSelectedPage courseSelectedPage = new ManagerSelectedPage(frame);
            //TeacherSelectedPage courseSelectedPage = new TeacherSelectedPage(teacher);
            //CourseSelectedPage courseSelectedPage = new CourseSelectedPage();
            setContentPane(courseSelectedPage.getPanel());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }

    @Override
    public JPanel getPanel() {
        return null;
    }
}

