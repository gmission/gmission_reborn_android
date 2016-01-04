package hk.ust.gmission.models;

/**
 * Created by bigstone on 1/1/2016.
 */
public class Selection extends BaseModel{
    String id;
    String hit_id;
    String brief;

    public String getId(){
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getHit_id() {
        return hit_id;
    }

    public void setHit_id(String hit_id) {
        this.hit_id = hit_id;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }
}
