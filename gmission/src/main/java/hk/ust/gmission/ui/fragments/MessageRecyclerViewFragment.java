package hk.ust.gmission.ui.fragments;

import android.accounts.AccountsException;
import android.content.Intent;
import android.os.Bundle;

import com.squareup.otto.Subscribe;

import java.io.IOException;

import javax.inject.Inject;

import hk.ust.gmission.BootstrapServiceProvider;
import hk.ust.gmission.R;
import hk.ust.gmission.core.Constants;
import hk.ust.gmission.core.api.QueryObject;
import hk.ust.gmission.events.HitAnswerSuccessEvent;
import hk.ust.gmission.events.MessageItemClickEvent;
import hk.ust.gmission.models.Hit;
import hk.ust.gmission.models.Message;
import hk.ust.gmission.models.ModelWrapper;
import hk.ust.gmission.ui.activities.HitActivity;
import hk.ust.gmission.ui.activities.HitListActivity;
import hk.ust.gmission.ui.adapters.MessageRecyclerViewAdapter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static hk.ust.gmission.core.Constants.Extra.HIT;
import static hk.ust.gmission.core.Constants.Extra.MESSAGE_ID;

public class MessageRecyclerViewFragment extends BaseRecyclerViewFragment<Message, MessageRecyclerViewAdapter> {

    @Inject protected BootstrapServiceProvider serviceProvider;
    protected MessageRecyclerViewAdapter messageRecyclerViewAdapter = new MessageRecyclerViewAdapter();

    private boolean isLoaderInitialized = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Injector.inject(this);
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setEmptyText(getString(R.string.no_message));

        mRecyclerView.setAdapter(messageRecyclerViewAdapter);


        configPullToRefresh(getView());

    }

    @Override
    protected void loadData() throws IOException, AccountsException {
        QueryObject queryObject = new QueryObject();
        queryObject.push("status", "neq", "deleted");
        queryObject.push("receiver_id", "eq", Constants.Http.PARAM_USER_ID);
        serviceProvider.getService(getActivity()).getMessageService().getMessages(queryObject.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnNext(new Action1<ModelWrapper<Message>>() {
                    @Override
                    public void call(ModelWrapper<Message> messages) {
                        if (!isLoaderInitialized){
                            getAdapter().setItems(messages.getObjects());
                        } else {
                            getAdapter().setItems(messages.getObjects());
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

        super.onDestroyView();
    }


    @Subscribe
    public void onListItemClick(MessageItemClickEvent event) {
        int position = mRecyclerView.getChildLayoutPosition(event.getView());
        MessageRecyclerViewAdapter adapter = (MessageRecyclerViewAdapter) mRecyclerView.getAdapter();

        final Message message = adapter.getItem(position);

        Observable<Hit> hitObservable = null;

        try {
            hitObservable = serviceProvider.getService(this.getActivity()).getHitService().getHit(message.getAttachment());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AccountsException e) {
            e.printStackTrace();
        }

        if (hitObservable != null){
            hitObservable
                    .doOnNext(new Action1<Hit>() {
                        @Override
                        public void call(Hit hit) {
                            hit.setMessage_id(message.getId());
                            message.setStatus("read");

                            getAdapter().notifyDataSetChanged();
                            startActivity(new Intent(getActivity(), HitActivity.class).putExtra(HIT, hit));
                        }
                    }).subscribe();
        }

    }

    @Subscribe
    public void onHitAnswerSuccess(HitAnswerSuccessEvent event){
        if (event.getType() == HitAnswerSuccessEvent.MESSAGE_TYPE){

        }
    }

}
