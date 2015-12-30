package hk.ust.gmission.ui.fragments;

import android.accounts.AccountsException;
import android.os.Bundle;

import com.squareup.otto.Subscribe;

import java.io.IOException;

import javax.inject.Inject;

import hk.ust.gmission.BootstrapServiceProvider;
import hk.ust.gmission.Injector;
import hk.ust.gmission.R;
import hk.ust.gmission.core.api.QueryObject;
import hk.ust.gmission.events.CampaignItemClickEvent;
import hk.ust.gmission.models.dao.Hit;
import hk.ust.gmission.models.wrapper.HitWrapper;
import hk.ust.gmission.ui.adapters.HitRecyclerViewAdapter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class HitRecyclerViewFragment extends BaseRecyclerViewFragment<Hit, HitRecyclerViewAdapter> {

    @Inject protected BootstrapServiceProvider serviceProvider;
    protected HitRecyclerViewAdapter hitRecyclerViewAdapter = new HitRecyclerViewAdapter();

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

        setEmptyText(getString(R.string.no_campaign));

        mRecyclerView.setAdapter(hitRecyclerViewAdapter);

        configPullToRefresh(getView());



    }

    @Override
    protected void loadData() throws IOException, AccountsException {
        Observable<HitWrapper> observable;
        if (campaignId != null){
            QueryObject queryObject = new QueryObject();
            queryObject.push("campaign_id", "eq", campaignId);
            observable = serviceProvider.getService(getActivity()).getHitService().getHits(queryObject.toString());
        } else {
            observable = serviceProvider.getService(getActivity()).getHitService().getHits();
        }


        observable.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnNext(new Action1<HitWrapper>() {
                    @Override
                    public void call(HitWrapper campaigns) {
                        if (!isLoaderInitialized){
                            getAdapter().setItems(campaigns.getObjects());
                        } else {
                            Hit hit = new Hit();
                            hit.setTitle("Test News");
                            hit.setDescription("This is a test news");
                            hit.setId(1);
                            getAdapter().addNewItem(hit);
                        }
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
                        if (!isLoaderInitialized && getAdapter().getItemCount() != 0){
                            isLoaderInitialized = true;
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
        int position = mRecyclerView.indexOfChild(event.getView());
        HitRecyclerViewAdapter adapter = (HitRecyclerViewAdapter) mRecyclerView.getAdapter();

        Hit campaign = adapter.getItem(position);

//        startActivity(new Intent(getActivity(), NewsActivity.class).putExtra(NEWS_ITEM, campaign));
    }

    protected int getErrorMessage(Exception exception) {
        return R.string.error_loading_news;
    }


}