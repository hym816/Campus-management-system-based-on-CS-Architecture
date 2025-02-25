package Server.Store;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ProductContainer extends JPanel {

    public ProductContainer(List<ProductComponent> productComponents) {
        // 使用 GridLayout 布局，每行固定3个卡片
        setLayout(new GridLayout(0, 3, 10, 10)); // 0行表示行数自动增加，3列，间距为10像素

        // 添加所有的 ProductComponent 到容器中
        for (ProductComponent productComponent : productComponents) {
            productComponent.setPreferredSize(new Dimension(200, 200)); // 确保每个卡片大小为200x200
            add(productComponent);
        }

        // 设置面板边距
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

}
