package hk.ust.gmission.ui.activities;

import android.accounts.AccountsException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.kevinsawicki.wishlist.Toaster;
import com.jakewharton.rxbinding.view.RxView;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.Bind;
import hk.ust.gmission.BootstrapServiceProvider;
import hk.ust.gmission.R;
import hk.ust.gmission.core.Constants;
import hk.ust.gmission.events.HitAnswerSuccessEvent;
import hk.ust.gmission.events.HitSubmitEnableEvent;
import hk.ust.gmission.models.Answer;
import hk.ust.gmission.models.Attachment;
import hk.ust.gmission.models.Hit;
import hk.ust.gmission.models.ImageVideoResult;
import hk.ust.gmission.ui.fragments.BaseAnswerFragment;
import hk.ust.gmission.ui.fragments.ImageHitFragment;
import hk.ust.gmission.ui.fragments.SelectionHitFragment;
import hk.ust.gmission.ui.fragments.TextHitFragment;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static hk.ust.gmission.core.Constants.Extra.HIT;


public class HitActivity extends BootstrapFragmentActivity {

    private Hit mHit = null;
    private HitActivity mActivity;

    @Bind(R.id.submit_btn) Button submitButton;
    @Bind(R.id.hit_content) TextView hitContent;
    @Bind(R.id.bad_hid_notification) TextView badHitNotificationText;

    @Inject protected BootstrapServiceProvider serviceProvider;

    private BaseAnswerFragment answerFragment = null;
    private Subscription buttonSubscription = null;
    private static int BUTTON_PRESS_DELAY_MILLIS = 1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hit_activity);
        mActivity = this;

        initializeAnswerArea();

        subscribeSubmitButton();
    }


    public void initializeAnswerArea(){
        if (getIntent() != null && getIntent().getExtras() != null) {
            mHit = (Hit) getIntent().getExtras().get(HIT);
            hitContent.setText(mHit.getDescription());
        }

        if (mHit.getType().equals("selection")) {
            answerFragment = SelectionHitFragment.newInstance(mHit);
        }


        if (mHit.getType().equals("text")) {
            answerFragment = TextHitFragment.newInstance();
        }


        if (mHit.getType().equals("image")) {
            answerFragment = ImageHitFragment.newInstance();
        }

        if (answerFragment != null){
            badHitNotificationText.setVisibility(View.GONE);
            replaceContainerFragment(answerFragment);
        }
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
                .map(new Func1<Object, Object>() {
                    @Override
                    public Object call(Object o) {
                        mActivity.showProgress();
                        return o;
                    }
                })
                .observeOn(Schedulers.io())
                .flatMap(new Func1<Object, Observable<ImageVideoResult>>() {
                    @Override
                    public Observable<ImageVideoResult> call(Object stringObservable) {

                        File imageFile = answerFragment.getImageFile();
                        if (imageFile == null){
                            ImageVideoResult imageVideoResult = new ImageVideoResult();
                            imageVideoResult.setFilename(null);
                            return Observable.just(imageVideoResult);
                        }
                        TypedFile typedFile = new TypedFile("image/jpeg", imageFile);
                        TypedString typedString = new TypedString("file");

                        Observable<ImageVideoResult> observable = null;
                        try {
//                            System.setProperty("http.keepAlive", "false");
                            observable = serviceProvider.getService(mActivity).getAttachmentService().createImage(typedFile, typedString);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (AccountsException e) {
                            e.printStackTrace();
                        }
                        return observable;
                    }
                })
                .flatMap(new Func1<ImageVideoResult, Observable<Attachment>>() {
                    @Override
                    public Observable<Attachment> call(ImageVideoResult imageVideoResult) {
                        if (imageVideoResult.getFilename() == null){
                            Attachment attachment = new Attachment();
                            attachment.setId(null);
                            return Observable.just(attachment);
                        }
                        File imageFile = answerFragment.getImageFile();
                        Attachment attachment = new Attachment();
                        attachment.setType("image");
                        attachment.setName(imageFile.getName());
                        attachment.setValue(imageVideoResult.getFilename());

                        Observable<Attachment> observable = null;

                        try {
                            observable = serviceProvider.getService(mActivity).getAttachmentService().createAttachment(attachment);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (AccountsException e) {
                            e.printStackTrace();
                        }

                        return observable;
                    }
                })
                .flatMap(new Func1<Attachment, Observable<Answer>>() {
                    @Override
                    public Observable<Answer> call(Attachment attachment) {
                        String answerBrief = answerFragment.getAnswer();
                        Answer answer = new Answer();
                        answer.setBrief(answerBrief);
                        answer.setHit_id(mHit.getId());
                        answer.setType(mHit.getType());
                        answer.setWorker_id(Constants.Http.PARAM_USER_ID);
                        if (attachment.getId() != null){
                            answer.setAttachment_id(attachment.getId());
                        }


                        Observable<Answer> observable = null;

                        try {
                            observable =  serviceProvider.getService(mActivity).getAnswerService().postAnswer(answer);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (AccountsException e) {
                            e.printStackTrace();
                        }

                        return observable;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<Answer>() {
                    @Override
                    public void call(Answer answer) {
                        if (mHit.getMessage_id() != null){
                            bus.post(new HitAnswerSuccessEvent(null, mHit.getMessage_id(), HitAnswerSuccessEvent.MESSAGE_TYPE));
                        } else {
                            bus.post(new HitAnswerSuccessEvent(answer.getHit_id(), null, HitAnswerSuccessEvent.CAMPAIGN_TYPE));
                        }

                        Toaster.showShort(mActivity, mActivity.getString(R.string.message_answer_success));
                        mActivity.hideProgress();
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
                .subscribe();
    }


}
