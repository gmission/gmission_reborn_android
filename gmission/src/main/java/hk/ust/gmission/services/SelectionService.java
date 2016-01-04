package hk.ust.gmission.services;

import hk.ust.gmission.core.Constants;
import hk.ust.gmission.models.Selection;
import hk.ust.gmission.models.ModelWrapper;
import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by bigstone on 1/1/2016.
 */
public interface SelectionService {
    @GET(Constants.Http.URL_SELECTIONS_FRAG)
    Observable<ModelWrapper<Selection>> getSelections(@Query("q") String query);
}
