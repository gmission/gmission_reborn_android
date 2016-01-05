package hk.ust.gmission.events;

/**
 * Created by bigstone on 2/1/2016.
 */
public class HitAnswerSuccessEvent {
    public static final int MESSAGE_TYPE = 100;
    public static final int CAMPAIGN_TYPE = 100;

    private String hit_id;
    private String message_id;

    private int type;

    public HitAnswerSuccessEvent(String hit_id, String message_id, int type) {
        this.hit_id = hit_id;
        this.message_id = message_id;
        this.type = type;
    }

    public String getHit_id() {
        return hit_id;
    }

    public void setHit_id(String hit_id) {
        this.hit_id = hit_id;
    }

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
