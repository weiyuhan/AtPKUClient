package atpku.client.model;

import java.io.Serializable;

/**
 * Created by wyh on 2016/5/19.
 */
public class Feedback implements Serializable
{
    public int id;
    public User user;
    public String content;
    public String postTime;
    public boolean isRead;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPostTime() {
        return postTime;
    }

    public void setPostTime(String postTime) {
        this.postTime = postTime;
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
                "id=" + id +
                ", user='" + user + '\'' +
                ", content='" + content + '\'' +
                ", postTime='" + postTime + '\'' +
                ", isRead=" + isRead +
                '}';
    }

    public String toShowString()
    {
        return content;
        /*
        if(isRead)
            return "已读 ： " + content;
        return "未读 ： " + content;*/
    }
}
