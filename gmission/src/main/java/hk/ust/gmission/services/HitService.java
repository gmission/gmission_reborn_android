package hk.ust.gmission.services;
import hk.ust.gmission.core.Constants;
import hk.ust.gmission.models.Coordinate;
import hk.ust.gmission.models.Hit;
import hk.ust.gmission.models.ModelWrapper;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by bigstone on 23/12/2015.
 */
public interface HitService {
    @GET(Constants.Http.URL_HITS_FRAG)
    Observable<ModelWrapper<Hit>> getHits();

    @GET(Constants.Http.URL_HITS_FRAG)
    Observable<ModelWrapper<Hit>> getHits(@Query("q") String query);

    @GET(Constants.Http.URL_HITS_FRAG + "/{id}")
    Observable<Hit> getHit(@Path("id") String id);

    @POST(Constants.Http.URL_HITS_FRAG)
    Observable<Hit> createHit(@Body Hit hit);
}
