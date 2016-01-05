package hk.ust.gmission.events;

import retrofit.RetrofitError;

public class NetworkErrorEvent {
    private RetrofitError cause;

    public NetworkErrorEvent(RetrofitError cause) {
        this.cause = cause;
    }

    public RetrofitError getCause() {
        return cause;
    }
}
