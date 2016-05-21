package atpku.client.model;

/**
 * Created by wyh on 2016/5/19.
 */
public class Feedback
{
    public int userID;
    public String content;
    public String time;
    public boolean isRead;

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }

    @Override
    public String toString() {
        return "Feedback{" +
                "userID=" + userID +
                ", content='" + content + '\'' +
                ", time='" + time + '\'' +
                ", isRead=" + isRead +
                '}';
    }
}
