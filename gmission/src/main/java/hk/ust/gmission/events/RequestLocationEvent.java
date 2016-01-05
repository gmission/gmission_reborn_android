package hk.ust.gmission.events;

/**
 * Created by bigstone on 29/12/2015.
 */
public class RequestLocationEvent {
    boolean startLocating;

    public boolean isStartLocating() {
        return startLocating;
    }

    public void setStartLocating(boolean startLocating) {
        this.startLocating = startLocating;
    }

    public RequestLocationEvent(boolean startLocating) {
        this.startLocating = startLocating;
    }
}
