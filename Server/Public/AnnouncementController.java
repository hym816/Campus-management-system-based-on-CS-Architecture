package Server.Public;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AnnouncementController implements Serializable {
    private static final String URL = "jdbc:mysql://"+Server.sqlip+":3306/my_database2";
    private static final String USER = Server.user;
    private static final String PASSWORD = Server.password;
    private static final String DATABASE = "my_database2";

    static Database db = new Database(URL,USER,PASSWORD);

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    static Message addAnnouncement(Message m) throws IOException {
        ClientController clientController = new ClientController();
        Announcement announcement = (Announcement) m.getContent().get(0);

        String id = announcement.getAnnouncementId();
        String content = announcement.getAnnouncementContent();
        String publisher = announcement.getAnnouncementTitle();
        Timestamp time = announcement.getPublicationTime();

        if(!isDupplicateAnnouncement(id)){
        Gson gson = new Gson();
        String json = gson.toJson(publisher);
        String query = "INSERT INTO announcement (announcementId,announcementContent,announcementTitle,publicationTime) VALUES (?,?,?,?)";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, id);
            preparedStatement.setString(2, content);
            preparedStatement.setString(3, json);
            preparedStatement.setTimestamp(4, time);

            int rowsInserted = preparedStatement.executeUpdate();  // 执行插入操作
            if (rowsInserted > 0) {
                System.out.println("公告添加成功!");
                Message message = new Message(Message.MessageType.success);
                return message;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }}


        else {
            System.out.println("已存在id为 "+id+" 的公告!");

        }
        Message message = new Message(Message.MessageType.failure);
        return message;
    }

    public static Message getAnnouncements() throws IOException {
        ClientController clientController =new ClientController();

        String SELECT_ANNOUNCEMENTS = "SELECT * FROM announcement";
        List<Announcement> announcements = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            // 假设 getConnection() 是一个有效的方法来获取数据库连接

            connection = getConnection();
            if (connection == null) {
                System.out.println("Failed to establish database connection.");
            } else {
                System.out.println("Connection established successfully.");
            }

            preparedStatement = connection.prepareStatement(SELECT_ANNOUNCEMENTS);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String id = resultSet.getString("announcementId");
                String title = resultSet.getString("announcementTitle");
                String content = resultSet.getString("announcementContent");
                Timestamp publishDate = resultSet.getTimestamp("publicationTime");

                Announcement announcement = new Announcement(id, title, content, publishDate);
                announcements.add(announcement);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        List<Serializable> temp = new ArrayList<>();
        temp.add((Serializable) announcements);
        Message m = new Message(Message.MessageType.send_all_announcement,temp);
        return m;
    }

    public static Message getAnnouncementsById(Message m) throws IOException {
        ClientController clientController = new ClientController();
        String id = (String) m.getContent().get(0);

        String SELECT_ANNOUNCEMENTS = "SELECT * FROM announcement WHERE announcementId = ?";

        Announcement announcement = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(SELECT_ANNOUNCEMENTS);
            preparedStatement.setString(1, id); // 设置第一个参数为 id
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String  announcementId = resultSet.getString("announcementId");
                String title = resultSet.getString("announcementTitle");
                String content = resultSet.getString("announcementContent");
                Timestamp publishDate = resultSet.getTimestamp("publicationTime");

                announcement = new Announcement(announcementId, title, content, publishDate);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        List<Serializable> temp = new ArrayList<>();
        temp.add(announcement);
        Message message = new Message(Message.MessageType.send_announcement_by_id,temp);
       return  message;
    }



    public static Message deleteAnnouncement(Message m)
    {

        Announcement announcement = (Announcement) m.getContent().get(0);
        String  announcementId = announcement.getAnnouncementId();
        //获取course信息from可视化界面


        if(isDupplicateAnnouncement(announcementId))
        {
            db.deleteFromDatabase("announcementId",announcementId,DATABASE,"announcement");
            Message message = new Message(Message.MessageType.success);
            return  message;
        }
        else
        {
            System.out.println("未查询到公告 "+announcementId+" !删除失败！");
        }
        Message message = new Message(Message.MessageType.failure);
        return  message;
    }

    public static Message editAnnouncement(Message m)
    {


        Announcement announcement = (Announcement) m.getContent().get(0);
        //获取更新信息from可视化界面
        if(isDupplicateAnnouncement(announcement.getAnnouncementId()))
        {
            String id = announcement.getAnnouncementId();
            String content = announcement.getAnnouncementContent();
            String publisher = announcement.getAnnouncementTitle();
            Timestamp time = announcement.getPublicationTime();
            Gson gson = new Gson();
            String json = gson.toJson(publisher);

            String query = "UPDATE announcement SET announcementContent = ?,announcementTitle = ?,publicationTime = ? WHERE announcementId = ?";
            try (Connection connection = getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                preparedStatement.setString(4, id);
                preparedStatement.setString(1, content);
                preparedStatement.setString(2, json);
                preparedStatement.setTimestamp(3,time);

                int rowsUpdated = preparedStatement.executeUpdate();  // 执行更新操作
                if (rowsUpdated > 0) {
                    System.out.println("公告更新成功!");
                }
                Message message = new Message(Message.MessageType.success);
                return message;

            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        else
        {
            System.out.println("没有编号为 "+announcement.getAnnouncementId()+" 的公告！");
            Message message = new Message(Message.MessageType.failure);
            return message;
        }


        Message message = new Message(Message.MessageType.failure);
        return message;
    }

    static boolean isDupplicateAnnouncement(String id)
    {

        String query = "SELECT announcementId FROM announcement WHERE announcementId = ?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0;  // 如果 count 大于 0，表示有重复的记录
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
