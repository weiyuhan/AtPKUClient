package atpku.client.model;

import java.io.Serializable;

/**
 * Created by wyh on 2016/5/21.
 */
public class Comment implements Serializable
{
    public int id;
    public User owner;
    public String content;
    public String commentTime;
    public int message;

    public int getId() {
        return id;
    }

    public void setId(int commentID) {
        this.id = commentID;
    }

    public int getMessage() {
        return message;
    }

    public void setMessage(int atMsgID) {
        this.message = atMsgID;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(String commentTime) {
        this.commentTime = commentTime.replace("T", " ");
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", owner=" + owner.getId() +
                ", message=" + message +
                ", content='" + content + '\'' +
                ", commentTime='" + commentTime + '\'' +
                '}';
    }
}
