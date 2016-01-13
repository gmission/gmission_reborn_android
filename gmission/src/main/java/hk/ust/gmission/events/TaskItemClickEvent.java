package hk.ust.gmission.events;

import android.view.View;

/**
 * Created by bigstone on 13/1/2016.
 */
public class TaskItemClickEvent {

    private View view;

    public TaskItemClickEvent(View view) {
        this.view = view;
    }

    public View getView(){
        return view;
    }
}
