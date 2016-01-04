
package hk.ust.gmission.services;

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

    public AttachmentService getAttachmentService() {
        return getRestAdapter().create(AttachmentService.class);
    }

    public AnswerService getAnswerService() {
        return getRestAdapter().create(AnswerService.class);
    }

    public UserService getUserService() {
        return getRestAdapter().create(UserService.class);
    }



    private RestAdapter getRestAdapter() {
        return restAdapter;
    }


}