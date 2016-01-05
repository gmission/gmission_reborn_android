package hk.ust.gmission.services;

import hk.ust.gmission.core.Constants;
import hk.ust.gmission.models.Message;
import hk.ust.gmission.models.ModelWrapper;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.PUT;
import retrofit.http.Path;
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

    @PUT(Constants.Http.URL_MESSAGES_FRAG + "/{messageId}")
    Observable<Message> updateMessage(@Path("messageId") String messageId, @Body Message message);
}
