package hk.ust.gmission.models.dao;

import java.util.Date;

/**
 * Created by bigstone on 23/12/2015.
 */
public class Hit {
    int id;
    String type;
    String title;
    String description;
    int attachment_id;
    int campaign_id;
    int credit;
    String status;
    int required_answer_count;
    int min_selection_count;
    int max_selection_count;
    Date begin_time;
    Date end_time;
    Date created_on;
    int location_id;
    int requester_id;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getAttachment_id() {
        return attachment_id;
    }

    public void setAttachment_id(int attachment_id) {
        this.attachment_id = attachment_id;
    }

    public int getCampaign_id() {
        return campaign_id;
    }

    public void setCampaign_id(int campaign_id) {
        this.campaign_id = campaign_id;
    }

    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getRequired_answer_count() {
        return required_answer_count;
    }

    public void setRequired_answer_count(int required_answer_count) {
        this.required_answer_count = required_answer_count;
    }

    public int getMin_selection_count() {
        return min_selection_count;
    }

    public void setMin_selection_count(int min_selection_count) {
        this.min_selection_count = min_selection_count;
    }

    public int getMax_selection_count() {
        return max_selection_count;
    }

    public void setMax_selection_count(int max_selection_count) {
        this.max_selection_count = max_selection_count;
    }

    public Date getBegin_time() {
        return begin_time;
    }

    public void setBegin_time(Date begin_time) {
        this.begin_time = begin_time;
    }

    public Date getEnd_time() {
        return end_time;
    }

    public void setEnd_time(Date end_time) {
        this.end_time = end_time;
    }

    public Date getCreated_on() {
        return created_on;
    }

    public void setCreated_on(Date created_on) {
        this.created_on = created_on;
    }

    public int getLocation_id() {
        return location_id;
    }

    public void setLocation_id(int location_id) {
        this.location_id = location_id;
    }

    public int getRequester_id() {
        return requester_id;
    }

    public void setRequester_id(int requester_id) {
        this.requester_id = requester_id;
    }
}
