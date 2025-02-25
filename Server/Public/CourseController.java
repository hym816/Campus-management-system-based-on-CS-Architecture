package Server.Public;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CourseController implements Serializable {

    static ClientController clientController;

    static {
        try {
            clientController = new ClientController();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public CourseController() throws IOException {
    }


    private static final String URL = "jdbc:mysql://"+Server.sqlip+":3306/my_database2";
    private static final String USER = Server.user;
    private static final String PASSWORD = Server.password;
    private static final String DATABASE = "my_database2";


    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }


    static Database db = new Database(URL, USER, PASSWORD);


    public static Message addCourse(Message m) throws IOException {
        Course course =(Course) m.getContent().get(0);


        //获取course信息from可视化界面
        if (!isDupplicateCourse(course.getCourseId())) {
            String id = course.getCourseId();
            String name = course.getCourseName();
            String teacher = course.getCourseTeacher();
            double credits = course.getCourseCredits();
            String category = course.getCategory();
            int capacity = course.getCourseCapacity();
            String location = course.getLocation();
            int totalTime = course.getTotalHours();
            String teacherId = course.getTeacherId();
            int num = 0;
            Gson gson = new Gson();
            String json = gson.toJson(course.getTimes());
            String query = "INSERT INTO course (courseId,courseName,courseTeacher,courseCredits,category,courseCapacity,location,totalTime,courseTime,numSelected,teacherId) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
            db.addToDatabaseWithMultipleValues(query, id, name, teacher, credits, category, capacity, location, totalTime, json,num,teacherId);
            Message response = new Message(Message.MessageType.add_course_success);
            return response;

            /*try (Connection connection = getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, id);
                preparedStatement.setString(2, name);
                preparedStatement.setString(3, teacher);
                preparedStatement.setDouble(4, credits);
                preparedStatement.setString(5, category);
                preparedStatement.setInt(6, capacity);
                preparedStatement.setString(7, location);
                preparedStatement.setInt(8,totalTime);
                preparedStatement.setString(9, json);
                int rowsInserted = preparedStatement.executeUpdate();  // 执行插入操作
                if (rowsInserted > 0) {
                    System.out.println("课程添加成功!");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }*/
        } else {
            System.out.println("已存在课程代码为 " + (String) m.getContent().get(0) + " 的课程！");
            Message response = new Message(Message.MessageType.add_course_failure);
            return  response;
        }
    }

    public static String formatCourseTime(String jsonTime) {
        Gson gson = new Gson();

        // 定义 JSON 解析的类型
        Type listType = new TypeToken<List<Map<String, String>>>() {
        }.getType();

        // 解析 JSON 字符串为 List<Map<String, String>>
        List<Map<String, String>> timeSlots = gson.fromJson(jsonTime, listType);

        StringBuilder formattedTime = new StringBuilder();
        for (Map<String, String> timeSlot : timeSlots) {
            String dayOfWeek = timeSlot.get("dayOfWeek");
            String startTime = timeSlot.get("startTime");
            String endTime = timeSlot.get("endTime");
            formattedTime.append(dayOfWeek)
                    .append(" 第")
                    .append(startTime)
                    .append("-")
                    .append(endTime)
                    .append("节课")

                    .append("\n");
        }

        return formattedTime.toString().trim(); // 去掉最后一个换行符
    }

    public static String parseFormattedTime(String formattedTime) {
        Gson gson = new Gson();
        List<Map<String, String>> timeSlots = new ArrayList<>();

        String[] lines = formattedTime.split("\n");
        for (String line : lines) {
            Map<String, String> timeSlot = new HashMap<>();

            // 假设输入格式为 "周三 第3~5节课"
            String[] parts = line.split(" ");
            String dayOfWeek = parts[0];
            String timeRange = parts[1].substring(1, parts[1].length() - 2); // 获取"第3~5节课"中的"3~5"
            String[] times = timeRange.split("-");

            timeSlot.put("dayOfWeek", dayOfWeek);
            timeSlot.put("startTime", times[0]);
            timeSlot.put("endTime", times[1]);

            timeSlots.add(timeSlot);
        }

        // 将 List<Map<String, String>> 转换为 JSON 字符串
        return gson.toJson(timeSlots);
    }

    public static Message getAllCourses(Message m) throws IOException {

        CourseController courseController = new CourseController();
        ArrayList<Object[]> courseList = new ArrayList<>();
        try {

            // 建立数据库连接
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();

            // 执行SQL查询
            ResultSet rs = stmt.executeQuery("SELECT * FROM course");
            System.out.println("Query executed successfully.");
            // 处理结果集并填充数据
            while (rs.next()) {
                Object[] course = new Object[13];
                course[0] = rs.getString("courseId");
                course[1] = rs.getString("courseName");
                course[2] = rs.getString("courseTeacher");
                course[3] = rs.getString("teacherId");
                course[4] = rs.getString("category");
                course[7]= rs.getInt("numSelected");
                course[5] = rs.getDouble("courseCredits");
                course[6] = rs.getInt("courseCapacity");
                course[8] = rs.getInt("totalTime");
                String time = formatCourseTime(rs.getString("courseTime"));
                course[9] = time;
                course[10] = rs.getString("location");
                course[11] = "选课"; // 操作列的按钮文本
                course[12] = "退选"; // 操作列的按钮文本
                courseList.add(course);
            }

            // 关闭连接
            rs.close();
            stmt.close();
            conn.close();

            List<Serializable> temp = new ArrayList<>();
            temp.add(courseList);
            Message message = new Message(Message.MessageType.send_all_course, temp);
            return message;

        } catch (SQLException e) {
            e.printStackTrace();
            Message message = new Message(Message.MessageType.failure);
            return message;

        }

    }
    public static Message getAllCoursesManager(Message m) throws IOException {

        CourseController courseController = new CourseController();
        ArrayList<Object[]> courseList = new ArrayList<>();




        try {

            // 建立数据库连接
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();

            // 执行SQL查询
            ResultSet rs = stmt.executeQuery("SELECT * FROM course");
            System.out.println("Query executed successfully.");
            // 处理结果集并填充数据
            while (rs.next()) {
                Object[] course = new Object[13];
                course[0] = rs.getString("courseId");
                course[1] = rs.getString("courseName");
                course[2] = rs.getString("courseTeacher");
                course[3] = rs.getString("teacherId");
                course[4] = rs.getString("category");
                course[7]= rs.getInt("numSelected");
                course[5] = rs.getDouble("courseCredits");
                course[6] = rs.getInt("courseCapacity");
                course[8] = rs.getInt("totalTime");
                String time = formatCourseTime(rs.getString("courseTime"));
                course[9] = time;
                course[10] = rs.getString("location");
                course[11] = "查看"; // 操作列的按钮文本
                course[12] = "详情"; // 操作列的按钮文本
                courseList.add(course);
            }

            // 关闭连接
            rs.close();
            stmt.close();
            conn.close();

            List<Serializable> temp = new ArrayList<>();
            temp.add(courseList);
            Message message = new Message(Message.MessageType.send_all_course, temp);
            return message;

        } catch (SQLException e) {
            e.printStackTrace();
            Message message = new Message(Message.MessageType.failure);
            return message;

        }

    }

    public static Message getCourseByTeacherName(Message m) throws IOException {
        ClientController clientController1 = new ClientController();
        ArrayList<Object[]> courseList = new ArrayList<>();
        String teacherName = (String) m.getContent().get(0);
        try {
            // 建立数据库连接
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();

            // 执行SQL查询，筛选指定老师的课程
            String query = "SELECT * FROM course WHERE teacherId = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, teacherName);
            ResultSet rs = pstmt.executeQuery();

            // 处理结果集并填充数据
            while (rs.next()) {
                Object[] course = new Object[13];
                course[0] = rs.getString("courseId");
                course[1] = rs.getString("courseName");
                course[2] = rs.getString("courseTeacher");
                course[3]=rs.getString("teacherId");
                course[4] = rs.getString("category");
                course[7]= rs.getInt("numSelected");
                course[5] = rs.getDouble("courseCredits");
                course[6] = rs.getInt("courseCapacity");
                course[8] = rs.getInt("totalTime");
                String time = formatCourseTime(rs.getString("courseTime"));
                course[9] = time;
                course[10] = rs.getString("location");
                course[11] = "查看"; // 操作列的按钮文本
                 // 操作列的按钮文本
                courseList.add(course);
            }

            // 关闭连接
            rs.close();
            pstmt.close();
            conn.close();
            List<Serializable> temp = new ArrayList<>();
            temp.add(courseList);
            Message message = new Message(Message.MessageType.send_course_by_teachername, temp);
            return message;

        } catch (SQLException e) {
            e.printStackTrace();
            Message message = new Message(Message.MessageType.failure);
            return message;

        }


    }

    public static Message getCourses(Message m) throws SQLException, IOException {
        List<Course> courses = new ArrayList<>();
        String studentId = (String) m.getContent().get(0);
        String query = "SELECT courseId, courseTeacher, courseName, category, location, courseCapacity, totalTime, courseCredits, courseTime, courseStudent,numSelected,teacherId FROM course";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            // 设置查询参数


            // 执行查询
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String id = resultSet.getString("courseId");
                    String teacher = resultSet.getString("courseTeacher");
                    String name = resultSet.getString("courseName");
                    String cate = resultSet.getString("category");
                    String loca = resultSet.getString("location");
                    int capacity = resultSet.getInt("courseCapacity");
                    int hours = resultSet.getInt("totalTime");
                    double credits = resultSet.getDouble("courseCredits");
                    int num =resultSet.getInt("numSelected");
                    String teacherId = resultSet.getString("teacherId");

                    // 解析 JSON 格式的 coursetimes
                    String coursetimesJson = resultSet.getString("courseTime");
                    Gson gson = new Gson();
                    Type listType = new TypeToken<ArrayList<courseTime>>() {}.getType();
                    ArrayList<courseTime> coursetimes = gson.fromJson(coursetimesJson, listType);

                    // 解析 courseStudent 列
                    String courseStudentJson = resultSet.getString("courseStudent");
                    Type stuInfoListType = new TypeToken<ArrayList<stu_info>>() {}.getType();
                    ArrayList<stu_info> stuInfos = gson.fromJson(courseStudentJson, stuInfoListType);

                    // 检查学生是否存在
                    boolean containsStudent = stuInfos.stream()
                            .anyMatch(stu -> stu.getStudentId().equals(studentId));

                    if (containsStudent) {
                        Course course = new Course(id, teacher, name, cate, loca, capacity, hours, credits, coursetimes,num,teacherId);
                        courses.add(course);
                    }
                }
            }
        }

        // 构建返回的 Message
        List<Serializable> temp = new ArrayList<>();
        temp.add((Serializable) courses);
        return new Message(Message.MessageType.send_course_by_studentid, temp);
    }


    public static Message getCourses1(Message m) throws SQLException, IOException {
        List<Course> courses = new ArrayList<>();
        String Id = (String) m.getContent().get(0);  // 传入的teacherId
        String query = "SELECT courseId, courseTeacher, courseName, category, location, courseCapacity, totalTime, courseCredits, courseTime, courseStudent, numSelected, teacherId FROM course";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            // 执行查询
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String teacherId = resultSet.getString("teacherId");

                    // 判断 teacherId 是否与传入的 Id 相同
                    if (teacherId.equals(Id)) {
                        String id = resultSet.getString("courseId");
                        String teacher = resultSet.getString("courseTeacher");
                        String name = resultSet.getString("courseName");
                        String cate = resultSet.getString("category");
                        String loca = resultSet.getString("location");
                        int capacity = resultSet.getInt("courseCapacity");
                        int hours = resultSet.getInt("totalTime");
                        double credits = resultSet.getDouble("courseCredits");
                        int num = resultSet.getInt("numSelected");

                        // 解析 JSON 格式的 coursetimes
                        String coursetimesJson = resultSet.getString("courseTime");
                        Gson gson = new Gson();
                        Type listType = new TypeToken<ArrayList<courseTime>>() {}.getType();
                        ArrayList<courseTime> coursetimes = gson.fromJson(coursetimesJson, listType);

                        Course course = new Course(id, teacher, name, cate, loca, capacity, hours, credits, coursetimes, num, teacherId);
                        courses.add(course);
                    }
                }
            }
        }

        // 构建返回的 Message
        List<Serializable> temp = new ArrayList<>();
        temp.add((Serializable) courses);
        return new Message(Message.MessageType.send_course_by_teacherid, temp);
    }




    public static Message deleteCourse(Message m) throws IOException {
        ClientController clientController1 = new ClientController();
        //获取course信息from可视化界面
        Course course = (Course) m.getContent().get(0);
        if (isDupplicateCourse(course.getCourseId())) {
            String id = course.getCourseId();
            db.deleteFromDatabase("courseId", id, DATABASE, "course");
            Message message = new Message(Message.MessageType.success);
          return message;

            /*String query = "DELETE FROM course WHERE courseId = ?";
            try (Connection connection = getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, id);
                int rowsDeleted = preparedStatement.executeUpdate();  // 执行删除操作
                if (rowsDeleted > 0) {
                    System.out.println("课程删除成功!");
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }*/
        } else {
            System.out.println("未查询到课程 " + course.getCourseId() + " !删除失败！");
            Message message = new Message(Message.MessageType.failure);
            return message;


        }
    }

    public static Message updataCourse(Message m) {
        //获取更新信息from可视化界面

        Course course = (Course) m.getContent().get(0);
        if (isDupplicateCourse(course.getCourseId())) {
            String id = course.getCourseId();
            String name = course.getCourseName();
            String teacher = course.getCourseTeacher();
            double credits = course.getCourseCredits();
            String category = course.getCategory();
            int capacity = course.getCourseCapacity();
            String location = course.getLocation();
            int totalTime = course.getTotalHours();
            int num =course.getNumSelected();
            String teacherId = course.getTeacherId();
            Gson gson = new Gson();
            String json = gson.toJson(course.courseTimes);
            db.reviseStringByColumnName("courseId", id, DATABASE, "course", "courseName", name);
            db.reviseStringByColumnName("courseId", id, DATABASE, "course", "courseTeacher", teacher);

            db.reviseStringByColumnName("courseId", id, DATABASE, "course", "courseCredits", credits);

            db.reviseStringByColumnName("courseId", id, DATABASE, "course", "location", location);

            db.reviseStringByColumnName("courseId", id, DATABASE, "course", "category", category);

            db.reviseStringByColumnName("courseId", id, DATABASE, "course", "courseCapacity", capacity);

            db.reviseStringByColumnName("courseId", id, DATABASE, "course", "courseTime", json);

            db.reviseStringByColumnName("courseId", id, DATABASE, "course", "totalTime", totalTime);
            db.reviseStringByColumnName("courseId", id, DATABASE, "course", "numSelected", num);
            db.reviseStringByColumnName("courseId", id, DATABASE, "course", "teacherId", teacherId);

            Message message = new Message(Message.MessageType.success);
            return message;



            /*String query = "UPDATE course SET courseName = ?,courseTeacher = ?,courseCredits = ?,category = ?,courseCapacity = ?,location = ?,totalTime = ?,courseTime = ? WHERE courseId = ?";
            try (Connection connection = getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                preparedStatement.setString(9, id);
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, teacher);
                preparedStatement.setDouble(3, credits);
                preparedStatement.setString(4, category);
                preparedStatement.setInt(5, capacity);
                preparedStatement.setString(6, location);
                preparedStatement.setInt(7,totalTime);
                preparedStatement.setString(8, json);

                int rowsUpdated = preparedStatement.executeUpdate();  // 执行更新操作
                if (rowsUpdated > 0) {
                    System.out.println("课程更新成功!");
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }*/

        } else {
            System.out.println("没有课程编号为 " + course.getCourseId() + " 的课程！");
            Message message = new Message(Message.MessageType.failure);
            return message;
        }


    }

    void showStudentsList(String courseId) {
        if (isDupplicateCourse(courseId)) {

            String json = db.getValueByColumnName("courseId", courseId, DATABASE, "course", "courseStudent", String.class);
            Type listType = new TypeToken<List<stu_info>>() {
            }.getType(); // Use List instead of ArrayList to be more flexible
            List<stu_info> courseStus = new Gson().fromJson(json, listType);
            // Check if the parsing was successful and the list is not empty
            if (courseStus != null && !courseStus.isEmpty()) {
                for (stu_info courseStu : courseStus) {
                    System.out.println("Student Id: " + courseStu.getStudentId());
                    System.out.println("Student Name: " + courseStu.getStudentName());
                    System.out.println("Student Gender: " + courseStu.getStudentGender());
                    System.out.println("Student Score: " + courseStu.getScore());
                }
            } else {
                System.out.println("No students enrolled in this course.");
            }
            /*String json = null;
            ResultSet rs = null;
            String query = "SELECT courseStudent FROM course WHERE courseId = ?";

            try (Connection connection = getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                preparedStatement.setString(1, courseId);
                rs = preparedStatement.executeQuery();

                if (rs.next()) {
                    json = rs.getString("courseStudent");
                    System.out.println("Retrieved JSON: " + json);

                    // Define the type of the expected JSON array
                    Type listType = new TypeToken<List<stu_info>>() {}.getType(); // Use List instead of ArrayList to be more flexible
                    List<stu_info> courseStus = new Gson().fromJson(json, listType);

                    // Check if the parsing was successful and the list is not empty
                    if (courseStus != null && !courseStus.isEmpty()) {
                        for (stu_info courseStu : courseStus) {
                            System.out.println("Student Id: " + courseStu.getStudentId());
                            System.out.println("Student Name: " + courseStu.getStudentName());
                            System.out.println("Student Gender: " + courseStu.getStudentGender());
                            System.out.println("Student Score: " + courseStu.getScore());
                        }
                    } else {
                        System.out.println("No students enrolled in this course.");
                    }
                } else {
                    System.out.println("No data found.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (JsonParseException e) {
                System.err.println("Failed to parse JSON string: " + json);
                e.printStackTrace();
            }*/
        } else {
            System.out.println("未查询到课程编号为 " + courseId + " 的课程信息！");
        }
    }

    public static Message selectCourse(Message m) throws SQLException, IOException {
        Course course = (Course) m.getContent().get(0);
        Student student = (Student) m.getContent().get(1);



        int num = course.getNumSelected();
        int capacity = course.getCourseCapacity();

        Gson gson = new Gson();
        ResultSet rs = null;
        String courseId = course.getCourseId();
        String studentId = student.getStudentId();
        String jsonUpdated = null;
        stu_info StuInfo = student.getStu_info();
        ArrayList<courseTime> ct = course.courseTimes;
        String teacherId=course.getTeacherId();

        if (!isTimeConflict(course, studentId)&& num<capacity) {


            String query = "SELECT courseStudent FROM course WHERE courseId = ?";


            try (Connection connection = getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, courseId);
                rs = preparedStatement.executeQuery();

                if (rs.next()) {
                    String json = rs.getString("courseStudent");
                    List<stu_info> students = json == null || json.isEmpty() ? new ArrayList<>() : gson.fromJson(json, new TypeToken<List<stu_info>>() {
                    }.getType());
                    students.add(StuInfo);
                    jsonUpdated = gson.toJson(students);

                    System.out.println("Retrieved JSON: " + json);
                } else {
                    System.out.println("No data found.");
                }

                query = "UPDATE course SET courseStudent = ? WHERE courseId = ?";
                try (PreparedStatement updateStatement = connection.prepareStatement(query)) {
                    updateStatement.setString(1, jsonUpdated); // 设置要更新的新JSON字符串
                    updateStatement.setString(2, courseId); // 设置WHERE子句中的条件
                    int rowsUpdated = updateStatement.executeUpdate();  // 执行更新操作
                    if (rowsUpdated > 0) {
                        System.out.println("课程更新成功!");
                    }
                }

              db.reviseStringByColumnName("courseId",courseId,DATABASE,"course","numSelected",num+1);

                // 对学生选课列表执行类似的操作
                query = "SELECT course FROM student WHERE studentId = ?";
                try (PreparedStatement preparedStatement2 = connection.prepareStatement(query)) {
                    preparedStatement2.setString(1, studentId);
                    rs = preparedStatement2.executeQuery();
                    List<CourseDetail> courses = new ArrayList<>();

                    if (rs.next()) {
                        String json = rs.getString("course");
                        if (json != null && !json.isEmpty()) {
                            courses = gson.fromJson(json, new TypeToken<List<CourseDetail>>() {
                            }.getType());
                        }
                        // 查找是否已有课程，更新其时间
                        boolean courseExists = false;
                        for (CourseDetail detail : courses) {
                            if (detail.getCourseId().equals(courseId)) {
                                detail.setCourseTimes(course.courseTimes);
                                courseExists = true;
                                break;
                            }
                        }
                        if (!courseExists) {
                            courses.add(new CourseDetail(courseId, course.courseTimes));
                        }
                        jsonUpdated = gson.toJson(courses);

                        System.out.println("Retrieved JSON: " + json);
                    } else {
                        System.out.println("No data found.");
                    }

                    query = "UPDATE student SET course = ? WHERE studentId = ?";
                    try (PreparedStatement updateStatement = connection.prepareStatement(query)) {
                        updateStatement.setString(1, jsonUpdated); // 设置要更新的新JSON字符串
                        updateStatement.setString(2, studentId); // 设置WHERE子句中的条件
                        int rowsUpdated = updateStatement.executeUpdate();  // 执行更新操作
                        if (rowsUpdated > 0) {
                            System.out.println("学生课程更新成功!");
                        }
                    }
                } catch (SQLException | JsonSyntaxException e) {
                    e.printStackTrace();
                }
                Message message = new Message(Message.MessageType.success);
                return message;
            }
        } else {
            System.out.println("选课失败！");
            Message message = new Message(Message.MessageType.failure);
            return message;
        }
    }

    public static Message unselectCourse(Message m) throws IOException {

        Course course = (Course) m.getContent().get(0);
        Student student = (Student) m.getContent().get(1);
        String courseId = course.getCourseId();
        String studentId = student.getStudentId();
        Gson gson = new Gson();

        int num = course.getNumSelected();
        int capacity = course.getCourseCapacity();

        // 1. 检查学生是否已选择该课程
        String queryStudent = "SELECT course FROM student WHERE studentId = ?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(queryStudent)) {

            preparedStatement.setString(1, studentId);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                String json = rs.getString("course");
                List<CourseDetail> courses = json == null || json.isEmpty() ? new ArrayList<>() : gson.fromJson(json, new TypeToken<List<CourseDetail>>() {
                }.getType());

                // 检查课程是否在学生的选课列表中
                boolean courseSelected = courses.stream().anyMatch(courseDetail -> courseId.equals(courseDetail.getCourseId()));
                if (!courseSelected) {
                    System.out.println("学生未选择该课程，无法退课！");
                    return new Message(Message.MessageType.failure);
                }

                // 继续执行退课逻辑
                courses.removeIf(courseDetail -> courseId.equals(courseDetail.getCourseId()));
                String updatedJson = gson.toJson(courses);

                String updateStudentQuery = "UPDATE student SET course = ? WHERE studentId = ?";
                try (PreparedStatement updateStatement = connection.prepareStatement(updateStudentQuery)) {
                    updateStatement.setString(1, updatedJson);
                    updateStatement.setString(2, studentId);
                    updateStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 2. 从课程表中删除学生
        String queryCourse = "SELECT courseStudent FROM course WHERE courseId = ?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(queryCourse)) {

            preparedStatement.setString(1, courseId);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                String json = rs.getString("courseStudent");
                List<stu_info> students = json == null || json.isEmpty() ? new ArrayList<>() : gson.fromJson(json, new TypeToken<List<stu_info>>() {
                }.getType());
                students.removeIf(studentInfo -> studentId.equals(studentInfo.getStudentId()));
                String updatedJson = gson.toJson(students);

                String updateCourseQuery = "UPDATE course SET courseStudent = ? WHERE courseId = ?";
                try (PreparedStatement updateStatement = connection.prepareStatement(updateCourseQuery)) {
                    updateStatement.setString(1, updatedJson);
                    updateStatement.setString(2, courseId);
                    updateStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 更新选课人数
        db.reviseStringByColumnName("courseId", courseId, DATABASE, "course", "numSelected", num - 1);

        Message message = new Message(Message.MessageType.success);
        return message;
    }



    static boolean isDupplicateCourse(String id) {
        String get = db.getValueByColumnName("courseId", id, DATABASE, "course", "courseId", String.class);
        return get != null;

        /*String query = "SELECT courseId FROM course WHERE courseId = ?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();  // 如果有下一行，则返回 true
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;  // 发生异常时返回 false
        }*/


    }

    void displayAllCourseInfo() {
        String query = "SELECT * FROM course";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet rs = preparedStatement.executeQuery(query);
            ResultSetMetaData metaData = rs.getMetaData();
            int columns = metaData.getColumnCount();
            for (int i = 1; i <= columns; ++i) {
                System.out.print(metaData.getColumnName(i) + "\t");
            }
            System.out.println(); // 换行

            // 输出数据行
            while (rs.next()) {
                for (int i = 1; i <= columns; ++i) {
                    System.out.print(rs.getString(i) + "\t");
                }
                System.out.println(); // 换行
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void queryCourses(String inputStr) {
        String query1 = "SELECT * FROM course WHERE courseName LIKE ?";
        String query2 = "SELECT * FROM course WHERE courseId LIKE ?";

        try (Connection connection = getConnection()) {
            // 处理第一个查询
            try (PreparedStatement preparedStatement = connection.prepareStatement(query1)) {
                preparedStatement.setString(1, "%" + inputStr + "%");
                ResultSet rs = preparedStatement.executeQuery();
                processResultSet(rs);
            }

            // 处理第二个查询
            try (PreparedStatement preparedStatement = connection.prepareStatement(query2)) {
                preparedStatement.setString(1, "%" + inputStr + "%");
                ResultSet rs = preparedStatement.executeQuery();
                processResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void processResultSet(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int columns = metaData.getColumnCount();

        // 输出列名
        for (int i = 1; i <= columns; ++i) {
            System.out.print(metaData.getColumnName(i) + "\t");
        }
        System.out.println(); // 换行

        // 输出数据行
        while (rs.next()) {
            for (int i = 1; i <= columns; ++i) {
                System.out.print(rs.getString(i) + "\t");
            }
            System.out.println(); // 换行
        }
    }

    void evaluateScore(String studentId, String courseId, String score) {
        String json = null;
        Gson gson = new Gson();
        json = db.getValueByColumnName("courseId", courseId, DATABASE, "course", "courseStudent", String.class);
        // 解析数据
        Type listType = new TypeToken<ArrayList<stu_info>>() {
        }.getType();
        List<stu_info> students = gson.fromJson(json, listType);

        // 修改数据
        for (stu_info student : students) {
            if (studentId.equals(student.getStudentId())) {
                student.setScore(score); // 修改成绩
                break;
            }
        }

        String updatedJson = gson.toJson(students);
        db.reviseStringByColumnName("courseId", courseId, DATABASE, "course", "courseStudent", updatedJson);

       /* String querySelect = "SELECT courseStudent FROM course WHERE courseId = ?";
        String queryUpdate = "UPDATE course SET courseStudent = ? WHERE courseId = ?";
        Gson gson = new Gson();
        String json = null;
        ResultSet rs = null;
        String jsonUpdated = null   ;
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(querySelect)) {
            preparedStatement.setString(1, courseId);
            rs = preparedStatement.executeQuery();
            if (rs.next()) {
                String jsonData = rs.getString("courseStudent");

                // 解析数据
                Type listType = new TypeToken<ArrayList<stu_info>>(){}.getType();
                List<stu_info> students = gson.fromJson(jsonData, listType);

                // 修改数据
                for (stu_info student : students) {
                    if (studentId.equals(student.getStudentId())) {
                        student.setScore(score); // 修改成绩
                        break;
                    }
                }

                PreparedStatement pstmtUpdate = connection.prepareStatement(queryUpdate);
                String updatedJson = gson.toJson(students);
                pstmtUpdate.setString(1, updatedJson);
                pstmtUpdate.setString(2, courseId);
                int rowsUpdated = pstmtUpdate.executeUpdate();

                System.out.println(rowsUpdated + " row(s) updated.");

            } else {
                System.out.println("No data found.");
            }



        } catch (SQLException e) {
            e.printStackTrace();
        }*/

    }


    static boolean isTimeConflict(Course course, String studentId) {
        String json = db.getValueByColumnName("studentId", studentId, DATABASE, "student", "course", String.class);
        if (json == null || json.isEmpty()) {
            return false; // 如果没有选课，则没有时间冲突
        }

        // 解析学生已选课程的时间信息
        Gson gson = new Gson();
        Type listType = new TypeToken<List<CourseDetail>>() {
        }.getType();
        List<CourseDetail> existingCourses = gson.fromJson(json, listType);

        // 获取新课程的时间安排
        List<courseTime> newCourseTimes = course.courseTimes;

        // 遍历已选课程并检查时间冲突
        for (CourseDetail existingCourse : existingCourses) {
            for (courseTime existingTime : existingCourse.getCourseTimes()) {
                for (courseTime newTime : newCourseTimes) {
                    if (isConflict(existingTime, newTime)) {
                        return true; // 如果发现冲突，返回true
                    }
                }
            }
        }

        return false;
    }

    private static boolean isConflict(courseTime time1, courseTime time2) {
        // 比较两个时间是否冲突
        if (!time1.getDayOfWeek().equals(time2.getDayOfWeek())) {
            return false; // 如果在不同的星期天上课，没有冲突
        }

        // 检查时间是否重叠
        return !(time1.getEndTime().compareTo(time2.getStartTime()) <= 0 || time1.getStartTime().compareTo(time2.getEndTime()) >= 0);


    }


    public static Message getStudentByCourseId(Message m) throws IOException {
        ClientController clientController = new ClientController();
        String courseId = (String) m.getContent().get(0);
        ArrayList<stu_info> stuInfos = new ArrayList<>();
        try (Connection connection = getConnection()) {
            String query = "SELECT courseStudent FROM course WHERE courseId = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, courseId);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String courseStudentJson = resultSet.getString("courseStudent");
                Gson gson = new Gson();
                Type stuInfoListType = new TypeToken<ArrayList<stu_info>>() {
                }.getType();
                stuInfos = gson.fromJson(courseStudentJson, stuInfoListType);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        List<Serializable>temp =new ArrayList<>();
        temp.add(stuInfos);
        Message message = new Message(Message.MessageType.send_students_by_courseid,temp);
        return message;
    }
    public  static Message saveScores(Message m) throws IOException {
        ClientController clientController1 = new ClientController();
        String courseId = (String) m.getContent().get(0);
        String courseStudentJson = (String) m.getContent().get(1);

         try (Connection connection = getConnection()) {
            String updateQuery = "UPDATE course SET courseStudent = ? WHERE courseId = ?";
            PreparedStatement updateStatement = connection.prepareStatement(updateQuery);

            // Read the current data and update scores
            Gson gson = new Gson();

            System.out.println("Generated JSON: " + courseStudentJson);

            updateStatement.setString(1, courseStudentJson);
            updateStatement.setString(2, courseId);

            updateStatement.executeUpdate();

            Message message =new Message(Message.MessageType.success);
            return message;


        } catch (SQLException e) {
            e.printStackTrace();
            Message message = new Message(Message.MessageType.failure);
           return  message;

        }

    }


    public static Message getStudentByStudentId(Message m) throws IOException {
        ClientController clientController = new ClientController();

        // 从消息中获取学生 ID
        String studentId = (String) m.getContent().get(0);

        // 存储学生信息的列表

        String name =null;
        String gender =null ;
        try (Connection connection = getConnection()) {
            // 查询学生表中的信息
            String query = "SELECT studentId, studentName, studentGender FROM student WHERE studentId = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, studentId);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {

                 name = resultSet.getString("studentName");
                 gender = resultSet.getString("studentGender");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 将学生信息列表放入消息中
        Student student = new Student(studentId,name,gender,null);


        List<Serializable> temp = new ArrayList<>();
       temp.add(student);
        Message message = new Message(Message.MessageType.send_students_by_studentid, temp);
        return message;
    }

    public static Message getScore(Message m) throws SQLException, IOException {
        List<Object[]> courseDataList = new ArrayList<>();
        String studentId = (String) m.getContent().get(0);
        String query = "SELECT courseId, courseTeacher, courseName, category, location, courseCapacity, totalTime, courseCredits, courseTime, courseStudent, numSelected, teacherId FROM course";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            // 执行查询
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String id = resultSet.getString("courseId");
                    String name = resultSet.getString("courseName");
                    String cate = resultSet.getString("category");
                    double credits = resultSet.getDouble("courseCredits");

                    // 解析 JSON 格式的 coursetimes
                    String coursetimesJson = resultSet.getString("courseTime");
                    Gson gson = new Gson();
                    Type listType = new TypeToken<ArrayList<courseTime>>() {}.getType();
                    ArrayList<courseTime> coursetimes = gson.fromJson(coursetimesJson, listType);

                    // 解析 courseStudent 列
                    String courseStudentJson = resultSet.getString("courseStudent");
                    Type stuInfoListType = new TypeToken<ArrayList<stu_info>>() {}.getType();
                    ArrayList<stu_info> stuInfos = gson.fromJson(courseStudentJson, stuInfoListType);

                    // 查找是否有该学生，并获取其分数
                    stu_info foundStudent = stuInfos.stream()
                            .filter(stu -> stu.getStudentId().equals(studentId))
                            .findFirst()
                            .orElse(null);

                    if (foundStudent != null) {
                        // 创建课程信息数据，包含课程标号、课程名称、课程类型、学分和分数
                        Object[] courseData = new Object[5];
                        courseData[0] = id;
                        courseData[1] = name;
                        courseData[2] = cate;
                        courseData[3] = credits;
                        courseData[4] = foundStudent.getScore();  // 获取学生的分数

                        // 添加到列表中
                        courseDataList.add(courseData);
                    }
                }
            }
        }

        // 将列表转换为二维数组
        Object[][] coursesArray = new Object[courseDataList.size()][5];
        for (int i = 0; i < courseDataList.size(); i++) {
            coursesArray[i] = courseDataList.get(i);
        }

        // 构建返回的 Message
        List<Serializable> temp = new ArrayList<>();
        temp.add(coursesArray);
        return new Message(Message.MessageType.send_score_by_studentid, temp);
    }
}






