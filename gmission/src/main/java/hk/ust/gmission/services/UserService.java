package hk.ust.gmission.services;

import hk.ust.gmission.RESTClient;
import hk.ust.gmission.core.Constants;
import hk.ust.gmission.models.dao.User;
import hk.ust.gmission.models.wrapper.UsersWrapper;
import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

/**
 * User service for connecting the the REST API and
 * getting the users.
 */
public interface UserService {

    @GET(RESTClient.URL_AUTH)
    Observable<UsersWrapper> getUsers();

    /**
     * The {@link retrofit.http.Query} values will be transform into query string paramters
     * via Retrofit
     *
     * @param username The users email
     * @param password The users password
     * @return A login response.
     */
    @GET(RESTClient.URL_AUTH)
    User authenticate(@Query(Constants.Http.PARAM_USERNAME) String username,
                      @Query(Constants.Http.PARAM_PASSWORD) String password);
}
