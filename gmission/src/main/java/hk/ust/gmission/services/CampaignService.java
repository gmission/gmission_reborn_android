package hk.ust.gmission.services;

import java.util.List;

import hk.ust.gmission.RESTClient;
import hk.ust.gmission.models.dao.Campaign;
import hk.ust.gmission.models.wrapper.CampaignWrapper;
import retrofit.http.GET;
import rx.Observable;

/**
 * Created by bigstone on 22/12/2015.
 */
public interface CampaignService {

    @GET(RESTClient.URL_CAMPAIGNS_FRAG)
    Observable<CampaignWrapper> getCampaigns();
}