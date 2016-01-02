package hk.ust.gmission.ui.fragments;

import android.support.v4.app.Fragment;

import com.squareup.otto.Bus;

import javax.inject.Inject;

/**
 * Created by bigstone on 1/1/2016.
 */
public class BaseAnswerFragment extends Fragment {

    @Inject protected Bus bus;

    @Override
    public void onResume() {
        super.onResume();
        bus.register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        bus.unregister(this);
    }

    public String getAnswer(){
        return null;
    }
}
