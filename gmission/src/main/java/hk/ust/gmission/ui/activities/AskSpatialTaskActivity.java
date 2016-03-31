package hk.ust.gmission.ui.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.kevinsawicki.wishlist.Toaster;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxSeekBar;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import hk.ust.gmission.R;
import hk.ust.gmission.core.Constants;
import hk.ust.gmission.events.TaskCreateSuccessEvent;
import hk.ust.gmission.models.Coordinate;
import hk.ust.gmission.models.GeoLocation;
import hk.ust.gmission.models.Hit;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

import static hk.ust.gmission.core.Constants.Extra.COORDINATE;
import static hk.ust.gmission.core.Constants.Extra.LOCATION_NAME;


public class AskSpatialTaskActivity extends BootstrapFragmentActivity {
    private static int TEXT_CHECK__DELAY_MILLIS = 1000;
    private static int BUTTON_PRESS_DELAY_MILLIS = 1000;

    @Bind(R.id.et_location_name) TextView mEtLocation;
    @Bind(R.id.et_content) EditText mEtContent;
    @Bind(R.id.et_title) EditText mEtTitle;
    @Bind(R.id.selection_radio) RadioGroup selectionRadioGroup;


    @Bind(R.id.creditTxt) TextView mTxtCredit;
    @Bind(R.id.answersTxt) TextView mTxtAnswersCount;
    @Bind(R.id.timeTxt) TextView mTxtTime;



    @Bind(R.id.creditSeekBar) SeekBar mSkbCredit;
    @Bind(R.id.answerSeekBar) SeekBar mSkbAnswersCount;
    @Bind(R.id.timeSeekBar) SeekBar mSkbTime;

    @Bind(R.id.submit_btn) Button submitButton;
    
    private AskSpatialTaskActivity mActivity;
    private Coordinate mCoordinate;
    private String mLocationName;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ask_saptial_task_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mActivity = this;

        initializeQuestionContent();

        subcribeViews();
        subcribeSubmitButton();

    }


    public void initializeQuestionContent(){
        if (getIntent() != null && getIntent().getExtras() != null) {
            mCoordinate = (Coordinate) getIntent().getExtras().get(COORDINATE);
            mLocationName = getIntent().getExtras().getString(LOCATION_NAME);
            mEtLocation.setText(mLocationName);
        }
    }

    public void subcribeSubmitButton(){
        RxView.clicks(submitButton)
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
                .flatMap(new Func1<Object, Observable<Coordinate>>() {
                    @Override
                    public Observable<Coordinate> call(Object o) {
                        return serviceProvider.getService().getGeoService().createCoordinate(mCoordinate);
                    }
                })
                .flatMap(new Func1<Coordinate, Observable<GeoLocation>>() {
                    @Override
                    public Observable<GeoLocation> call(Coordinate coordinate) {
                        GeoLocation geoLocation = new GeoLocation();
                        geoLocation.setCoordinate_id(coordinate.getId());
                        geoLocation.setName(mEtLocation.getText().toString());

                        return serviceProvider.getService().getGeoService().createGeoLocation(geoLocation);
                    }
                })
                .flatMap(new Func1<GeoLocation, Observable<Hit>>() {
                    @Override
                    public Observable<Hit> call(GeoLocation geoLocation) {
                        Hit hit = new Hit();

                        hit.setRequester_id(Constants.Http.PARAM_USER_ID);
                        hit.setLocation_id(geoLocation.getId());
                        hit.setTitle(mEtTitle.getText().toString());
                        hit.setDescription(mEtContent.getText().toString());
                        hit.setStatus("open");

                        int checkedRdId = selectionRadioGroup.getCheckedRadioButtonId();
                        switch (checkedRdId){
                            case R.id.rb_text_type:
                                hit.setType(Constants.Extra.TEXT_TYPE);break;
                            case R.id.rb_image_type:
                                hit.setType(Constants.Extra.IMAGE_TYPE);break;
                            case R.id.rb_choice_type:
                                hit.setType(Constants.Extra.CHOICE_TYPE);break;
                        }


                        int minutes_10 = mSkbTime.getProgress() + 1;
                        hit.setEnd_time(new Date(Calendar.getInstance().getTime().getTime() + (minutes_10 * 10 * 60 * 1000)));

                        int credits = mSkbCredit.getProgress() + 1;
                        hit.setCredit(credits);

                        int requiredAnswers = mSkbAnswersCount.getProgress() + 1;
                        hit.setRequired_answer_count(requiredAnswers);

                        return serviceProvider.getService().getHitService().createHit(hit);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<Hit>() {
                    @Override
                    public void call(Hit hit) {
                        Toaster.showShort(mActivity, mActivity.getString(R.string.message_ask_question_success));
                        bus.post(new TaskCreateSuccessEvent());
                        mActivity.hideProgress();
                        finish();
                    }
                })
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                        mActivity.hideProgress();
                    }
                })
                .subscribe();
    }

    public void subcribeViews(){
        RxSeekBar.userChanges(mSkbCredit).doOnNext(new Action1<Integer>() {
            @Override
            public void call(Integer progress) {
                progress++;
                mTxtCredit.setText(String.valueOf(progress));
            }
        }).subscribe();

        RxSeekBar.userChanges(mSkbAnswersCount).doOnNext(new Action1<Integer>() {
            @Override
            public void call(Integer progress) {
                progress++;
                mTxtAnswersCount.setText(String.valueOf(progress));
            }
        }).subscribe();

        RxSeekBar.userChanges(mSkbTime).doOnNext(new Action1<Integer>() {
            @Override
            public void call(Integer progress) {
                progress++;
                int hours = progress / 6;
                int minutes_10 = progress % 6;
                mTxtTime.setText(String.valueOf(hours+":"+minutes_10+"0"));
            }
        }).subscribe();


        Observable<CharSequence> titleObserver = RxTextView.textChanges(mEtTitle).debounce(TEXT_CHECK__DELAY_MILLIS, TimeUnit.MILLISECONDS);
        Observable<CharSequence> contentObserver = RxTextView.textChanges(mEtContent).debounce(TEXT_CHECK__DELAY_MILLIS, TimeUnit.MILLISECONDS);

        Observable.combineLatest(titleObserver, contentObserver, new Func2<CharSequence, CharSequence, Boolean>() {

                    @Override
                    public Boolean call(CharSequence title, CharSequence content) {
                        String location = mEtLocation.getText().toString();
                        return title.length() >= 5 && content.length() >= 5 && location.length() > 0;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean enableButten) {
                        if (enableButten){
                            submitButton.setEnabled(true);
                        }
                    }
                })
                .subscribe();
    }







    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;

    }

}
