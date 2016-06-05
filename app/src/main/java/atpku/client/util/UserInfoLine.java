package atpku.client.util;

import java.io.Serializable;

/**
 * Created by wyh on 2016/6/6.
 */
public class UserInfoLine implements Serializable
{
    public String key;
    public String value;
    public String imgUrl;
    public String enterUrl;

    public UserInfoLine(String key, String value, String imgUrl, String enterUrl) {
        this.key = key;
        this.value = value;
        this.imgUrl = imgUrl;
        this.enterUrl = enterUrl;
    }

    public UserInfoLine() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getEnterUrl() {
        return enterUrl;
    }

    public void setEnterUrl(String enterUrl) {
        this.enterUrl = enterUrl;
    }

    @Override
    public String toString() {
        return "UserInfoLine{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", enterUrl='" + enterUrl + '\'' +
                '}';
    }
}
