package Server.Public;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class textField extends JTextField {

    private Color borderColor = new Color(0, 0, 0, 0); // 初始透明边框颜色
    private Color defaultBackground = new Color(230, 230, 230); // 初始浅灰色
    private Color focusBorderColor = new Color(0, 122, 255, 180); // 聚焦时边框颜色
    private boolean isMousePressed = false;

    public textField(int columns) {
        super(columns);
        setOpaque(false); // 透明背景
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // 内边距

        // 添加焦点监听器，控制边框颜色变化
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                borderColor = focusBorderColor;
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                borderColor = new Color(0, 0, 0, 0);
                repaint();
            }
        });

        // 鼠标事件监听器，控制渐变效果
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                isMousePressed = true;
                animateBackgroundColor(defaultBackground, Color.GRAY, 150); // 渐变为灰色
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isMousePressed = false;
                animateBackgroundColor(Color.GRAY, defaultBackground, 150); // 渐变恢复为浅灰色
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 绘制圆角矩形背景
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

        // 绘制边框
        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(1));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);

        super.paintComponent(g);
        g2.dispose();
    }

    private void animateBackgroundColor(Color startColor, Color endColor, int duration) {
        new Thread(() -> {
            for (int i = 0; i <= 100; i++) {
                float ratio = i / 100f;
                Color stepColor = new Color(
                        (int) (startColor.getRed() * (1 - ratio) + endColor.getRed() * ratio),
                        (int) (startColor.getGreen() * (1 - ratio) + endColor.getGreen() * ratio),
                        (int) (startColor.getBlue() * (1 - ratio) + endColor.getBlue() * ratio)
                );
                setBackground(stepColor);
                repaint();
                try {
                    Thread.sleep(duration / 100);
                } catch (InterruptedException ignored) {
                }
            }
        }).start();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Rounded Text Field Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 20)); // 设置布局和间距

        // 创建第一个输入框
        textField textField1 = new textField(20);
        frame.add(textField1);

        // 创建第二个输入框
        textField textField2 = new textField(20);
        frame.add(textField2);

        frame.setVisible(true);
    }
}
