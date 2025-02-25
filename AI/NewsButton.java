package Server.AI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.border.EmptyBorder;
import javax.swing.Timer;

public class NewsButton extends JPanel {
    private static final Color DEFAULT_COLOR = Color.WHITE;
    private static final Color HOVER_COLOR = new Color(230, 230, 230);
    private static final Color PRESS_COLOR = new Color(200, 200, 200);
    private String title;
    private Runnable onClickAction;

    public NewsButton(String title, Runnable onClickAction) {
        this.title = title;
        this.onClickAction = onClickAction;
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(DEFAULT_COLOR);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        add(titleLabel, BorderLayout.CENTER);

        // 添加鼠标事件监听器来处理悬停和点击效果
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                animateBackgroundColor(HOVER_COLOR, 100);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                animateBackgroundColor(DEFAULT_COLOR, 100);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                animateBackgroundColor(PRESS_COLOR, 100);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                animateBackgroundColor(HOVER_COLOR, 100);
                if (onClickAction != null) {
                    onClickAction.run();
                }
            }
        });
    }

    // 渐变背景颜色的方法
    private void animateBackgroundColor(Color targetColor, int duration) {
        Timer timer = new Timer(10, null);
        Color startColor = getBackground();
        int steps = duration / 10;
        int rStep = (targetColor.getRed() - startColor.getRed()) / steps;
        int gStep = (targetColor.getGreen() - startColor.getGreen()) / steps;
        int bStep = (targetColor.getBlue() - startColor.getBlue()) / steps;

        timer.addActionListener(e -> {
            int r = getBackground().getRed() + rStep;
            int g = getBackground().getGreen() + gStep;
            int b = getBackground().getBlue() + bStep;
            setBackground(new Color(Math.min(255, Math.max(0, r)),
                    Math.min(255, Math.max(0, g)),
                    Math.min(255, Math.max(0, b))));
            repaint();

            // 判断是否达到目标颜色
            if (Math.abs(getBackground().getRed() - targetColor.getRed()) <= Math.abs(rStep) &&
                    Math.abs(getBackground().getGreen() - targetColor.getGreen()) <= Math.abs(gStep) &&
                    Math.abs(getBackground().getBlue() - targetColor.getBlue()) <= Math.abs(bStep)) {
                setBackground(targetColor);
                timer.stop();
            }
        });
        timer.start();
    }
}
