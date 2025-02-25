package Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class MenuButton extends JButton {

    // List to keep track of all MenuButtons
    private static final List<MenuButton> buttons = new ArrayList<>();
    private boolean isSelected = false;

    public MenuButton(String text, String iconPath) {
        super(text);
        setContentAreaFilled(false); // Makes the button background transparent
        setFocusPainted(false);
        setBorderPainted(false);
        setForeground(Color.BLACK);
        setHorizontalAlignment(SwingConstants.LEFT); // Align text to the left
        setFont(new Font("微软雅黑", Font.PLAIN, 14)); // 设置字体大小
        setIconTextGap(10);
        // Load and scale the icon if the path is provided and valid
        if (iconPath != null && !iconPath.isEmpty()) {
            ImageIcon icon = loadIcon(iconPath, getPreferredSize().height);
            if (icon != null) {
                setIcon(icon);
            } else {
                System.out.println("图标加载失败: " + iconPath);
            }
        }

        // Add the button to the list of MenuButtons
        buttons.add(this);

        // Add mouse listener to handle click events
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Deselect other buttons and select the clicked button
                deselectOtherButtons();
                isSelected = true;
                repaint();
            }
        });
    }

    // Method to load and scale the icon
    private ImageIcon loadIcon(String path, int height) {
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(path));
            Image scaledImage = icon.getImage().getScaledInstance(-1, (int) (height * 0.5), Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (Exception e) {
            System.err.println("图标加载失败: " + path);
            return null;
        }
    }

    // Method to deselect all other MenuButtons
    private void deselectOtherButtons() {
        for (MenuButton button : buttons) {
            if (button != this) {
                button.isSelected = false;
                button.repaint();
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw the background as a rounded rectangle when selected
        if (isSelected) {
            g2.setColor(Color.LIGHT_GRAY);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
        }

        super.paintComponent(g);
        g2.dispose();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Menu Button Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);
        frame.setLayout(new FlowLayout());

        // Create and add MenuButtons with optional icons
        MenuButton button1 = new MenuButton("Button 1", "Icon/deleteIcon.png"); // 替换为有效路径
        MenuButton button2 = new MenuButton("Button 2", "Icon/deleteIcon.png"); // 替换为有效路径
        MenuButton button3 = new MenuButton("Button 3", null); // No icon for this button

        frame.add(button1);
        frame.add(button2);
        frame.add(button3);

        frame.setVisible(true);
    }
}
