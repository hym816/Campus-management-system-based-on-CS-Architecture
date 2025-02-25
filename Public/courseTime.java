package Server.Public;

import java.io.Serializable;

public class courseTime implements Serializable
{
    private static final long serialVersionUID = 1L;
    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    String startTime;
    String endTime;
    String dayOfWeek;

    public courseTime(String start, String end, String day)
    {
        setEndTime(end);
        setStartTime(start);
        setDayOfWeek(day);
    }

    public String toString() {
        return "courseTime{" +
                "startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", dayOfWeek='" + dayOfWeek + '\'' +
                '}';
    }
}
