package hk.ust.gmission.services;

import hk.ust.gmission.core.Constants;
import hk.ust.gmission.models.dao.Message;
import hk.ust.gmission.models.wrapper.ModelWrapper;
import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by bigstone on 1/1/2016.
 */
public interface MessageService {
    @GET(Constants.Http.URL_MESSAGES_FRAG)
    Observable<ModelWrapper<Message>> getMessages();

    @GET(Constants.Http.URL_MESSAGES_FRAG)
    Observable<ModelWrapper<Message>> getMessages(@Query("q") String query);
}
