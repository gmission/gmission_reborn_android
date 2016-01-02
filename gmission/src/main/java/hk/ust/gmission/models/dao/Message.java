package hk.ust.gmission.models.dao;

import java.util.Date;

/**
 * Created by bigstone on 1/1/2016.
 */
public class Message {
    int id;
    String type;
    String content;
    String att_type;
    int attachment;
    int sender_id;
    int receiver_id;
    String status;
    Date created_on;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAtt_type() {
        return att_type;
    }

    public void setAtt_type(String att_type) {
        this.att_type = att_type;
    }

    public int getAttachment() {
        return attachment;
    }

    public void setAttachment(int attachment) {
        this.attachment = attachment;
    }

    public int getSender_id() {
        return sender_id;
    }

    public void setSender_id(int sender_id) {
        this.sender_id = sender_id;
    }

    public int getReceiver_id() {
        return receiver_id;
    }

    public void setReceiver_id(int receiver_id) {
        this.receiver_id = receiver_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreated_on() {
        return created_on;
    }

    public void setCreated_on(Date created_on) {
        this.created_on = created_on;
    }
}
