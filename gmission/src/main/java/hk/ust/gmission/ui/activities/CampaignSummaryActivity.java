package hk.ust.gmission.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import dagger.Provides;
import hk.ust.gmission.R;
import hk.ust.gmission.core.Constants;
import hk.ust.gmission.core.api.QueryObject;
import hk.ust.gmission.models.Campaign;
import hk.ust.gmission.models.CampaignUser;
import hk.ust.gmission.models.Hit;
import hk.ust.gmission.models.ModelWrapper;
import hk.ust.gmission.services.CampaignService;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static hk.ust.gmission.core.Constants.Extra.CAMPAIGN_ID;
import static hk.ust.gmission.core.Constants.Extra.HIT_ID;

public class CampaignSummaryActivity extends BootstrapFragmentActivity {


    @Bind(R.id.campaign_title_text) TextView campaignTitleText;
    @Bind(R.id.campaign_content_text) TextView campaignContentText;
    @Bind(R.id.view_tasks_btn) Button viewTasksButton;
    @Bind(R.id.join_btn) Button joinButton;

    private Campaign mCampaign = null;
    private CampaignSummaryActivity mActivity;
    private static int BUTTON_PRESS_DELAY_MILLIS = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.campaign_summary_activity);

        mActivity = this;

        downloadCampaign();

        subscribeButtons();
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

    public void downloadCampaign(){
        if (getIntent() != null && getIntent().getExtras() != null) {
            String campaignId = getIntent().getExtras().getString(CAMPAIGN_ID);
            CampaignService service = serviceProvider.getService().getCampaignService();
            service.getCampaign(campaignId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(new Action1<Campaign>() {
                        @Override
                        public void call(Campaign campaign) {
                            mCampaign = campaign;
                            campaignTitleText.setText(mCampaign.getTitle());
                            campaignContentText.setText(mCampaign.getBrief());
                        }
                    })
                    .subscribe();


            QueryObject queryObject = new QueryObject();
            queryObject.push("user_id", "eq", Constants.Http.PARAM_USER_ID);
            queryObject.push("campaign_id", "eq", campaignId);

            service.getCampaignUsers(queryObject.toString())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(new Action1<ModelWrapper<CampaignUser>>() {
                        @Override
                        public void call(ModelWrapper<CampaignUser> campaignUserModelWrapper) {
                            joinButton.setVisibility(View.VISIBLE);
                            if (campaignUserModelWrapper.getNum_results() > 0) {
                                joinButton.setText(getString(R.string.label_quit));
                            } else {
                                joinButton.setText(getString(R.string.label_join));
                            }
                        }
                    })
                    .subscribe();
        }
    }

    private void subscribeButtons() {
        RxView.clicks(viewTasksButton)
                .debounce(BUTTON_PRESS_DELAY_MILLIS, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        startActivity(new Intent(mActivity, HitListActivity.class).putExtra(CAMPAIGN_ID, String.valueOf(mCampaign.getId())));
                    }
                })
                .subscribe();

        RxView.clicks(joinButton)
                .debounce(BUTTON_PRESS_DELAY_MILLIS, TimeUnit.MILLISECONDS)
                .observeOn(Schedulers.io())
                .doOnNext(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        final CampaignService service = serviceProvider.getService().getCampaignService();
                        if (joinButton.getText().equals(getString(R.string.label_join))) {
                            CampaignUser campaignUser = new CampaignUser();
                            campaignUser.setCampaign_id(mCampaign.getId());
                            campaignUser.setUser_id(Constants.Http.PARAM_USER_ID);
                            campaignUser.setRole_id("2");
                            service.createCampaignUser(campaignUser)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .doOnNext(new Action1<CampaignUser>() {
                                        @Override
                                        public void call(CampaignUser dummy) {
                                            Toast.makeText(mActivity.getApplicationContext(), getString(R.string.message_join_campaign_success), Toast.LENGTH_SHORT).show();
                                            joinButton.setText(getString(R.string.label_quit));
                                        }
                                    })
                                    .doOnError(new Action1<Throwable>() {
                                        @Override
                                        public void call(Throwable throwable) {
                                            Toast.makeText(mActivity.getApplicationContext(), getString(R.string.message_fail), Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .subscribe();
                        } else {
                            QueryObject queryObject = new QueryObject();
                            queryObject.push("user_id", "eq", Constants.Http.PARAM_USER_ID);
                            queryObject.push("campaign_id", "eq", mCampaign.getId());
                            service.getCampaignUsers(queryObject.toString())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .doOnNext(new Action1<ModelWrapper<CampaignUser>>() {
                                        @Override
                                        public void call(ModelWrapper<CampaignUser> campaignUserModelWrapper) {
                                            for (CampaignUser campaignUser : campaignUserModelWrapper.getObjects()){
                                                service.deleteCampaignUser(campaignUser.getId()).subscribe();
                                            }
                                        }
                                    })
                                    .doOnCompleted(new Action0() {
                                        @Override
                                        public void call() {
                                            Toast.makeText(mActivity.getApplicationContext(), getString(R.string.message_quit_campaign_success), Toast.LENGTH_SHORT).show();
                                            joinButton.setText(getString(R.string.label_join));
                                        }
                                    })
                                    .subscribe();
                        }
                    }
                })
                .subscribe();
    }
}
