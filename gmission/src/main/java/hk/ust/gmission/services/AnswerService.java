package hk.ust.gmission.services;

import hk.ust.gmission.core.Constants;
import hk.ust.gmission.models.Answer;
import hk.ust.gmission.models.Message;
import hk.ust.gmission.models.ModelWrapper;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by bigstone on 2/1/2016.
 */
public interface AnswerService {

    @POST(Constants.Http.URL_ANSWERS_FRAG)
    Observable<Answer> postAnswer(@Body Answer answer);

    @GET(Constants.Http.URL_ANSWERS_FRAG)
    Observable<ModelWrapper<Answer>> getAnswers();

    @GET(Constants.Http.URL_ANSWERS_FRAG)
    Observable<ModelWrapper<Answer>> getAnswers(@Query("q") String query);

    @PUT(Constants.Http.URL_ANSWERS_FRAG + "/{id}")
    Observable<Answer> updateAnswer(@Path("id") String id, @Body Answer message);
}
