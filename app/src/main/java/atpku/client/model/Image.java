package atpku.client.model;

import java.io.Serializable;

/**
 * Created by wyh on 2016/5/29.
 */
public class Image implements Serializable
{
    public int id;
    public String url;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Image{" +
                "id=" + id +
                ", url='" + url + '\'' +
                '}';
    }
}
