package atpku.client.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wyh on 2016/5/19.
 */
public class Message implements Serializable
{
    public int id;
    public User owner;
    public List<Comment> comments;
    public Place atPlace;
    public List<User> likeUsers;
    public List<User> dislikeUsers;
    public List<User> reportUsers;
    public int reportTimes;
    public String title;
    public String content;
    public String postTime;
    public String startTime;
    public String endTime;
    public int heat;
    public int stat;
    public List<Image> images;

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public Place getAtPlace() {
        return atPlace;
    }

    public void setAtPlace(Place atPlace) {
        this.atPlace = atPlace;
    }

    public List<User> getLikeUsers() {
        return likeUsers;
    }

    public void setLikeUsers(List<User> likeUsers) {
        this.likeUsers = likeUsers;
    }

    public List<Integer> getLikeUserIds()
    {
        List<Integer> result = new ArrayList<Integer>();
        for (User user : likeUsers)
        {
            result.add(user.getId());
        }
        return result;
    }

    public List<User> getDislikeUsers() {
        return dislikeUsers;
    }

    public void setDislikeUsers(List<User> dislikeUsers) {
        this.dislikeUsers = dislikeUsers;
    }

    public List<Integer> getDislikeUserIds()
    {
        List<Integer> result = new ArrayList<Integer>();
        for (User user : dislikeUsers)
        {
            result.add(user.getId());
        }
        return result;
    }

    public List<User> getReportUsers() {
        return reportUsers;
    }
    public int getReportTimes() { return reportTimes; }
    public void setReportTimes(int reportTimes) { this.reportTimes = reportTimes; }

    public void setReportUsers(List<User> reportUsers) {
        this.reportUsers = reportUsers;
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
        this.postTime = postTime.replace("T", " ");
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime.replace("T", " ");
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime.replace("T", " ");
    }

    public int getHeat() {
        return heat;
    }

    public void setHeat(int heat) {
        this.heat = heat;
    }

    public int getStat() {
        return stat;
    }

    public void setStat(int stat) {
        this.stat = stat;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", owner=" + owner +
                ", comments=" + comments +
                ", atPlace=" + atPlace +
                ", likeUsers=" + likeUsers +
                ", dislikeUsers=" + dislikeUsers +
                ", reportUsers=" + reportUsers +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", postTime='" + postTime + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", heat=" + heat +
                ", stat=" + stat +
                '}';
    }

    // Marker的snippet的显示内容
    public String snippetString() {
        return title;
    }

}
