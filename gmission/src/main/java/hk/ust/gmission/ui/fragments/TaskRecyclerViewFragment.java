package hk.ust.gmission.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.squareup.otto.Subscribe;

import hk.ust.gmission.R;
import hk.ust.gmission.core.Constants;
import hk.ust.gmission.core.api.QueryObject;
import hk.ust.gmission.events.HitItemClickEvent;
import hk.ust.gmission.events.TaskItemClickEvent;
import hk.ust.gmission.models.Hit;
import hk.ust.gmission.models.ModelWrapper;
import hk.ust.gmission.ui.activities.AnswerListActivity;
import hk.ust.gmission.ui.adapters.TaskRecyclerViewAdapter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static hk.ust.gmission.core.Constants.Extra.HIT_ID;

/**
 * Created by bigstone on 6/1/2016.
 */
public class TaskRecyclerViewFragment  extends BaseRecyclerViewFragment<Hit, TaskRecyclerViewAdapter> {
    protected TaskRecyclerViewAdapter mAdapter = new TaskRecyclerViewAdapter();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setEmptyText(getString(R.string.no_hit));

        mRecyclerView.setAdapter(mAdapter);

        configPullToRefresh(getView());
    }

    @Override
    public void onResume() {
        super.onResume();
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    protected void loadData() {
        Observable<ModelWrapper<Hit>> observable;

        QueryObject queryObject = new QueryObject();
        queryObject.push("requester_id", "eq", Constants.Http.PARAM_USER_ID);
        queryObject.push("campaign_id", "is_null", "");
        observable = serviceProvider.getService(getActivity()).getHitService().getHits(queryObject.toString());


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




    @Override
    public void onDestroyView() {

        super.onDestroyView();
    }


    @Subscribe
    public void onListItemClick(TaskItemClickEvent event) {
        int position = mRecyclerView.getChildLayoutPosition(event.getView());
        TaskRecyclerViewAdapter adapter = (TaskRecyclerViewAdapter) mRecyclerView.getAdapter();

        Hit hit = adapter.getItem(position);

        startActivity(new Intent(getActivity(), AnswerListActivity.class).putExtra(HIT_ID, hit.getId()));
    }


}
