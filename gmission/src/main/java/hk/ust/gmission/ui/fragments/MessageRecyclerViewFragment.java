package hk.ust.gmission.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import hk.ust.gmission.R;
import hk.ust.gmission.core.Constants;
import hk.ust.gmission.core.api.QueryObject;
import hk.ust.gmission.events.HitAnswerSuccessEvent;
import hk.ust.gmission.events.MessageItemClickEvent;
import hk.ust.gmission.models.Answer;
import hk.ust.gmission.models.Hit;
import hk.ust.gmission.models.Message;
import hk.ust.gmission.models.ModelWrapper;
import hk.ust.gmission.services.AnswerService;
import hk.ust.gmission.services.MessageService;
import hk.ust.gmission.ui.activities.HitActivity;
import hk.ust.gmission.ui.adapters.MessageRecyclerViewAdapter;
import hk.ust.gmission.util.Ln;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

import static hk.ust.gmission.core.Constants.Extra.HIT;

public class MessageRecyclerViewFragment extends BaseRecyclerViewFragment<Message, MessageRecyclerViewAdapter> {

    protected MessageRecyclerViewAdapter adapter = new MessageRecyclerViewAdapter();

    private MessageRecyclerViewFragment mFragment = null;

    private boolean isLoaderInitialized = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragment = this;
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setEmptyText(getString(R.string.no_message));

        mRecyclerView.setAdapter(adapter);


        configPullToRefresh(getView());

    }

    @Override
    protected void loadData(){
        QueryObject queryObject = new QueryObject();
        queryObject.push("status", "neq", "deleted");
        queryObject.push("receiver_id", "eq", Constants.Http.PARAM_USER_ID);
        serviceProvider.getService(getActivity()).getMessageService().getMessages(queryObject.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnNext(new Action1<ModelWrapper<Message>>() {
                    @Override
                    public void call(ModelWrapper<Message> messages) {
                        getAdapter().setItems(messages.getObjects());
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
        final MessageRecyclerViewAdapter adapter = (MessageRecyclerViewAdapter) mRecyclerView.getAdapter();

        final Message message = adapter.getItem(position);
        message.setStatus("read");

        final MessageService messageService = serviceProvider.getService(this.getActivity()).getMessageService();
        final AnswerService answerService = serviceProvider.getService(this.getActivity()).getAnswerService();

        serviceProvider.getService(this.getActivity()).getHitService().getHit(message.getAttachment())
                .observeOn(Schedulers.io())
                .map(new Func1<Hit, Hit>() {

                    @Override
                    public Hit call(Hit hit) {
                        messageService.updateMessage(message.getId(), message)
                        .doOnNext(new Action1<Message>() {
                            @Override
                            public void call(Message message) {
                                Ln.d("Read Message:"+message.getId()+"|status:"+message.getStatus());
                            }
                        }).subscribe();
                        return hit;
                    }
                })
                .flatMap(new Func1<Hit, Observable<Hit>>() {
                    @Override
                    public Observable<Hit> call(Hit hit) {
                        QueryObject queryObject = new QueryObject();
                        queryObject.push("worker_id", "eq", Constants.Http.PARAM_USER_ID);
                        queryObject.push("hit_id", "eq", hit.getId());
                        return Observable.combineLatest(Observable.just(hit), answerService.getAnswers(queryObject.toString()),
                                new Func2<Hit, ModelWrapper<Answer>, Hit>() {
                            @Override
                            public Hit call(Hit hit, ModelWrapper<Answer> answerModelWrapper) {
                                if (answerModelWrapper.getNum_results() == 0){
                                    hit.setStatus("open");
                                } else {
                                    hit.setStatus("answered");
                                }
                                return hit;
                            }
                        });

                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<Hit, Observable<Hit>>() {//to switch to mainThread
                    @Override
                    public Observable<Hit> call(Hit hit) {
                        if (hit.getStatus().equals("answered")){
                            Toast.makeText(mFragment.getContext(), mFragment.getString(R.string.message_answered), Toast.LENGTH_SHORT).show();
                            return null;
                        } else {
                            return Observable.just(hit);
                        }
                    }
                })
                .doOnNext(new Action1<Hit>() {
                    @Override
                    public void call(Hit hit) {
                        hit.setMessage_id(message.getId());
                        startActivity(new Intent(getActivity(), HitActivity.class).putExtra(HIT, hit));
                    }
                })
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
        if (event.getType() == HitAnswerSuccessEvent.MESSAGE_TYPE){

        }
    }

}
