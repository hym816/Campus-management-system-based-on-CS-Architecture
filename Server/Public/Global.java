package Server.Public;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

public class Global {

    private static ThreadLocal<Boolean> verificationSuccess = ThreadLocal.withInitial(() -> false);

    // 获取当前线程的验证状态
    public static boolean isVerificationSuccess() {
        return verificationSuccess.get();
    }

    // 设置当前线程的验证状态
    public static void setVerificationSuccess(boolean success) {
        verificationSuccess.set(success);
    }

    // 清除当前线程的状态，避免内存泄漏
    public static void clearVerificationStatus() {
        verificationSuccess.remove();
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
