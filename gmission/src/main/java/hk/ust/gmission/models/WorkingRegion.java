package hk.ust.gmission.models;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by bigstone on 15/4/15.
 */
public class WorkingRegion implements Serializable {
    Integer id;
    @Expose
    double longitude;
    @Expose
    double latitude;
    @Expose
    double min_angle;
    @Expose
    double max_angle;
    @Expose
    double range;
    @Expose
    String comments;
    @Expose
    int worker_id;

    Date created_on;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getMin_angle() {
        return min_angle;
    }

    public void setMin_angle(double min_angle) {
        this.min_angle = min_angle;
    }

    public double getMax_angle() {
        return max_angle;
    }

    public void setMax_angle(double max_angle) {
        this.max_angle = max_angle;
    }

    public double getRange() {
        return range;
    }

    public void setRange(double range) {
        this.range = range;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public int getWorker_id() {
        return worker_id;
    }

    public void setWorker_id(int worker_id) {
        this.worker_id = worker_id;
    }

    public Date getCreated_on() {
        return created_on;
    }

    public void setCreated_on(Date created_on) {
        this.created_on = created_on;
    }
}
