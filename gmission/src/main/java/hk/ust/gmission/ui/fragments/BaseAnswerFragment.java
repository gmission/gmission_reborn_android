package hk.ust.gmission.ui.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.squareup.otto.Bus;

import java.io.File;

import javax.inject.Inject;

import butterknife.ButterKnife;
import hk.ust.gmission.Injector;
import hk.ust.gmission.R;

/**
 * Created by bigstone on 1/1/2016.
 */
public class BaseAnswerFragment extends Fragment {
    public static final int RESULT_CANCELED = 0;
    public static final int RESULT_OK = -1;
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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Injector.inject(this);
        ButterKnife.bind(this, view);
    }

    public String getAnswer(){
        return null;
    }

    public File getImageFile(){return null;}


}
