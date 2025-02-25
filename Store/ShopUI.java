package Server.Store;

import Server.Public.ClientController;
import Server.Public.ContentPanel;
import Server.Public.Message;
import Server.Public.Product;
import Server.SearchBox;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ShopUI implements ContentPanel {

    private JPanel panel;
    private JButton refreshButton;
    private JButton showCartButton; // 新增显示购物车按钮
    private JPanel buttonPanel; // 持有按钮的面板
    private SearchBox searchBox; // 新增搜索框

    public ShopUI() throws IOException, ClassNotFoundException {
        panel = new JPanel(); // 初始化 panel
        panel.setLayout(new BorderLayout()); // 设置布局管理器，使用 BorderLayout 便于扩展

        // 创建搜索框
        searchBox = new SearchBox();

        // 创建刷新按钮和显示购物车按钮
        refreshButton = new JButton("刷新");
        showCartButton = new JButton("显示购物车"); // 创建显示购物车按钮

        // 创建顶部按钮面板
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new BorderLayout()); // 设置按钮面板为 BorderLayout
        buttonPanel.add(refreshButton, BorderLayout.WEST); // 将刷新按钮添加到左侧
        buttonPanel.add(showCartButton, BorderLayout.EAST); // 将显示购物车按钮添加到右侧

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(searchBox, BorderLayout.NORTH); // 将搜索框添加到顶部
        topPanel.add(buttonPanel, BorderLayout.SOUTH); // 将按钮面板添加到搜索框下方

        panel.add(topPanel, BorderLayout.NORTH); // 将整体的 topPanel 添加到 panel 的北边（顶部）


        searchBox.addSearchActionListener(e -> performSearch(searchBox.getText()));

        refreshButton.addActionListener(e -> {
            setupShopPanelWithoutSearch();
        });

        // 添加显示购物车按钮的 ActionListener
        showCartButton.addActionListener(e -> {
            Message response = null;

            ClientController clientController = null;
            try {
                clientController = new ClientController();
                clientController.sendMessage(new Message(Message.MessageType.all_product));
                response = clientController.receiveMessage();
            } catch (IOException | ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
            List<Serializable> temp = response.getContent();
            List<Product> parsedProducts = new ArrayList<>();

            if (temp != null && !temp.isEmpty() && temp.get(0) instanceof List<?>) {
                List<?> tempList = (List<?>) temp.get(0);
                // 确保列表中的元素是 Product 类型
                if (!tempList.isEmpty() && tempList.get(0) instanceof Product) {
                    parsedProducts = (List<Product>) tempList;
                }
            }

            CartUI cartUI = new CartUI(parsedProducts); // 创建购物车界面实例
            cartUI.setVisible(true); // 显示购物车界面
        });

        setupShopPanelWithoutSearch();
    }


    private void performSearch(String query) {
        if (query == null || query.trim().isEmpty()) {
            setupShopPanelWithoutSearch(); // 如果查询为空，显示所有商品，但不通过 performSearch 调用
            return;
        }

        query = query.toLowerCase(); // 将查询转为小写进行不区分大小写的匹配

        try {
            // 获取所有商品并过滤
            ClientController clientController = new ClientController();
            clientController.sendMessage(new Message(Message.MessageType.all_product));
            Message response = clientController.receiveMessage();
            List<Serializable> temp = response.getContent();
            List<Product> parsedProducts = new ArrayList<>();

            if (temp != null && !temp.isEmpty() && temp.get(0) instanceof List<?>) {
                List<?> tempList = (List<?>) temp.get(0);
                if (!tempList.isEmpty() && tempList.get(0) instanceof Product) {
                    parsedProducts = (List<Product>) tempList;
                }
            }

            // 过滤商品列表：名称包含查询字符串的商品
            List<ProductComponent> productComponents = new ArrayList<>();
            for (Product product : parsedProducts) {
                if (product.getName().toLowerCase().contains(query)) { // 模糊匹配
                    ProductComponent component = new ProductComponent(
                            Integer.toString(product.getProduct_Id()),
                            product.getName(),
                            product.getImage(),
                            product.getCount(),
                            product.getPrice());
                    productComponents.add(component);
                }
            }

            // 更新界面显示匹配的商品
            updateProductDisplay(productComponents);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // 设置商品展示的函数，用于显示所有商品，不涉及搜索
    private void setupShopPanelWithoutSearch() {
        try {
            if (panel.getComponentCount() > 1) {
                panel.remove(1); // 移除当前展示的商品内容
            }

            ClientController clientController = new ClientController();
            clientController.sendMessage(new Message(Message.MessageType.all_product));
            Message response = clientController.receiveMessage();
            List<Serializable> temp = response.getContent();
            List<Product> parsedProducts = new ArrayList<>();

            if (temp != null && !temp.isEmpty() && temp.get(0) instanceof List<?>) {
                List<?> tempList = (List<?>) temp.get(0);
                if (!tempList.isEmpty() && tempList.get(0) instanceof Product) {
                    parsedProducts = (List<Product>) tempList;
                }
            }

            // 创建所有商品的组件
            List<ProductComponent> productComponents = new ArrayList<>();
            for (Product product : parsedProducts) {
                ProductComponent component = new ProductComponent(
                        Integer.toString(product.getProduct_Id()),
                        product.getName(),
                        product.getImage(),
                        product.getCount(),
                        product.getPrice());
                productComponents.add(component);
            }

            updateProductDisplay(productComponents);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // 更新商品展示的函数
    private void updateProductDisplay(List<ProductComponent> productComponents) {
        if (panel.getComponentCount() > 1) {
            panel.remove(1); // 移除当前展示的商品内容
        }

        ProductContainer productContainer = new ProductContainer(productComponents);
        JScrollPane scrollPane = new JScrollPane(productContainer);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        panel.add(scrollPane, BorderLayout.CENTER);

        panel.revalidate(); // 重新验证布局
        panel.repaint(); // 重新绘制界面
    }



//    private void setupShopPanel() throws IOException, ClassNotFoundException {
//        // 清空中心区域的组件，不移除顶部的按钮面板
//        if (panel.getComponentCount() > 1) { // 检查是否有多于1个组件（即除了按钮面板外的其他内容）
//            panel.remove(1); // 移除中心部分的组件（索引 1 为中心内容）
//        }
//
//        ClientController clientController = new ClientController();
//        clientController.sendMessage(new Message(Message.MessageType.all_product));
//        Message response = clientController.receiveMessage();
//        List<Serializable> temp = response.getContent();
//        List<Product> parsedProducts = new ArrayList<>();
//
//        if (temp != null && !temp.isEmpty() && temp.get(0) instanceof List<?>) {
//            List<?> tempList = (List<?>) temp.get(0);
//            // 确保列表中的元素是 Product 类型
//            if (!tempList.isEmpty() && tempList.get(0) instanceof Product) {
//                parsedProducts = (List<Product>) tempList;
//            }
//        }
//
//        // 创建 ProductComponent 列表
//        List<ProductComponent> productComponents = new ArrayList<>();
//        for (Product product : parsedProducts) {
//            ProductComponent component = new ProductComponent(
//                    Integer.toString(product.getProduct_Id()), // 使用 int 类型
//                    product.getName(),
//                    product.getImage(),
//                    product.getCount(),
//                    product.getPrice());
//            productComponents.add(component);
//        }
//
//        // 创建 ProductContainer 并添加到 panel 中
//        ProductContainer productContainer = new ProductContainer(productComponents);
//        JScrollPane scrollPane = new JScrollPane(productContainer);
//        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
//        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
//        panel.add(scrollPane, BorderLayout.CENTER); // 将滚动面板添加到 panel 的中心
//
//        panel.revalidate(); // 重新验证布局
//        panel.repaint(); // 重新绘制界面
//    }

    @Override
    public JPanel getPanel() {
        return panel; // 返回 panel
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        JFrame frame = new JFrame("Shop UI");
        ShopUI shopUI = new ShopUI();
        frame.setContentPane(shopUI.getPanel());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600); // 设置窗口大小
        frame.setVisible(true);
    }
}














//package ex;
//
//import javax.swing.*;
//import java.awt.*;
//import java.io.IOException;
//import java.io.Serializable;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.util.ArrayList;
//import java.util.List;
//
//public class ShopUI implements ContentPanel {
//
//    private JPanel panel;
//    private JButton button;
//
//
//    public ShopUI() throws IOException, ClassNotFoundException {
//        panel = new JPanel(); // 初始化 panel
//        panel.setLayout(new BorderLayout()); // 设置布局管理器，使用 BorderLayout 便于扩展
//        button = new JButton("刷新");
//        setupShopPanel();
//        panel.add(button, BorderLayout.NORTH); // 将按钮添加到 panel 的北边（顶部）
//
//        // 添加按钮的 ActionListener
//        button.addActionListener(e -> {
//            try {
//                setupShopPanel(); // 点击按钮时调用 setupShopPanel
//            } catch (IOException | ClassNotFoundException ex) {
//                ex.printStackTrace();
//            }
//        });
//    }
//
//    // 设置商品展示的函数
//    private void setupShopPanel() throws IOException, ClassNotFoundException {
//
//
//        ClientController clientController = new ClientController();
//        clientController.sendMessage(new Message(Message.MessageType.all_product));
//        Message response = clientController.receiveMessage();
//        List<Serializable> temp = response.getContent();
//        List<Product> parsedProducts = new ArrayList<>();
//
//        if (temp != null && !temp.isEmpty() && temp.get(0) instanceof List<?>) {
//            List<?> tempList = (List<?>) temp.get(0);
//            // 确保列表中的元素是 Product 类型
//            if (!tempList.isEmpty() && tempList.get(0) instanceof Product) {
//                parsedProducts = (List<Product>) tempList;
//            }
//        }
//
//        // 创建 ProductComponent 列表
//        List<ProductComponent> productComponents = new ArrayList<>();
//        for (Product product : parsedProducts) {
//            ProductComponent component = new ProductComponent(
//                    String.valueOf(product.getProduct_Id()),
//                    product.getName(),
//                    product.getImage(),
//                    product.getCount(),
//                    product.getPrice());
//            productComponents.add(component);
//        }
//
//        // 创建 ProductContainer 并添加到 panel 中
//        ProductContainer productContainer = new ProductContainer(productComponents);
//        JScrollPane scrollPane = new JScrollPane(productContainer);
//        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
//        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
//        panel.add(scrollPane, BorderLayout.CENTER); // 将滚动面板添加到 panel 的中心
//    }
//
//    @Override
//    public JPanel getPanel() {
//        return panel; // 返回 panel
//    }
//
//    public static void main(String[] args) throws IOException, ClassNotFoundException {
//        JFrame frame = new JFrame("Shop UI");
//        ShopUI shopUI = new ShopUI();
//        frame.setContentPane(shopUI.getPanel());
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setSize(800, 600); // 设置窗口大小
//        frame.setVisible(true);
//    }
//}
