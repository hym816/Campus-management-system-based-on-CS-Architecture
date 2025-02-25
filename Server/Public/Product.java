package Server.Public;

import java.io.Serializable;

public class Product implements Serializable {
    private int product_Id;
    private String name;
    private double price;
    private int count;
    private byte[] image;  // 新增字段用于存储图片数据

    // 更新构造函数，增加 image 参数
    public Product(int product_Id, String name, double price, int count, byte[] image) {
        this.product_Id = product_Id;
        this.name = name;
        this.price = price;
        this.count = count;
        this.image = image;  // 赋值图片数据
    }

    // 新增 image 的 getter 和 setter 方法
    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public int getProduct_Id() {
        return product_Id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "Product{" +
                "product_Id=" + product_Id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", count=" + count +
                ", image=" + (image != null ? "Present" : "Null") +
                '}';
    }
}
