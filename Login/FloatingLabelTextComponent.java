package Server.Login;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class FloatingLabelTextComponent extends JPanel {
    private JTextComponent textComponent; // 用于统一处理 JTextField 和 JPasswordField
    private JLabel floatingLabel;
    private Timer timer;
    private int labelX = 10;
    private int labelY = 30; // 标签初始位置调整为文本框中心
    private float fontSize = 16f; // 初始标签字体大小
    private final int targetLabelY = 0; // 标签最终的目标位置，向上多移5像素

    public FloatingLabelTextComponent(String placeholder, boolean isPasswordField) {
        setLayout(null); // 使用绝对定位来精确控制组件内部布局
        setPreferredSize(new Dimension(300, 60)); // 设置组件的首选大小

        // 根据标志创建合适的文本组件
        if (isPasswordField) {
            textComponent = new JPasswordField();
            ((JPasswordField) textComponent).setEchoChar('·'); // 设置掩码字符为“·”
        } else {
            textComponent = new JTextField();
        }

        // 设置文本组件的通用属性
        textComponent.setFont(textComponent.getFont().deriveFont(fontSize));
        textComponent.setMargin(new Insets(5, 10, 5, 10)); // 设置内边距
        textComponent.setBounds(0, 20, 300, 40); // 设置文本框的位置和大小
        textComponent.setBackground(new Color(220, 220, 220)); // 设置文本框背景颜色
        textComponent.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true), // 圆角边框
                BorderFactory.createEmptyBorder(5, 10, 5, 10) // 内边距
        ));
        textComponent.setOpaque(true); // 确保背景不透明
        add(textComponent);

        // 设置浮动标签
        floatingLabel = new JLabel(placeholder);
        floatingLabel.setForeground(Color.GRAY);
        floatingLabel.setFont(new Font("SansSerif", Font.PLAIN, (int) fontSize));
        floatingLabel.setBounds(labelX, labelY, 150, 20); // 设置标签的位置和大小
        add(floatingLabel);

        // 将浮动标签置于文本框之上
        setComponentZOrder(floatingLabel, 0);
        setComponentZOrder(textComponent, 1);

        // 添加焦点监听器来处理边框颜色和标签动画
        textComponent.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                textComponent.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(30, 144, 255), 1, true), // 天蓝色圆角边框
                        BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));
                startAnimation(); // 开始动画，当文本框获得焦点时
            }

            @Override
            public void focusLost(FocusEvent e) {
                textComponent.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true), // 恢复浅灰色圆角边框
                        BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));
                if (textComponent instanceof JTextField && ((JTextField) textComponent).getText().isEmpty()) {
                    reverseAnimation(); // 当文本框失去焦点且为空时，反向动画
                } else if (textComponent instanceof JPasswordField && ((JPasswordField) textComponent).getPassword().length == 0) {
                    reverseAnimation(); // 密码框情况下的反向动画
                }
            }
        });
    }

    // 添加 setText 方法
    public void setText(String text) {
        if (textComponent instanceof JTextField) {
            ((JTextField) textComponent).setText(text);
        } else if (textComponent instanceof JPasswordField) {
            ((JPasswordField) textComponent).setText(text);
        }
        // 如果有文本内容，直接触发动画
        if (!text.isEmpty()) {
            startAnimation();
        }
    }

    private void startAnimation() {
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
        timer = new Timer(10, e -> {
            if (labelY > targetLabelY) { // 限制标签上移的最终高度
                labelY -= 1;
                if (labelY < targetLabelY) {
                    labelY = targetLabelY; // 确保不会超过目标位置
                }
                fontSize -= 0.1f;
                floatingLabel.setFont(floatingLabel.getFont().deriveFont(fontSize));
                floatingLabel.setBounds(labelX, labelY, 150, 20);
            } else {
                timer.stop();
            }
        });
        timer.start();
    }

    private void reverseAnimation() {
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
        timer = new Timer(10, e -> {
            if (labelY < 30) { // 恢复标签的原始位置（文本框中心）
                labelY += 1;
                fontSize += 0.1f;
                floatingLabel.setFont(floatingLabel.getFont().deriveFont(fontSize));
                floatingLabel.setBounds(labelX, labelY, 150, 20);
            } else {
                timer.stop();
            }
        });
        timer.start();
    }

    public String getText() {
        if (textComponent instanceof JPasswordField) {
            return new String(((JPasswordField) textComponent).getPassword());
        } else {
            return ((JTextField) textComponent).getText();
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Floating Label Text Component Demo");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        FloatingLabelTextComponent usernameField = new FloatingLabelTextComponent("用户名", false);
        FloatingLabelTextComponent passwordField = new FloatingLabelTextComponent("密码", true);

        frame.setLayout(new FlowLayout());
        frame.add(usernameField);
        frame.add(passwordField);

        frame.setVisible(true);
    }
}
