package hk.ust.gmission.ui.fragments;

import android.accounts.AccountsException;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.otto.Bus;

import java.io.IOException;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import hk.ust.gmission.Injector;
import hk.ust.gmission.R;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.header.StoreHouseHeader;
import in.srain.cube.views.ptr.indicator.PtrIndicator;

/**
 * Created by bigstone on 21/12/2015.
 */
public abstract class BaseRecyclerViewFragment<E, A> extends Fragment {

    protected FragmentActivity mActivity;

    @Bind(R.id.recycler_view) RecyclerView mRecyclerView;


    /**
     * Empty view
     */
    @Bind(android.R.id.empty) TextView emptyView;

    /**
     * Progress bar
     */
    @Bind(R.id.pb_loading) ProgressBar progressBar;

    /**
     * Is the list currently shown?
     */
    protected boolean listShown;

    protected LinearLayoutManager mLayoutManager;

    @Inject protected Bus bus;

    private boolean CAN_LOAD_MORE = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }


    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.recycle_list, null);
    }

    protected BaseRecyclerViewFragment<E, A> setEmptyText(final String message) {
        if (emptyView != null) {
            emptyView.setText(message);
        }
        return this;
    }


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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (FragmentActivity) activity;
    }


    @Override
    public void onDestroyView() {
        listShown = false;
        emptyView = null;
        progressBar = null;
        mRecyclerView = null;

        super.onDestroyView();
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Injector.inject(this);
        ButterKnife.bind(this, view);
        mLayoutManager = new LinearLayoutManager(this.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if(dy > 0) //check for scroll down
                {
                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                    if (CAN_LOAD_MORE)
                    {
                        if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount - 2)
                        {
                            CAN_LOAD_MORE = false;
                            Log.v("...", "Last Item Wow !");
                            //TODO Do pagination.. i.e. fetch new data
                        }
                    }
                }
            }
        });
    }





    public boolean canScrollUp(View view) {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (view instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) view;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView
                        .getChildAt(0).getTop() < absListView.getPaddingTop());
            } else {
                return view.getScrollY() > 0;
            }
        } else {
            return ViewCompat.canScrollVertically(view, -1);
        }
    }

    protected A getAdapter(){
        return (A) mRecyclerView.getAdapter();
    }

    protected void loadData()throws IOException, AccountsException {

    }

    protected void configPullToRefresh(final View view){

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
