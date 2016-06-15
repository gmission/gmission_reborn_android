package hk.ust.gmission.events;

import retrofit.RetrofitError;

/**
 * Created by bigstone on 10/4/2016.
 */
public class BadReqestEvent {
    private RetrofitError cause;

    public BadReqestEvent(RetrofitError cause) {
        this.cause = cause;
    }

    public RetrofitError getCause() {
        return cause;
    }
}
