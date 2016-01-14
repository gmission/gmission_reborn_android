package hk.ust.gmission.models;

import java.util.Date;

/**
 * Created by bigstone on 1/1/2016.
 */
public class Message extends BaseModel{
    public final static String MESSAGE_TYPE_FEED = "task assignment";
    public final static String MESSAGE_TYPE_ANSWER = "new answer noti";
    public final static String MESSAGE_TYPE_COMMENT = "new comment noti";

    String id;
    String type;
    String content;
    String att_type;
    String attachment;
    String sender_id;
    String receiver_id;
    String status;
    Date created_on;


    public String getId(){
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public String getReceiver_id() {
        return receiver_id;
    }

    public void setReceiver_id(String receiver_id) {
        this.receiver_id = receiver_id;
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
