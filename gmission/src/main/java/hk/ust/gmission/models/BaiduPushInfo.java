package hk.ust.gmission.models;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by rui on 14-6-8.
 */
public class BaiduPushInfo implements Serializable {
    String id;
    String type;
    String user_id;
    User user;
    String baidu_user_id;
    String baidu_channel_id;
    boolean is_valid;
    Date created_on;

    public BaiduPushInfo() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getBaidu_user_id() {
        return baidu_user_id;
    }

    public void setBaidu_user_id(String baidu_user_id) {
        this.baidu_user_id = baidu_user_id;
    }

    public String getBaidu_channel_id() {
        return baidu_channel_id;
    }

    public void setBaidu_channel_id(String baidu_channel_id) {
        this.baidu_channel_id = baidu_channel_id;
    }

    public boolean is_valid() {
        return is_valid;
    }

    public void setIs_valid(boolean is_valid) {
        this.is_valid = is_valid;
    }

    public Date getCreated_on() {
        return created_on;
    }

    public void setCreated_on(Date created_on) {
        this.created_on = created_on;
    }
}
