package hk.ust.gmission.services;

import hk.ust.gmission.core.Constants;
import hk.ust.gmission.models.Coordinate;
import hk.ust.gmission.models.GeoLocation;
import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

/**
 * Created by bigstone on 4/1/2016.
 */
public interface GeoService {

    @GET(Constants.Http.URL_LOCATIONS_FRAG + "/{id}")
    Observable<GeoLocation> getGeoLocation(@Path("id") String id);

    @GET(Constants.Http.URL_COORDINATES_FRAG + "/{id}")
    Observable<Coordinate> getCoordinate(@Path("id") String id);
}
