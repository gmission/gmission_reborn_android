package hk.ust.gmission.services;

import hk.ust.gmission.core.Constants;
import hk.ust.gmission.models.wrapper.CampaignWrapper;
import retrofit.http.GET;
import rx.Observable;

/**
 * Created by bigstone on 22/12/2015.
 */
public interface CampaignService {

    @GET(Constants.Http.URL_CAMPAIGNS_FRAG)
    Observable<CampaignWrapper> getCampaigns();
}