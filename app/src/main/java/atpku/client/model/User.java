package atpku.client.model;

/**
 * Created by wyh on 2016/5/19.
 */
public class User
{
    public String username;
    public String email;
    public String password;
    public String joinTime;
    public boolean isBanned;
    public int gender;
    public int commentReceived;
    public int likeReceived;
    public int dislikeReceived;
    public int reportReceived;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(String joinTime) {
        this.joinTime = joinTime;
    }

    public boolean isBanned() {
        return isBanned;
    }

    public void setIsBanned(boolean isBanned) {
        this.isBanned = isBanned;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
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

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", joinTime='" + joinTime + '\'' +
                ", isBanned=" + isBanned +
                ", gender=" + gender +
                ", commentReceived=" + commentReceived +
                ", likeReceived=" + likeReceived +
                ", dislikeReceived=" + dislikeReceived +
                ", reportReceived=" + reportReceived +
                '}';
    }
}
