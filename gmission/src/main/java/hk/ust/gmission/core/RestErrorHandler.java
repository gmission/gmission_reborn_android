package hk.ust.gmission.core;

import hk.ust.gmission.events.NetworkErrorEvent;
import hk.ust.gmission.events.RestAdapterErrorEvent;
import hk.ust.gmission.events.UnAuthorizedErrorEvent;
import com.squareup.otto.Bus;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;

public class RestErrorHandler implements ErrorHandler {

    public static final int HTTP_NOT_FOUND = 404;
    public static final int HTTP_BAD_REQUEST = 400;
    public static final int HTTP_UNAUTHORIZED = 401;
    public static final int INVALID_LOGIN_PARAMETERS = 101;

    private Bus bus;

    public RestErrorHandler(Bus bus) {
        this.bus = bus;
    }

    @Override
    public Throwable handleError(RetrofitError cause) {
        if(cause != null) {
            if (cause.isNetworkError()) {
                bus.post(new NetworkErrorEvent(cause));
                cause = null;
            } else if(isUnAuthorized(cause)) {
                bus.post(new UnAuthorizedErrorEvent(cause));
                cause = null;
            }
        }


        // You could also put some generic error handling in here so you can start
        // getting analytics on error rates/etc. Perhaps ship your logs off to
        // Splunk, Loggly, etc
        //

        //cause = null;//prevent error propagation

        return cause;
    }

    /**
     * If a user passes an incorrect username/password combo in we could
     * get a unauthorized error back from the API. On parse.com this means
     * we get back a HTTP 404 with an error as JSON in the body as such:
     *
     *  {
     *     code: 101,
     *     error: "invalid login parameters"
     *  }
     *
     *  }
     *
     * Therefore we need to check for the 101 and the 404.
     *
     * @param cause The initial error.
     * @return
     */
    private boolean isUnAuthorized(RetrofitError cause) {
        boolean authFailed = false;

        if(cause.getResponse().getStatus() == HTTP_NOT_FOUND) {
            final ApiError err = (ApiError) cause.getBodyAs(ApiError.class);
            if(err != null && err.getCode() == INVALID_LOGIN_PARAMETERS) {
                authFailed = true;
            }
        }
        if(cause.getResponse().getStatus() == HTTP_UNAUTHORIZED || cause.getResponse().getStatus() == HTTP_BAD_REQUEST ) {

            authFailed = true;
        }




        return authFailed;
    }
}
