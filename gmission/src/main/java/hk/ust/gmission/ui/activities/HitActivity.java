package hk.ust.gmission.ui.activities;

import android.accounts.AccountsException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.widget.Button;
import android.widget.TextView;

import com.github.kevinsawicki.wishlist.Toaster;
import com.jakewharton.rxbinding.view.RxView;
import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.Bind;
import hk.ust.gmission.BootstrapServiceProvider;
import hk.ust.gmission.R;
import hk.ust.gmission.core.Constants;
import hk.ust.gmission.events.HitSubmitEnableEvent;
import hk.ust.gmission.models.dao.Answer;
import hk.ust.gmission.models.dao.Hit;
import hk.ust.gmission.ui.fragments.BaseAnswerFragment;
import hk.ust.gmission.ui.fragments.SelectionHitFragment;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static hk.ust.gmission.core.Constants.Extra.HIT;


public class HitActivity extends BootstrapFragmentActivity {

    private Hit mHit = null;
    private HitActivity mActivity;

    @Bind(R.id.submit_btn) Button submitButton;
    @Bind(R.id.hit_content) TextView hitContent;
    @Inject
    protected BootstrapServiceProvider serviceProvider;

    private BaseAnswerFragment answerFragment = null;
    private Subscription buttonSubscription = null;
    private static int BUTTON_PRESS_DELAY_MILLIS = 1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hit_activity);

        if (getIntent() != null && getIntent().getExtras() != null) {
            mHit = (Hit) getIntent().getExtras().get(HIT);
            hitContent.setText(mHit.getDescription());
        }

        if (mHit.getType().equals("selection")) {
            answerFragment = SelectionHitFragment.newInstance(mHit);

        }

        mActivity = this;
        replaceContainerFragment(answerFragment);
        subscribeSubmitButton();

    }

    @Subscribe
    public void onSubmitEnable(HitSubmitEnableEvent event) {
        submitButton.setEnabled(true);
    }


    private void replaceContainerFragment(Fragment newFragment) {
        final FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .replace(R.id.container, newFragment)
                .commitAllowingStateLoss();

    }

    private void subscribeSubmitButton() {
        buttonSubscription = RxView.clicks(submitButton)
                .debounce(BUTTON_PRESS_DELAY_MILLIS, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        finish();
                    }
                })
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable e) {
                        Throwable cause = e.getCause() != null ? e.getCause() : e;

                        String message;
                        // A 404 is returned as an Exception with this message
                        if ("Received authentication challenge is null".equals(cause.getMessage())) {
                            message = getResources().getString(R.string.message_reg_error);
                        } else {
                            message = cause.getMessage();
                        }

                        Toaster.showLong(HitActivity.this, message);

                    }
                })
                .observeOn(Schedulers.io())
                .doOnNext(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        String answerBrief = answerFragment.getAnswer();
                        Answer answer = new Answer();
                        answer.setBrief(answerBrief);
                        answer.setHit_id(mHit.getId());
                        answer.setType("selection");
                        answer.setWorker_id(Integer.valueOf(Constants.Http.PARAM_USER_ID));
                        answer.setAttachment_id(1);
//                        answer.setLocation_id(2);

                        try {
                            serviceProvider.getService(mActivity).getAnswerService().postAnswer(answer)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(Schedulers.io())
                                    .doOnNext(new Action1<Answer>() {
                                        @Override
                                        public void call(Answer answer) {

                                        }
                                    })
                                    .doOnError(new Action1<Throwable>() {
                                        @Override
                                        public void call(Throwable e) {
                                            e.printStackTrace();
                                        }
                                    })
                                    .subscribe();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (AccountsException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .subscribe();
    }


}
