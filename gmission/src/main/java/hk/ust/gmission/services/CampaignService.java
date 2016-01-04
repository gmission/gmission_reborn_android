package hk.ust.gmission.services;

import hk.ust.gmission.core.Constants;
import hk.ust.gmission.models.Campaign;
import hk.ust.gmission.models.ModelWrapper;
import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by bigstone on 22/12/2015.
 */
public interface CampaignService {

    @GET(Constants.Http.URL_CAMPAIGNS_FRAG)
    Observable<ModelWrapper<Campaign>> getCampaigns();

    @GET(Constants.Http.URL_CAMPAIGNS_FRAG)
    Observable<ModelWrapper<Campaign>> getCampaigns(@Query("q") String query);
}