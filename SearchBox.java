package Server;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SearchBox extends JPanel {
    private PlaceholderTextField textField;
    private JLabel searchIconLabel;
    private JLabel deleteIconLabel;

    public SearchBox() {
        // 设置布局和外观
        setLayout(new BorderLayout());
        setBackground(new Color(220, 220, 220)); // 浅灰色背景
        setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1, true), // 圆角边框
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        setPreferredSize(new Dimension(300, 30)); // 搜索框的尺寸

        // 设置搜索图标，并调整大小为搜索框的高度
        searchIconLabel = new JLabel();
        ImageIcon searchIcon = loadIcon("Icon/searchIcon.png", 20, 20);
        if (searchIcon != null) {
            searchIconLabel.setIcon(searchIcon);
        }
        add(searchIconLabel, BorderLayout.WEST);

        // 创建文本框，背景为浅灰色
        textField = new PlaceholderTextField("搜索"); // 使用自定义的带提示文字的文本框
        textField.setBorder(new EmptyBorder(0, 5, 0, 5));
        textField.setBackground(new Color(220, 220, 220)); // 浅灰色
        textField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        textField.setForeground(Color.BLACK);

        // 添加输入监听，当有输入时，显示删除图标
        textField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                toggleDeleteIcon();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                toggleDeleteIcon();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                toggleDeleteIcon();
            }
        });

        add(textField, BorderLayout.CENTER);

        // 设置删除图标，并调整大小为搜索框的高度
        deleteIconLabel = new JLabel();
        ImageIcon deleteIcon = loadIcon("Icon/deleteIcon.png", 20, 20);
        if (deleteIcon != null) {
            deleteIconLabel.setIcon(deleteIcon);
        }
        deleteIconLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        deleteIconLabel.setVisible(false); // 初始状态隐藏
        deleteIconLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setText(""); // 使用 setText 清空搜索框
                toggleDeleteIcon();
            }
        });

        add(deleteIconLabel, BorderLayout.EAST);
    }

    // 增加 addActionListener 方法
    public void addActionListener(ActionListener listener) {
        textField.addActionListener(listener);
    }

    // 增加 setText 方法
    public void setText(String text) {
        textField.setText(text);
    }

    // 获取搜索框文本的方法
    public String getText() {
        return textField.getText();
    }

    // 切换删除图标的显示状态
    private void toggleDeleteIcon() {
        if (textField.getText().isEmpty()) {
            deleteIconLabel.setVisible(false);
        } else {
            deleteIconLabel.setVisible(true);
        }
    }

    // 加载图标并调整大小
    private ImageIcon loadIcon(String path, int width, int height) {
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(path));
            Image scaledImage = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (Exception e) {
            System.err.println("图标加载失败: " + path);
            return null;
        }
    }

    public void addSearchActionListener(ActionListener listener) {
        textField.addActionListener(listener);
    }

    // 自定义的文本框类，支持显示提示文字并控制光标显示
    static class PlaceholderTextField extends JTextField {
        private final String placeholder;

        public PlaceholderTextField(String placeholder) {
            this.placeholder = placeholder;
            setCaretColor(new Color(0, 0, 0, 0)); // 初始时隐藏光标
            setFocusable(true);

            // 处理焦点事件，控制光标显示和隐藏
            addFocusListener(new java.awt.event.FocusAdapter() {
                @Override
                public void focusGained(java.awt.event.FocusEvent evt) {
                    setCaretColor(Color.BLACK); // 聚焦时显示光标
                    repaint();
                }

                @Override
                public void focusLost(java.awt.event.FocusEvent evt) {
                    setCaretColor(new Color(0, 0, 0, 0)); // 失去焦点时隐藏光标
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (getText().isEmpty() && !((JTextField) this).hasFocus()) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.GRAY); // 设置提示文字颜色
                g2.setFont(getFont().deriveFont(Font.ITALIC));
                g2.drawString(placeholder, getInsets().left, g.getFontMetrics().getMaxAscent() + getInsets().top);
            }
        }
    }

    // 测试用的主函数，可以在测试阶段使用，最终集成时可以移除
    public static void main(String[] args) {
        JFrame frame = new JFrame("搜索框示例");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 100);
        JPanel panel = new JPanel();

        // 创建搜索框并添加到面板
        SearchBox searchBox = new SearchBox();
        panel.add(searchBox);
        frame.add(panel);

        frame.setVisible(true);
    }
}
