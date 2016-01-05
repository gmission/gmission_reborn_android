package hk.ust.gmission.ui.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jakewharton.rxbinding.widget.RxSeekBar;

import butterknife.Bind;
import hk.ust.gmission.R;
import rx.functions.Action1;

public class AskSpatialTaskActivity extends BootstrapFragmentActivity {



    @Bind(R.id.et_location_name) TextView mEtLocation;
    @Bind(R.id.et_content) EditText mEtContent;



    @Bind(R.id.creditTxt) TextView mTxtCredit;
    @Bind(R.id.answersTxt) TextView mTxtAnswersCount;
    @Bind(R.id.timeTxt) TextView mTxtTime;



    @Bind(R.id.creditSeekBar) SeekBar mSkbCredit;
    @Bind(R.id.answerSeekBar) SeekBar mSkbAnswersCount;
    @Bind(R.id.timeSeekBar) SeekBar mSkbTime;

    @Bind(R.id.submit_btn) Button submitButton;
    
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ask_saptial_task_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        subcribeViews();

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
    }







    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;

    }

}
