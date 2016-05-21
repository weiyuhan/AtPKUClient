package atpku.client.model;

import java.util.List;

/**
 * Created by wyh on 2016/5/19.
 */
public class Message
{
    public int ownerID;
    public String title;
    public String content;
    public String postTime;
    public String startTime;
    public String endTime;
    public List<Integer> comments;
    public List<Integer> likeUsers;
    public List<Integer> dislikeUsers;
    public List<Integer> reportUsers;
    public boolean isGlobal;
    public int atPlaceID;

    public int getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(int ownerID) {
        this.ownerID = ownerID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public List<Integer> getComments() {
        return comments;
    }

    public void setComments(List<Integer> comments) {
        this.comments = comments;
    }

    public List<Integer> getLikeUsers() {
        return likeUsers;
    }

    public void setLikeUsers(List<Integer> likeUsers) {
        this.likeUsers = likeUsers;
    }

    public List<Integer> getDislikeUsers() {
        return dislikeUsers;
    }

    public void setDislikeUsers(List<Integer> dislikeUsers) {
        this.dislikeUsers = dislikeUsers;
    }

    public List<Integer> getReportUsers() {
        return reportUsers;
    }

    public void setReportUsers(List<Integer> reportUsers) {
        this.reportUsers = reportUsers;
    }

    public boolean isGlobal() {
        return isGlobal;
    }

    public void setIsGlobal(boolean isGlobal) {
        this.isGlobal = isGlobal;
    }

    public int getAtPlaceID() {
        return atPlaceID;
    }

    public void setAtPlaceID(int atPlaceID) {
        this.atPlaceID = atPlaceID;
    }

    @Override
    public String toString() {
        return "Message{" +
                "ownerID=" + ownerID +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", postTime='" + postTime + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", comments=" + comments +
                ", likeUsers=" + likeUsers +
                ", dislikeUsers=" + dislikeUsers +
                ", reportUsers=" + reportUsers +
                ", isGlobal=" + isGlobal +
                ", atPlaceID=" + atPlaceID +
                '}';
    }
}
