package Server.Public;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

class CourseDetail implements Serializable {
    private String courseId;
    private List<courseTime> courseTimes;

    public CourseDetail(String courseId, ArrayList<courseTime> courseTimes) {
        this.courseId = courseId;
        this.courseTimes = courseTimes;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public List<courseTime> getCourseTimes() {
        return courseTimes;
    }

    public void setCourseTimes(ArrayList<courseTime> courseTimes) {
        this.courseTimes = courseTimes;
    }
}
