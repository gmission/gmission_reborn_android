package hk.ust.gmission.models;

import java.util.Date;

/**
 * Created by bigstone on 2/1/2016.
 */
public class Answer extends BaseModel{
    String id;
    String hit_id;
    String brief;
    String attachment_id;
    String type;
    Date created_on;
    String location_id;
    String worker_id;

    public String getId(){
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getCreated_on() {
        return created_on;
    }

    public void setCreated_on(Date created_on) {
        this.created_on = created_on;
    }

    public String getHit_id() {
        return hit_id;
    }

    public void setHit_id(String hit_id) {
        this.hit_id = hit_id;
    }

    public String getAttachment_id() {
        return attachment_id;
    }

    public void setAttachment_id(String attachment_id) {
        this.attachment_id = attachment_id;
    }

    public String getLocation_id() {
        return location_id;
    }

    public void setLocation_id(String location_id) {
        this.location_id = location_id;
    }

    public String getWorker_id() {
        return worker_id;
    }

    public void setWorker_id(String worker_id) {
        this.worker_id = worker_id;
    }
}
