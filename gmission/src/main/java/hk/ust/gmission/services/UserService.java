package hk.ust.gmission.services;

import hk.ust.gmission.core.Constants;
import hk.ust.gmission.models.User;
import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

/**
 * Created by bigstone on 3/1/2016.
 */
public interface UserService {
    @GET(Constants.Http.URL_USERS_FRAG + "/{id}")
    Observable<User> getUser(@Path("id") String id);
}
