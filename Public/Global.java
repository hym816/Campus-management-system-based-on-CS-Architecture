package Server.Public;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Global {

    // 静态购物车，存储商品编号和数量
    public static Map<Integer, Integer> cart = new HashMap<>();

    // 清空购物车
    public static void clearCart() {
        cart.clear();
    }

    // 方法用于显示Message对象中的图片
    public static void displayImageFromMessage(Message message) {
        // 获取图片数据
        List<Serializable> content = message.getContent();
        if (content != null && !content.isEmpty()) {
            byte[] imageBytes = (byte[]) content.get(0);

            // 将字节数组转换为BufferedImage
            BufferedImage image = null;
            try {
                image = ImageIO.read(new ByteArrayInputStream(imageBytes));
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            // 显示图片
            displayImage(image);
        }
    }

    // 显示BufferedImage的方法
    private static void displayImage(BufferedImage image) {
        // 创建一个JFrame来承载图片
        JFrame frame = new JFrame("Display Image");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(image.getWidth(), image.getHeight());

        // 创建一个JLabel来显示图片
        ImageIcon imageIcon = new ImageIcon(image);
        JLabel label = new JLabel(imageIcon);

        // 将JLabel添加到JFrame中
        frame.add(label);

        // 显示窗口
        frame.setVisible(true);
    }

}