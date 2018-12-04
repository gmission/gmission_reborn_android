package hk.ust.gmission.ui.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.ButterKnife;
import hk.ust.gmission.BootstrapServiceProvider;
import hk.ust.gmission.Injector;
import hk.ust.gmission.R;

/**
 * Base class for all Bootstrap Activities that need fragments.
 */
public class BootstrapFragmentActivity extends AppCompatActivity {

    @Inject protected Bus bus;
    @Inject protected BootstrapServiceProvider serviceProvider;

    private ProgressDialog dialog;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Injector.inject(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void setContentView(final int layoutResId) {
        super.setContentView(layoutResId);

        ButterKnife.bind(this);

        dialog = new ProgressDialog(BootstrapFragmentActivity.this);
        dialog.setMessage(getText(R.string.message_uploading));
        dialog.setIndeterminate(true);
        dialog.setCancelable(true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(final DialogInterface dialog) {
            }
        });
    }



    @Override
    protected void onResume() {
        super.onResume();
        bus.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bus.unregister(this);
    }

    /**
     * Hide progress dialog
     */
    public void hideProgress() {
        dialog.dismiss();
    }

    /**
     * Show progress dialog
     */
    public void showProgress() {
        dialog.show();
    }
}
