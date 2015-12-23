package hk.ust.gmission.events;

import android.view.View;

/**
 * Created by bigstone on 22/12/2015.
 */
public class CampaignItemClickEvent {
    private View view;

    public CampaignItemClickEvent(View view) {
        this.view = view;
    }

    public View getView(){
        return view;
    }
}
