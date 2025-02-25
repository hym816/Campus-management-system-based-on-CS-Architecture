package Server.Public;

import javax.swing.*;
import java.awt.Image;

public class IconUtils {

    /**
     * 加载图标并调整到指定宽度和高度
     * @param path 图标的路径
     * @param width 图标的宽度
     * @param height 图标的高度
     * @return 调整大小后的 ImageIcon
     */
    public static ImageIcon loadIcon(String path, int width, int height) {
        ImageIcon icon = new ImageIcon(path);
        // 调整图片大小
        Image image = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(image);
    }
}
