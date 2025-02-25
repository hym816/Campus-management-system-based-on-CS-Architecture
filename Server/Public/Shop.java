package Server.Public;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Shop {
    private static Database database = new Database("jdbc:mysql://"+Server.sqlip+":3306/my_database2",
            Server.user,Server.password);

    private static String databaseName = "my_database2"; // 数据库名称
    private static String tableName = "shop"; // 表格名称

    public static Connection getConn()
    {
        return database.connection;
    }

    public static List<Product> getProductsFromDatabase(Connection conn) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT product_Id, product, price, count, image FROM " + databaseName + "." + tableName;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            preparedStatement = conn.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int product_Id = resultSet.getInt("product_Id");
                String name = resultSet.getString("product");
                double price = resultSet.getDouble("price");
                int count = resultSet.getInt("count");
                byte[] image = resultSet.getBytes("image");  // 获取图片数据

                // 创建包括图片的 Product 对象
                products.add(new Product(product_Id, name, price, count, image));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("从数据库获取商品失败");
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return products;
    }



    // 从数据库中获取所有商品
    public static Message getProducts(Connection conn) {
        List<Product> products = new ArrayList<>();
        // 更新 SQL 查询以包括 image 列
        String sql = "SELECT product_Id, product, price, count, image FROM " + databaseName + "." + tableName;
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                // 获取 product_Id, name, price, count, 和 image 数据
                int product_Id = resultSet.getInt("product_Id");
                String name = resultSet.getString("product");
                double price = resultSet.getDouble("price");
                int count = resultSet.getInt("count");
                byte[] image = resultSet.getBytes("image"); // 获取图片数据

                // 使用包括 product_Id 和 image 的构造函数创建 Product 对象
                products.add(new Product(product_Id, name, price, count, image));
            }

            List<Serializable> temp = new ArrayList<>();
            temp.add((Serializable) new ArrayList<>(products));
            Message m = new Message(Message.MessageType.success, temp);
            return m;

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("从数据库获取商品失败");
        }
        return new Message(Message.MessageType.failure);
    }


    //if (!temp.isEmpty() && temp.get(0) instanceof List<?>) {
    //            List<?> tempList = (List<?>) temp.get(0);
    //
    //            // 确保列表中的元素是 Product 类型
    //            if (!tempList.isEmpty() && tempList.get(0) instanceof Product) {
    //                // 将 tempList 转换回 List<Product>
    //                List<Product> parsedProducts = (List<Product>) tempList;




    // 添加商品到数据库
    public static Message addProduct(Product product) {
        String sql = "INSERT INTO " + databaseName + "." + tableName + " (product, price, count, image) VALUES (?, ?, ?, ?)";
        database.addToDatabaseWithMultipleValues(sql, product.getName(), product.getPrice(), product.getCount(),product.getImage());
        return new Message(Message.MessageType.success);
    }


    // 从数据库中删除商品
    public static Message removeProduct(String productName) {
        database.deleteFromDatabase("product", productName, databaseName, tableName);
        return new Message(Message.MessageType.success);
    }

    // 从数据库中删除商品
    public static Message removeProduct(int productId) {
        // 根据 product_Id 删除商品
        database.deleteFromDatabase("product_Id", productId, databaseName, tableName);
        return new Message(Message.MessageType.success);
    }



    // 购买商品，并减少库存
    public static Message purchaseProduct(String productName,int num) {
        List<Product> products = getProductsFromDatabase(database.connection);
        for (Product product : products) {
            if (product.getName().equalsIgnoreCase(productName) &&
                    product.getCount() >= num)
            {
                database.reviseStringByColumnName("product", productName, databaseName, tableName, "count", product.getCount() - num);
                System.out.println("已购买 " + product.getName());
                return new Message(Message.MessageType.success);
            }
        }
        System.out.println("商品 " + productName + " 不存在或库存不足。");
        return new Message(Message.MessageType.failure);
    }

    // 购买商品，并减少库存
    public static Message purchaseProduct(int productId, int num) {
        List<Product> products = getProductsFromDatabase(database.connection);
        for (Product product : products) {
            if (product.getProduct_Id() == productId &&
                    product.getCount() > num ) {
                // 根据 product_Id 更新库存
                database.reviseStringByColumnName("product_Id", productId, databaseName, tableName, "count", product.getCount() - num);
                System.out.println("已购买 " + product.getName());
                return new Message(Message.MessageType.success);
            }
        }
        System.out.println("商品 ID " + productId + " 不存在或库存不足。");
        return new Message(Message.MessageType.failure);
    }


    // 增加商品库存
    public static Message increaseProductCount(String productName, int amount) {
        List<Product> products = getProductsFromDatabase(database.connection);
        for (Product product : products) {
            if (product.getName().equalsIgnoreCase(productName)) {
                if (amount > 0) {
                    database.reviseStringByColumnName("product", productName, databaseName, tableName, "count", product.getCount() + amount);
                    System.out.println("已增加 " + productName + " 的库存数量，增加了 " + amount + " 件。");
                } else {
                    System.out.println("增加的数量必须大于 0。");
                }
                return new Message(Message.MessageType.success);
            }
        }
        System.out.println("商品 " + productName + " 不存在。");
        return new Message(Message.MessageType.failure);
    }

    // 增加商品库存
    public static Message increaseProductCount(int productId, int amount) {
        List<Product> products = getProductsFromDatabase(database.connection);
        for (Product product : products) {
            if (product.getProduct_Id() == productId) { // 使用 productId 进行匹配
                if (amount > 0) {
                    // 更新数据库中的 count 值，使用 reviseStringByColumnName 方法
                    database.reviseStringByColumnName("product_Id", productId, databaseName, tableName, "count", product.getCount() + amount);
                    System.out.println("已增加 ID 为 " + productId + " 的商品库存数量，增加了 " + amount + " 件。");
                } else {
                    System.out.println("增加的数量必须大于 0。");
                }
                return new Message(Message.MessageType.success);
            }
        }
        System.out.println("商品 ID " + productId + " 不存在。");
        return new Message(Message.MessageType.failure);
    }

}

