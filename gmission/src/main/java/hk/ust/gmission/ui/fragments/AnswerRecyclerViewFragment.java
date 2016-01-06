package hk.ust.gmission.ui.fragments;

import android.os.Bundle;
import android.view.View;

import hk.ust.gmission.R;
import hk.ust.gmission.core.api.QueryObject;
import hk.ust.gmission.models.Answer;
import hk.ust.gmission.models.ModelWrapper;
import hk.ust.gmission.ui.adapters.AnswerRecyclerViewAdapter;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by bigstone on 6/1/2016.
 */
public class AnswerRecyclerViewFragment extends BaseRecyclerViewFragment<Answer, AnswerRecyclerViewAdapter> {

    protected AnswerRecyclerViewAdapter mAdapter;

    private String hitId = null;

    public void setHitId(String hitId) {
        this.hitId = hitId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setEmptyText(getString(R.string.no_answer));
        mAdapter = new AnswerRecyclerViewAdapter(serviceProvider.getService(this.getActivity()).getHitService()
                ,serviceProvider.getService(this.getActivity()).getAttachmentService()
                ,serviceProvider.getService(this.getActivity()).getAnswerService());

        mRecyclerView.setAdapter(mAdapter);

        configPullToRefresh(getView());



    }

    @Override
    protected void loadData() {

        QueryObject queryObject = new QueryObject();
        queryObject.push("hit_id", "eq", hitId);

        serviceProvider.getService(getActivity()).getAnswerService().getAnswers(queryObject.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnNext(new Action1<ModelWrapper<Answer>>() {
                    @Override
                    public void call(ModelWrapper<Answer> campaigns) {
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


}
