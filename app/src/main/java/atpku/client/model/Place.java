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
    public String type;
    public List<Message> globalMessages;

    public List<Message> getGlobalMessages() {
        return globalMessages;
    }

    public void setGlobalMessages(List<Message> globalMessages) {
        this.globalMessages = globalMessages;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
                ", type='" + type + '\'' +
                ", globalMessages=" + globalMessages +
                '}';
    }

    // 暂时只显示第0条
    public String snippetString() {
        if(globalMessages == null || globalMessages.size() == 0)
            return null;
        return globalMessages.get(0).snippetString();
    }
}
