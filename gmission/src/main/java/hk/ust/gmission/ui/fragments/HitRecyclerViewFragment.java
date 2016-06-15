package hk.ust.gmission.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.squareup.otto.Subscribe;

import hk.ust.gmission.R;
import hk.ust.gmission.core.api.QueryObject;
import hk.ust.gmission.events.HitAnswerSuccessEvent;
import hk.ust.gmission.events.HitItemClickEvent;
import hk.ust.gmission.models.Hit;
import hk.ust.gmission.models.ModelWrapper;
import hk.ust.gmission.ui.activities.HitActivity;
import hk.ust.gmission.ui.activities.HitSummaryActivity;
import hk.ust.gmission.ui.adapters.HitRecyclerViewAdapter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static hk.ust.gmission.core.Constants.Extra.HIT_ID;


public class HitRecyclerViewFragment extends BaseRecyclerViewFragment<Hit, HitRecyclerViewAdapter> {


    protected HitRecyclerViewAdapter adapter = new HitRecyclerViewAdapter();

    private boolean isLoaderInitialized = false;
    private String campaignId = null;

    public void setCampaignId(String campaignId) {
        this.campaignId = campaignId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setEmptyText(getString(R.string.no_hit));

        mRecyclerView.setAdapter(adapter);

        configPullToRefresh(getView());



    }

    @Override
    protected void loadData() {
        Observable<ModelWrapper<Hit>> observable;
        if (campaignId != null){
            QueryObject queryObject = new QueryObject();
            queryObject.push("campaign_id", "eq", campaignId);
            queryObject.setOrder_by("created_on", "desc");

            observable = serviceProvider.getService().getHitService().getHits(queryObject.toString());
        } else {
            observable = serviceProvider.getService().getHitService().getHits();
        }


        observable.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnNext(new Action1<ModelWrapper<Hit>>() {
                    @Override
                    public void call(ModelWrapper<Hit> campaigns) {
                        getAdapter().setItems(campaigns.getObjects());
                    }
                })
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable e) {
                        e.printStackTrace();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        if (getAdapter().getItemCount() == 0){
                            emptyView.setVisibility(View.VISIBLE);
                        } else {
                            emptyView.setVisibility(View.GONE);
                        }

                        getAdapter().notifyDataSetChanged();

                    }
                })
                .subscribe();
    }


    @Subscribe
    public void onHitAnswerSuccess(HitAnswerSuccessEvent event){
        if (event.getType() == HitAnswerSuccessEvent.CAMPAIGN_TYPE){
            getAdapter().removeItem(event.getHit_id());
            getAdapter().notifyDataSetChanged();
        }
    }



    @Override
    public void onDestroyView() {

        super.onDestroyView();
    }


    @Subscribe
    public void onListItemClick(HitItemClickEvent event) {
        int position = mRecyclerView.getChildLayoutPosition(event.getView());
        HitRecyclerViewAdapter adapter = (HitRecyclerViewAdapter) mRecyclerView.getAdapter();

        Hit hit = adapter.getItem(position);

        if (hit.getType().equals("3d")) {
            startActivity(new Intent(getActivity(), HitSummaryActivity.class).putExtra(HIT_ID, hit.getId()));
        } else {
            startActivity(new Intent(getActivity(), HitActivity.class).putExtra(HIT_ID, hit.getId()));
        }

    }


}