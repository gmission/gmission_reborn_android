package hk.ust.gmission.services;

import hk.ust.gmission.core.Constants;
import hk.ust.gmission.models.User;
import hk.ust.gmission.models.UsersWrapper;
import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

/**
 * User service for connecting the the REST API and
 * getting the users.
 */
public interface UserService {

    @GET(Constants.Http.URL_USERS_FRAG)
    Observable<UsersWrapper> getUsers();

    /**
     * The {@link retrofit.http.Query} values will be transform into query string paramters
     * via Retrofit
     *
     * @param email The users email
     * @param password The users password
     * @return A login response.
     */
    @GET(Constants.Http.URL_AUTH_FRAG)
    User authenticate(@Query(Constants.Http.PARAM_USERNAME) String email,
                      @Query(Constants.Http.PARAM_PASSWORD) String password);
}
