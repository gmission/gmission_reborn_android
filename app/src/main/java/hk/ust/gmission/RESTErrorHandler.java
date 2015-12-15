package hk.ust.gmission;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;

/**
 * Created by rui on 14-5-26.
 */
public class RESTErrorHandler implements ErrorHandler {

    @Override
    public Throwable handleError(RetrofitError cause) {
        if( BootstrapApplication.getInstance() != null && BootstrapApplication.getInstance().getApplicationContext() != null) {
//            Toast.makeText(BootstrapApplication.getInstance().getApplicationContext(), "网络连接不正常...请检查...", Toast.LENGTH_LONG).show();
            return cause;
        }
        return cause;
    }

}
