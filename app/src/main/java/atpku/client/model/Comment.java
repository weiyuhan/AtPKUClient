package atpku.client.model;

import java.io.Serializable;

/**
 * Created by wyh on 2016/5/21.
 */
public class Comment implements Serializable
{
    public int commentID;
    public int atMsgID;
    public int ownerID;
    public String content;
    public String commentTime;

    public int getCommentID() {
        return commentID;
    }

    public void setCommentID(int commentID) {
        this.commentID = commentID;
    }

    public int getAtMsgID() {
        return atMsgID;
    }

    public void setAtMsgID(int atMsgID) {
        this.atMsgID = atMsgID;
    }

    public int getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(int ownerID) {
        this.ownerID = ownerID;
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
        this.commentTime = commentTime;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "commentID=" + commentID +
                ", atMsgID=" + atMsgID +
                ", ownerID=" + ownerID +
                ", content='" + content + '\'' +
                ", commentTime='" + commentTime + '\'' +
                '}';
    }
}
