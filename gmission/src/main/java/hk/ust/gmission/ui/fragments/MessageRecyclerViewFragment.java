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
import hk.ust.gmission.events.MessageItemClickEvent;
import hk.ust.gmission.models.Message;
import hk.ust.gmission.models.ModelWrapper;
import hk.ust.gmission.ui.activities.HitListActivity;
import hk.ust.gmission.ui.adapters.MessageRecyclerViewAdapter;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

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
        queryObject.push("status", "eq", "new");
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

        Message message = adapter.getItem(position);

        startActivity(new Intent(getActivity(), HitListActivity.class).putExtra(MESSAGE_ID, String.valueOf(message.getId())));
    }



}
