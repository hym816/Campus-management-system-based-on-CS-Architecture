package Server.Public;

import java.io.Serializable;

public class stu_info implements  Serializable
{

    String studentId;

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentGender() {
        return studentGender;
    }

    public void setStudentGender(String studentGender) {
        this.studentGender = studentGender;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = String.valueOf(score);
    }

    String studentName;
    String studentGender;
    String score;

    public stu_info(String id, String name, String gender, String sc)
    {
        setScore(sc);
        setStudentGender(gender);
        setStudentId(id);
        setStudentName(name);
    }

}
