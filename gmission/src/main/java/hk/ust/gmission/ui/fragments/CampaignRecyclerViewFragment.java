package hk.ust.gmission.ui.fragments;

import android.accounts.AccountsException;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import hk.ust.gmission.BootstrapServiceProvider;
import hk.ust.gmission.Injector;
import hk.ust.gmission.R;
import hk.ust.gmission.events.CampaignItemClickEvent;
import hk.ust.gmission.events.NewsItemClickEvent;
import hk.ust.gmission.models.dao.Campaign;
import hk.ust.gmission.models.dao.News;
import hk.ust.gmission.models.wrapper.CampaignWrapper;
import hk.ust.gmission.models.wrapper.NewsWrapper;
import hk.ust.gmission.ui.activities.NewsActivity;
import hk.ust.gmission.ui.adapters.CampaignRecyclerViewAdapter;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.header.StoreHouseHeader;
import in.srain.cube.views.ptr.indicator.PtrIndicator;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static hk.ust.gmission.core.Constants.Extra.NEWS_ITEM;

public class CampaignRecyclerViewFragment extends BaseRecyclerViewFragment<Campaign, CampaignRecyclerViewAdapter> {

    @Inject protected BootstrapServiceProvider serviceProvider;
    @Inject protected CampaignRecyclerViewAdapter campaignRecyclerViewAdapter;

    private boolean isLoaderInitialized = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Injector.inject(this);
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setEmptyText(getString(R.string.no_campaign));

        mRecyclerView.setAdapter(campaignRecyclerViewAdapter);


        configPullToRefresh(getView());

    }

    private void loadData() throws IOException, AccountsException {
        serviceProvider.getService(getActivity()).getCampaignService().getCampaigns()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnNext(new Action1<CampaignWrapper>() {
                    @Override
                    public void call(CampaignWrapper campaigns) {
                        if (!isLoaderInitialized){
                            getAdapter().setItems(campaigns.getObjects());
                        } else {
                            Campaign campaign = new Campaign();
                            campaign.setTitle("Test News");
                            campaign.setBrief("This is a test news");
                            campaign.setId(1);
                            getAdapter().addNewItem(campaign);
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
                        if (!isLoaderInitialized){
                            isLoaderInitialized = true;
                        }

                        getAdapter().notifyDataSetChanged();

                    }
                })
                .subscribe();
    }





    @Override
    public void onDestroyView() {
        getAdapter().setItems(null);

        super.onDestroyView();
    }


    @Subscribe
    public void onListItemClick(CampaignItemClickEvent event) {
        int position = mRecyclerView.indexOfChild(event.getView());
        CampaignRecyclerViewAdapter adapter = (CampaignRecyclerViewAdapter) mRecyclerView.getAdapter();

        Campaign campaign = adapter.getItem(position);

//        startActivity(new Intent(getActivity(), NewsActivity.class).putExtra(NEWS_ITEM, campaign));
    }

    protected int getErrorMessage(Exception exception) {
        return R.string.error_loading_news;
    }

    private void configPullToRefresh(final View view){

        final StoreHouseHeader header = new StoreHouseHeader(this.getActivity().getApplicationContext());
        header.setPadding(0, 15, 0, 0);
        header.initWithString(getString(R.string.loading));
        final PtrFrameLayout frame = (PtrFrameLayout) view.findViewById(R.id.ptr_frame);
        frame.addPtrUIHandler(new PtrUIHandler() {

            @Override
            public void onUIReset(PtrFrameLayout frame) {
                header.initWithString(getString(R.string.refreshing));
            }

            @Override
            public void onUIRefreshPrepare(PtrFrameLayout frame) {
            }

            @Override
            public void onUIRefreshBegin(PtrFrameLayout frame) {
                try {
                    loadData();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (AccountsException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onUIRefreshComplete(PtrFrameLayout frame) {

            }

            @Override
            public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {

            }
        });

        frame.setDurationToCloseHeader(1000);
        frame.setHeaderView(header);
        frame.addPtrUIHandler(header);
        frame.postDelayed(new Runnable() {
            @Override
            public void run() {
                frame.autoRefresh(false);
            }
        }, 200);

        frame.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                if (canScrollUp(mRecyclerView)){
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public void onRefreshBegin(final PtrFrameLayout frame) {
                frame.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        frame.refreshComplete();
                    }
                }, 200);
            }
        });
    }
}
