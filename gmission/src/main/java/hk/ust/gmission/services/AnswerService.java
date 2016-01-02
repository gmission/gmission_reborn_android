package hk.ust.gmission.services;

import hk.ust.gmission.core.Constants;
import hk.ust.gmission.models.dao.Answer;
import retrofit.http.Body;
import retrofit.http.POST;
import rx.Observable;

/**
 * Created by bigstone on 2/1/2016.
 */
public interface AnswerService {

    @POST(Constants.Http.URL_ANSWERS_FRAG)
    Observable<Answer> postAnswer(@Body Answer answer);
}
