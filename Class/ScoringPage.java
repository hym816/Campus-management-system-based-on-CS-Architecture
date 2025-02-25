package Server.Class;

import Server.Public.ClientController;
import Server.Public.Message;
import Server.Public.stu_info;
import com.google.gson.Gson;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ScoringPage extends JDialog {
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private JButton saveButton;
    private String courseId;

    public ScoringPage(Frame parent, String courseId) throws IOException, ClassNotFoundException {
        super(parent, "Score Students", true);
        this.courseId = courseId;

        setLayout(new BorderLayout());
        setSize(600, 400);

        // Initialize UI components
        tableModel = new DefaultTableModel(new String[]{"学号", "姓名", "性别", "分数"}, 0);
        studentTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(studentTable);
        add(scrollPane, BorderLayout.CENTER);

        saveButton = new JButton("保存");
        add(saveButton, BorderLayout.SOUTH);

        // Load students
        loadStudentsForCourse(courseId);

        // Add button listener
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    saveScores();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                } catch (ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        setVisible(true);
    }

    private void loadStudentsForCourse(String courseId) throws IOException, ClassNotFoundException {

        ArrayList<stu_info> stuInfos = new ArrayList<>();
        ClientController clientController = new ClientController();
        List<Serializable> temp = new ArrayList<>();
        temp.add(courseId);
        clientController.sendMessage(new Message(Message.MessageType.get_students_by_courseid,temp));
        Message response = clientController.receiveMessage();
        stuInfos = (ArrayList<stu_info>) response.getContent().get(0);
        // Populate table with student data
        for (stu_info stuInfo : stuInfos) {
            tableModel.addRow(new Object[]{stuInfo.getStudentId(), stuInfo.getStudentName(),stuInfo.getStudentGender(), stuInfo.getScore()});
        }
        /*
        try (Connection connection = getConnection()) {
            String query = "SELECT courseStudent FROM course WHERE courseId = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, courseId);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String courseStudentJson = resultSet.getString("courseStudent");
                Gson gson = new Gson();
                java.lang.reflect.Type stuInfoListType = new TypeToken<ArrayList<stu_info>>() {}.getType();
                ArrayList<stu_info> stuInfos = gson.fromJson(courseStudentJson, stuInfoListType);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading student data.", "Error", JOptionPane.ERROR_MESSAGE);
        }*/
    }

    private void saveScores() throws IOException, ClassNotFoundException {
        String courseStudentJson = getCourseStudentJson();
        ClientController clientController =new ClientController();
        List<Serializable>temp = new ArrayList<>();
        temp.add(courseId);
        temp.add(courseStudentJson);
        clientController.sendMessage(new Message(Message.MessageType.save_score,temp));
        Message response = clientController.receiveMessage();
        if(response.getType().equals(Message.MessageType.success))
            JOptionPane.showMessageDialog(this, "保存成功", "成功", JOptionPane.INFORMATION_MESSAGE);
        else
            JOptionPane.showMessageDialog(this, "保存失败", "失败", JOptionPane.ERROR_MESSAGE);


      /* try (Connection connection = getConnection()) {
            String updateQuery = "UPDATE course SET courseStudent = ? WHERE courseId = ?";
            PreparedStatement updateStatement = connection.prepareStatement(updateQuery);

            // Read the current data and update scores
            Gson gson = new Gson();
            String courseStudentJson = getCourseStudentJson();
            System.out.println("Generated JSON: " + courseStudentJson);

            updateStatement.setString(1, courseStudentJson);
            updateStatement.setString(2, courseId);

            updateStatement.executeUpdate();

            JOptionPane.showMessageDialog(this, "Scores saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving scores.", "Error", JOptionPane.ERROR_MESSAGE);
        }*/
    }

    private String getCourseStudentJson() {
        ArrayList<stu_info> stuInfos = new ArrayList<>();

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String studentId = (String) tableModel.getValueAt(i, 0);
            String studentName = (String) tableModel.getValueAt(i, 1);
            String studentGender = (String) tableModel.getValueAt(i, 2);
            String score = (String) tableModel.getValueAt(i, 3); // 分数作为 String 类型

            stu_info stuInfo = new stu_info(studentId, studentName, studentGender, score);
            stuInfos.add(stuInfo);
        }

        Gson gson = new Gson();
        return gson.toJson(stuInfos);
    }

}
