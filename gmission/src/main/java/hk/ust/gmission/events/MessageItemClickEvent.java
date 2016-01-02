package hk.ust.gmission.events;

import android.view.View;

/**
 * Created by bigstone on 1/1/2016.
 */
public class MessageItemClickEvent {
    private View view;

    public MessageItemClickEvent(View view) {
        this.view = view;
    }

    public View getView(){
        return view;
    }
}
