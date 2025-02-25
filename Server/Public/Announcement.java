package Server.Public;

import java.io.Serializable;
import java.sql.Timestamp;



public class Announcement implements Serializable {

    String announcementId;
    String announcementContent;
    String announcementTitle;

    public Announcement(String id,String title,String content,Timestamp time)
    {
        setAnnouncementContent(content);
        setAnnouncementId(id);
        setAnnouncementTitle(title);
        setPublicationTime(time);


    }

    public String getAnnouncementId() {
        return announcementId;
    }

    public void setAnnouncementId(String announcementId) {
        this.announcementId = announcementId;
    }

    public String getAnnouncementContent() {
        return announcementContent;
    }

    public void setAnnouncementContent(String announcementContent) {
        this.announcementContent = announcementContent;
    }

    public String getAnnouncementTitle() {
        return announcementTitle;
    }

    public void setAnnouncementTitle(String title) {
        this.announcementTitle = title;
    }

    public Timestamp getPublicationTime() {
        return publicationTime;
    }

    public void setPublicationTime(Timestamp publicationTime) {
        this.publicationTime = publicationTime;
    }

    Timestamp publicationTime;
}
