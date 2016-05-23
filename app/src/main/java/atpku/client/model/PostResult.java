package atpku.client.model;

import java.io.Serializable;

/**
 * Created by wyh on 2016/5/22.
 */
public class PostResult implements Serializable
{
    public boolean success;
    public String message;
    public String data;

    @Override
    public String toString() {
        return "PostResult{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", data='" + data + '\'' +
                '}';
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
