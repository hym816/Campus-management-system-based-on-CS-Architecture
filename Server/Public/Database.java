package Server.Public;

import com.mysql.cj.jdbc.MysqlDataSource;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class Database {
    String url;
    String user;
    String password;

    Connection connection = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet;

    public Database(String url, String user, String password){
        this.url = url;
        this.user = user;
        this.password = password;
        try {
            MysqlDataSource mysqlDataSource = new MysqlDataSource();
            mysqlDataSource.setURL(url);
            mysqlDataSource.setUser(user);
            mysqlDataSource.setPassword(password);
            connection = (Connection) mysqlDataSource.getConnection();
            System.out.println("数据库连接成功");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    //查询，根据id，获取相关表中某一列的对应信息，最后一个参数输入需要返回的值，例返回String类型，则输入String.class
    //columnName表示唯一标识列表名，knownValue表示columnName列的一个已知值，databaseName表示数据库名字，
    // chartName表示表格名字，targetColumnName表示目标查询列名，type表示返回类型，如String.class，int.class，double.class
    public <T> T getValueByColumnName(String columnName, String knownValue, String databaseName,
    		String chartName, String targetColumnName, Class<T> type){
        //数据库查询指令
        String sql = "SELECT "+targetColumnName+" FROM "+databaseName+"."+chartName+" WHERE "+columnName+" = ?";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,knownValue);
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                Object result = resultSet.getObject(targetColumnName);
                if(type.isInstance(result)){
                    return type.cast(result);
                }
            }
            else{
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                    if (preparedStatement != null) preparedStatement.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return null;
    }
    
    //修改，新增newValue，表示修改后的值
    public<T> void reviseStringByColumnName(String columnName, String knownValue, 
    		String databaseName, String chartName, String targetColumnName, T newValue){
        //数据库更新指令
        String sql = "UPDATE "+databaseName+"."+chartName+" SET "+targetColumnName+" = ? WHERE "+columnName+" = ?";
        int flag;
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setObject(1,newValue);
            //preparedStatement.setString(1,newValue);
            preparedStatement.setString(2,knownValue);
            flag = preparedStatement.executeUpdate();
            if(flag!=0){
                System.out.println(targetColumnName+"修改成功");
            }
            else{
                System.out.println(targetColumnName+"修改失败");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    //删除
    public<T> void deleteFromDatabase(String columnName, T knownValue, String databaseName,
    		String chartName){
        //数据库删除指令
        String sql = "DELETE FROM "+databaseName+"."+chartName+" WHERE "+columnName+" = ?";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setObject(1,knownValue);
            int affectedRows = preparedStatement.executeUpdate();
            if(affectedRows != 0){
                System.out.println("删除成功");
            }else{
                System.out.println("删除失败");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error deleting row: " + e.getMessage(), e);
        }finally {
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    //增加
    public<T> void addToDatabase(String databaseName, String chartName, String idColumnName,
    		T newId, String passwordColumnName){
        //数据库删除指令
        String sql = "INSERT INTO "+databaseName+"."+chartName+" ("+idColumnName+", "+passwordColumnName+") VALUES (?, ?)";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setObject(1,newId);
            preparedStatement.setObject(2,"password");
            int affectedRows = preparedStatement.executeUpdate();
            if(affectedRows != 0){
                System.out.println("数据插入成功");
            }else{
                System.out.println("数据插入失败");
            }
        } catch (SQLException e) {
            throw new RuntimeException("数据库插入错误: " + e.getMessage(), e);
        }
    }
    
    
    
    
    public <T> void addToDatabaseWithMultipleValues(String sql, Object... values) {
        try {
            preparedStatement = connection.prepareStatement(sql);
            for (int i = 0; i < values.length; i++) {
                preparedStatement.setObject(i + 1, values[i]);
            }
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows != 0) {
                System.out.println("数据插入成功");
            } else {
                System.out.println("数据插入失败");
            }
        } catch (SQLException e) {
            throw new RuntimeException("数据库插入错误: " + e.getMessage(), e);
        } finally {
            try {
                if (preparedStatement != null) preparedStatement.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // 新增方法：获取所有学生 ID
    public List<String> getAllIds(String databaseName, String chartName, String idColumnName) {
        List<String> ids = new ArrayList<>();
        String sql = "SELECT " + idColumnName + " FROM " + databaseName + "." + chartName;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                ids.add(resultSet.getString(idColumnName));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("无法获取所有ID: " + e.getMessage(), e);
        }

        return ids;
    }

    // 插入图片到avatar列的方法
    public void insertImageByStudentId(String tableName, String imagePath, String studentId) {
        String sql = "UPDATE " + tableName + " SET avatar = ? WHERE student_id = ?";
        try (FileInputStream fis = new FileInputStream(imagePath)) {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setBinaryStream(1, fis, fis.available());
            preparedStatement.setString(2, studentId);
            int rows = preparedStatement.executeUpdate();
            if (rows > 0) {
                System.out.println("图片插入成功，学生ID: " + studentId);
            } else {
                System.out.println("未找到对应的学生ID: " + studentId + "，图片插入失败");
            }
        } catch (SQLException | IOException e) {
            throw new RuntimeException("插入图片时出现错误: " + e.getMessage(), e);
        } finally {
            closePreparedStatement();
        }
    }

    // 读取图片方法
    public void readImageByStudentId(String tableName, String columnName, String studentId, String outputPath) {
        String sql = "SELECT " + columnName + " FROM " + tableName + " WHERE student_id = ?";
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, studentId);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                InputStream input = resultSet.getBinaryStream(columnName);
                FileOutputStream fos = new FileOutputStream(outputPath);
                byte[] buffer = new byte[1024];
                int bytesRead = -1;
                while ((bytesRead = input.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
                fos.close();
                System.out.println("图片读取成功，保存到: " + outputPath);
            } else {
                System.out.println("未找到对应的学生ID: " + studentId);
            }
        } catch (SQLException | IOException e) {
            throw new RuntimeException("读取图片时出现错误: " + e.getMessage(), e);
        } finally {
            closeResultSet();
            closePreparedStatement();
        }
    }

    // 关闭 PreparedStatement
    private void closePreparedStatement() {
        if (preparedStatement != null) {
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // 关闭 ResultSet
    private void closeResultSet() {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public <T> void reviseStringByColumnName(String columnName, Object knownValue,
                                             String databaseName, String chartName, String targetColumnName, T newValue) {
        // 构建 SQL 更新指令
        String sql = "UPDATE " + databaseName + "." + chartName + " SET " + targetColumnName + " = ? WHERE " + columnName + " = ?";
        int flag;
        try {
            preparedStatement = connection.prepareStatement(sql);

            // 设置 newValue 的参数，根据其类型
            if (newValue instanceof Integer) {
                preparedStatement.setInt(1, (Integer) newValue);
            } else if (newValue instanceof String) {
                preparedStatement.setString(1, (String) newValue);
            } else if (newValue instanceof Double) {
                preparedStatement.setDouble(1, (Double) newValue);
            } else {
                preparedStatement.setObject(1, newValue); // 泛型处理其他可能的类型
            }

            // 设置 knownValue 的参数，根据其类型
            if (knownValue instanceof Integer) {
                preparedStatement.setInt(2, (Integer) knownValue);
            } else if (knownValue instanceof String) {
                preparedStatement.setString(2, (String) knownValue);
            } else {
                throw new IllegalArgumentException("Unsupported type for knownValue: " + knownValue.getClass().getName());
            }

            flag = preparedStatement.executeUpdate();
            if (flag != 0) {
                System.out.println(targetColumnName + "修改成功");
            } else {
                System.out.println(targetColumnName + "修改失败");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            // 确保关闭 preparedStatement 资源
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    //获取相同条件下的所有信息
    //knowvalue即为已知的相同条件
    public <T> List<T> getAllValueByColumnName(String columnName, String knownValue, String databaseName,
                                               String chartName, String targetColumnName, Class<T> type){
        //数据库查询指令
        String sql = "SELECT "+targetColumnName+" FROM "+databaseName+"."+chartName+" WHERE "+columnName+" = ?";
        List<T> list = new ArrayList<>();
        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,knownValue);
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                Object result = resultSet.getObject(targetColumnName);
                if(type.isInstance(result)){
                    list.add(type.cast(result));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                    if (preparedStatement != null) preparedStatement.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    // 获取当前表格中最大的hid值，并将其加1
    public int getNextHid() {
        int nextHid = 1; // 如果表为空，默认从1开始
        String sql = "SELECT MAX(CAST(hid AS UNSIGNED)) AS max_hid FROM my_database2.history"; // 使用CAST将hid转换为数字

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            if (resultSet.next()) {
                // 从结果集中获取最大值，并进行类型转换
                int maxHid = resultSet.getInt("max_hid");
                nextHid = maxHid + 1; // 获取最大值并加1
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // 打印详细的SQL错误信息
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("Message: " + e.getMessage());
        }

        return nextHid;
    }

    public int getNextRid() {
        int nextRid = 1; // 如果表为空，默认从1开始
        String sql = "SELECT MAX(CAST(rid AS UNSIGNED)) AS max_rid FROM my_database2.bookrecommend"; // 使用CAST将rid转换为数字

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            if (resultSet.next()) {
                // 从结果集中获取最大值，并进行类型转换
                int maxRid = resultSet.getInt("max_rid");
                nextRid = maxRid + 1; // 获取最大值并加1
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // 打印详细的SQL错误信息
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("Message: " + e.getMessage());
        }
        return nextRid;
    }


    public List<String> getTopBooksByTimes() {
        List<String> topBooks = new ArrayList<>();
        // SQL 查询：将 times 从 VARCHAR 转换为 INT 类型进行排序，确保返回按借阅次数从高到低的 bid
        String sql = "SELECT bid FROM my_database2.book ORDER BY CAST(times AS SIGNED) DESC LIMIT 10";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            // 将每本书的编号（bid）按借阅次数从高到低加入列表
            while (resultSet.next()) {
                topBooks.add(resultSet.getString("bid"));  // 获取书籍编号（bid）列的值
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return topBooks;
    }


    public List<String> getTopReadersByTimes() {
        List<String> topReaders = new ArrayList<>();
        // SQL 查询：将 count 从 VARCHAR 转换为 INT 类型进行排序，确保返回按借阅次数从高到低的 uid
        String sql = "SELECT uid FROM my_database2.reader ORDER BY CAST(count AS SIGNED) DESC LIMIT 5";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            // 将每个读者的 uid 按借阅次数从高到低加入列表
            while (resultSet.next()) {
                topReaders.add(resultSet.getString("uid"));  // 获取读者唯一标识符（uid）列的值
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return topReaders;
    }

    public void updateTopReader(String uid, String readerName) {
        String sql = "INSERT INTO my_database2.reader (uid, name, count) " +
                "VALUES (?, ?, 1) " +
                "ON DUPLICATE KEY UPDATE count = count + 1";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, uid); // 设置uid参数
            preparedStatement.setString(2, readerName); // 设置reader_name参数

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("更新或插入操作成功");
            } else {
                System.out.println("没有更新或插入任何记录");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("更新topReader表时发生错误: " + e.getMessage(), e);
        }
    }


    public static void main(String[] args) {
        Database db = new Database("jdbc:mysql://10.208.126.254/test", "haoyumin", "000000-Aa");

        // 测试插入图片到指定 student_id 的 avatar 列
        db.insertImageByStudentId("Student_Information", "/Users/haoyumin/Desktop/PersonalInfo.png", "9022119");

        // 测试读取图片（假设读取 id 为 1 的图片并保存）
        db.readImageByStudentId("Student_Information", "avatar", "9022119", "output_image.jpg");
    }
}    






