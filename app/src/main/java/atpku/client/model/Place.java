package atpku.client.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wyh on 2016/5/19.
 */
public class Place implements Serializable
{
    public int id;
    public double lng;
    public double lat;
    public String name;
    public Message globalMessage;

    public Message getGlobalMessage() {
        return globalMessage;
    }

    public void setGlobalMessage(Message globalMessage) {
        this.globalMessage = globalMessage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Place{" +
                "id=" + id +
                ", lng=" + lng +
                ", lat=" + lat +
                ", name='" + name + '\'' +
                ", globalMessage=" + globalMessage +
                '}';
    }

    // 暂时只显示第0条
    public String snippetString() {
        if(globalMessage == null)
            return null;
        return globalMessage.snippetString();
    }
}
