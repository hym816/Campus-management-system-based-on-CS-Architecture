package Server.Public;

import java.io.Serializable;
import java.util.ArrayList;

public class Student implements Serializable {
String studentId;
String gender;

    public ArrayList<String> getCourses() {
        return courses;
    }

    public void setCourses(ArrayList<String> courses) {
        this.courses = courses;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    stu_info getStu_info()
    {
        stu_info stu = new stu_info(getStudentId(),getStudentName(),getGender(),"0");
        return stu;
    }
    String studentName;
ArrayList<String> courses;
public Student(String id, String name, String gen, String course)
{
    setStudentId(id);
    setStudentName(name);
    setGender(gen);
    this.courses = new ArrayList<>();
    courses.add(course);

}


}
