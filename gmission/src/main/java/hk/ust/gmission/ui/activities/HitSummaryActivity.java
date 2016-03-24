package hk.ust.gmission.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import hk.ust.gmission.R;
import hk.ust.gmission.models.Hit;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

import static hk.ust.gmission.core.Constants.Extra.CAMPAIGN_ID;
import static hk.ust.gmission.core.Constants.Extra.HIT_ID;

public class HitSummaryActivity extends BootstrapFragmentActivity {


    @Bind(R.id.view_compain_btn) Button viewCampaignButton;
    @Bind(R.id.view_answers_btn) Button viewAnswersButton;
    @Bind(R.id.view_model_btn) Button viewModelButton;
    @Bind(R.id.hit_content) TextView hitContent;

    private Hit mHit = null;
    private HitSummaryActivity mActivity;

    private static int BUTTON_PRESS_DELAY_MILLIS = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hit_summary_activity);
        mActivity = this;

        downloadHit();

        subscribeButtons();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.hit_summary_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
//            case R.id.refresh:
//                startActivity(new Intent(this, AnswerListActivity.class).putExtra(HIT_ID, mHit.getId()));
//                break;
            // action with ID action_settings was selected
            default:
                finish();
                break;
        }
        return true;
    }

    public void downloadHit(){
        if (getIntent() != null && getIntent().getExtras() != null) {
            String hitId = getIntent().getExtras().getString(HIT_ID);
            serviceProvider.getService(mActivity).getHitService().getHit(hitId)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<Hit>() {
                    @Override
                    public void call(Hit hit) {
                        mHit = hit;
                        hitContent.setText(hit.getTitle() + "\n" + hit.getDescription());
                    }
                })
                .subscribe();
        }
    }


    private void subscribeButtons() {
        RxView.clicks(viewCampaignButton)
                .debounce(BUTTON_PRESS_DELAY_MILLIS, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        startActivity(new Intent(mActivity, CampaignSummaryActivity.class).putExtra(CAMPAIGN_ID, String.valueOf(mHit.getCampaign_id())));
                    }
                })
                .subscribe();

        RxView.clicks(viewAnswersButton)
                .debounce(BUTTON_PRESS_DELAY_MILLIS, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        startActivity(new Intent(mActivity, AnswerListActivity.class).putExtra(HIT_ID, mHit.getId()));
                    }
                })
                .subscribe();

        RxView.clicks(viewModelButton)
                .debounce(BUTTON_PRESS_DELAY_MILLIS, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        startActivity(new Intent(mActivity, MeshViewActivity.class).putExtra(HIT_ID, mHit.getId()));
                    }
                })
                .subscribe();
    }
}
