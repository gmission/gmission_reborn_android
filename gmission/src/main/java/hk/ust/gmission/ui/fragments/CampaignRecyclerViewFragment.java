package hk.ust.gmission.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.squareup.otto.Subscribe;

import hk.ust.gmission.R;
import hk.ust.gmission.core.api.QueryObject;
import hk.ust.gmission.events.CampaignItemClickEvent;
import hk.ust.gmission.models.Campaign;
import hk.ust.gmission.models.ModelWrapper;
import hk.ust.gmission.ui.activities.HitListActivity;
import hk.ust.gmission.ui.adapters.CampaignRecyclerViewAdapter;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static hk.ust.gmission.core.Constants.Extra.CAMPAIGN_ID;

public class CampaignRecyclerViewFragment extends BaseRecyclerViewFragment<Campaign, CampaignRecyclerViewAdapter> {

    protected CampaignRecyclerViewAdapter adapter = new CampaignRecyclerViewAdapter();

    private boolean isLoaderInitialized = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setEmptyText(getString(R.string.no_campaign));

        mRecyclerView.setAdapter(adapter);


        configPullToRefresh(getView());

    }

    @Override
    protected void loadData(){
        QueryObject queryObject = new QueryObject();
        queryObject.push("status", "eq", "open");
        serviceProvider.getService(getActivity()).getCampaignService().getCampaigns(queryObject.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnNext(new Action1<ModelWrapper<Campaign>>() {
                    @Override
                    public void call(ModelWrapper<Campaign> campaigns) {
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





    @Override
    public void onDestroyView() {

        super.onDestroyView();
    }


    @Subscribe
    public void onListItemClick(CampaignItemClickEvent event) {
        int position = mRecyclerView.getChildLayoutPosition(event.getView());
        CampaignRecyclerViewAdapter adapter = (CampaignRecyclerViewAdapter) mRecyclerView.getAdapter();

        Campaign campaign = adapter.getItem(position);

        startActivity(new Intent(getActivity(), HitListActivity.class).putExtra(CAMPAIGN_ID, String.valueOf(campaign.getId())));
    }


}
