package hk.ust.gmission.events;

import android.view.View;

/**
 * Created by bigstone on 21/12/2015.
 */
public class NewsItemClickEvent {
    private View view;

    public NewsItemClickEvent(View view) {
        this.view = view;
    }

    public View getView(){
        return view;
    }
}

