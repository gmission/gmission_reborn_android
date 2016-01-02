
package hk.ust.gmission.services;

import java.util.List;

import hk.ust.gmission.models.dao.CheckIn;
import hk.ust.gmission.models.dao.Message;
import hk.ust.gmission.models.dao.User;
import retrofit.RestAdapter;

/**
 * Bootstrap API service
 */
public class BootstrapService {

    private RestAdapter restAdapter;

    /**
     * Create bootstrap service
     * Default CTOR
     */
    public BootstrapService() {
    }

    /**
     * Create bootstrap service
     *
     * @param restAdapter The RestAdapter that allows HTTP Communication.
     */
    public BootstrapService(RestAdapter restAdapter) {
        this.restAdapter = restAdapter;
    }




    public CampaignService getCampaignService() {
        return getRestAdapter().create(CampaignService.class);
    }

    public MessageService getMessageService() {
        return getRestAdapter().create(MessageService.class);
    }

    public HitService getHitService() {
        return getRestAdapter().create(HitService.class);
    }

    public SelectionService getSelectionService() {
        return getRestAdapter().create(SelectionService.class);
    }

    public AnswerService getAnswerService() {
        return getRestAdapter().create(AnswerService.class);
    }

    public CheckInService getCheckInService() {
        return getRestAdapter().create(CheckInService.class);
    }

    private RestAdapter getRestAdapter() {
        return restAdapter;
    }


    /**
     * Get all bootstrap Checkins that exists on Parse.com
     */
    public List<CheckIn> getCheckIns() {
        return getCheckInService().getCheckIns().getResults();
    }

}