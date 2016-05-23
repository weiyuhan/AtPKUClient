package atpku.client.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wyh on 2016/5/19.
 */
public class User implements Serializable
{
    public int id;
    public String nickname;
    public String email;
    public String date_joined;
    public String last_login;
    public String isAdmin;


    public String isBanned;
    public String gender;
    public int commentReceived;
    public int likeReceived;
    public int dislikeReceived;
    public int reportReceived;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDate_joined() {
        return date_joined;
    }

    public void setDate_joined(String date_joined) {
        this.date_joined = date_joined;
    }

    public String getLast_login() {
        return last_login;
    }

    public void setLast_login(String last_login) {
        this.last_login = last_login;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getCommentReceived() {
        return commentReceived;
    }

    public void setCommentReceived(int commentReceived) {
        this.commentReceived = commentReceived;
    }

    public int getLikeReceived() {
        return likeReceived;
    }

    public void setLikeReceived(int likeReceived) {
        this.likeReceived = likeReceived;
    }

    public int getDislikeReceived() {
        return dislikeReceived;
    }

    public void setDislikeReceived(int dislikeReceived) {
        this.dislikeReceived = dislikeReceived;
    }

    public int getReportReceived() {
        return reportReceived;
    }

    public void setReportReceived(int reportReceived) {
        this.reportReceived = reportReceived;
    }


    public String getIsBanned() {
        return isBanned;
    }

    public void setIsBanned(String isBanned) {
        this.isBanned = isBanned;
    }

    public String getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(String isAdmin) {
        this.isAdmin = isAdmin;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", nickname='" + nickname + '\'' +
                ", email='" + email + '\'' +
                ", date_joined='" + date_joined + '\'' +
                ", last_login='" + last_login + '\'' +
                ", isAdmin='" + isAdmin + '\'' +
                ", isBanned='" + isBanned + '\'' +
                ", gender='" + gender + '\'' +
                ", commentReceived=" + commentReceived +
                ", likeReceived=" + likeReceived +
                ", dislikeReceived=" + dislikeReceived +
                ", reportReceived=" + reportReceived +
                '}';
    }
}
