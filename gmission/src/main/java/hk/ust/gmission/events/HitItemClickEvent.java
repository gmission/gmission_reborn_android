package hk.ust.gmission.events;

import android.view.View;

/**
 * Created by bigstone on 1/1/2016.
 */
public class HitItemClickEvent {
    private View view;

    public HitItemClickEvent(View view) {
        this.view = view;
    }

    public View getView(){
        return view;
    }
}
