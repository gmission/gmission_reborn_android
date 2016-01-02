package hk.ust.gmission.models.dao;

/**
 * Created by bigstone on 1/1/2016.
 */
public class Selection {

    public int id;
    public int hit_id;
    public String brief;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHit_id() {
        return hit_id;
    }

    public void setHit_id(int hit_id) {
        this.hit_id = hit_id;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }
}
