package Server.Library;

import javax.swing.*;
import java.awt.*;

public class ComboBoxPanel extends JPanel {
    private static JComboBox<String> comboBox;

    public ComboBoxPanel() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(150, 30)); // 设置下拉栏的首选尺寸

        // 创建下拉栏并添加选项
        comboBox = new JComboBox<>(new String[]{"书名", "作者", "国籍", "类型"});
        comboBox.setFont(new Font("SansSerif", Font.PLAIN, 14));
        comboBox.setBackground(new Color(220, 220, 220)); // 设置下拉栏背景颜色
        comboBox.setForeground(Color.BLACK); // 设置下拉栏文字颜色

        // 添加下拉栏到面板
        add(comboBox, BorderLayout.CENTER);
    }


    public JComboBox<String> getComboBox() {
        return comboBox;
    }
    public static String getSelectedValue() {
        // 获取选中的值并返回
        return (String) comboBox.getSelectedItem();
    }
}