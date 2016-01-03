package hk.ust.gmission.events;

/**
 * Created by bigstone on 2/1/2016.
 */
public class HitAnswerSuccessEvent {
    private String hit_id;

    public HitAnswerSuccessEvent(String hit_id) {
        this.hit_id = hit_id;
    }

    public String getHit_id() {
        return hit_id;
    }

    public void setHit_id(String hit_id) {
        this.hit_id = hit_id;
    }
}
