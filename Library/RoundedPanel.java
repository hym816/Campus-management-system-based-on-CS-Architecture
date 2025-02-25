package Server.Library;

import javax.swing.*;
import java.awt.*;

class RoundedPanel extends JPanel {
    private Color backgroundColor;
    private int cornerRadius;

    public RoundedPanel(int radius, Color bgColor) {
        super();
        cornerRadius = radius;
        backgroundColor = bgColor;
        setOpaque(false); // 设置为透明，以便绘制圆角背景
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Dimension arcs = new Dimension(cornerRadius, cornerRadius); // 圆角的宽度和高度
        int width = getWidth();
        int height = getHeight();
        Graphics2D graphics = (Graphics2D) g;
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // 抗锯齿

        // 绘制圆角矩形背景
        graphics.setColor(backgroundColor);
        graphics.fillRoundRect(0, 0, width, height, arcs.width, arcs.height); // 绘制圆角矩形
    }
}
