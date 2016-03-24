package hk.ust.gmission.services;

import hk.ust.gmission.core.Constants;
import hk.ust.gmission.models.Campaign;
import hk.ust.gmission.models.CampaignUser;
import hk.ust.gmission.models.ModelWrapper;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
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

    @GET(Constants.Http.URL_CAMPAIGNS_FRAG + "/{id}")
    Observable<Campaign> getCampaign(@Path("id") String id);


    @POST(Constants.Http.URL_CAMPAIGN_USER_FRAG)
    Observable<CampaignUser> createCampaignUser(@Body CampaignUser campaignUser);

    @DELETE(Constants.Http.URL_CAMPAIGN_USER_FRAG + "/{id}")
    Observable<CampaignUser> deleteCampaignUser(@Path("id") String id);

    @GET(Constants.Http.URL_CAMPAIGN_USER_FRAG)
    Observable<ModelWrapper<CampaignUser>> getCampaignUsers(@Query("q") String query);

}