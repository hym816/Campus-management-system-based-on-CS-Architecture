package Server.Public;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Course implements Serializable {
    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseTeacher() {
        return courseTeacher;
    }

    public void setCourseTeacher(String courseTeacher) {
        this.courseTeacher = courseTeacher;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getCourseCapacity() {
        return courseCapacity;
    }

    public void setCourseCapacity(int courseCapacity) {
        this.courseCapacity = courseCapacity;
    }

    public double getCourseCredits() {
        return courseCredits;
    }

    public void setCourseCredits(double courseCredits) {
        this.courseCredits = courseCredits;
    }

    public int getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(int totalHours) {
        this.totalHours = totalHours;
    }



    public Course(String id, String teacher, String name, String cate, String loca, int capacity, int hours, double credits, ArrayList<courseTime> coursetimes, int num, String teacherid)
    {
        setTeacherId(teacherid);
        setCourseId(id);
        setCourseName(name);
        setCategory(cate);
        setCourseCapacity(capacity);
        setCourseCredits(credits);
        setCourseTeacher(teacher);
        setLocation(loca);
        setTotalHours(hours);
        setNumSelected(num);
        this.courseTimes = new ArrayList<>();
        this.students = new ArrayList<>();
        this.courseTimes = coursetimes;

    }
    String courseId;
    String courseTeacher;
    String location;
    String category;
    String courseName;
    int courseCapacity;

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    String teacherId;

    public int getNumSelected() {
        return numSelected;
    }

    public void setNumSelected(int numSelected) {
        this.numSelected = numSelected;
    }

    int numSelected = 0;
    double courseCredits;
    int totalHours;
    ArrayList<courseTime> courseTimes = null;
    ArrayList<stu_info> students = null;

    public List<courseTime> getTimes() {
        return courseTimes;
    }
}
