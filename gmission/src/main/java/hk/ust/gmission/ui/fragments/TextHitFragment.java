package hk.ust.gmission.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import hk.ust.gmission.R;
import hk.ust.gmission.events.HitSubmitEnableEvent;
import rx.functions.Action1;

public class TextHitFragment extends BaseAnswerFragment {

    @Bind(R.id.hit_edit_text) EditText answerText;

    private static int BUTTON_PRESS_DELAY_MILLIS = 500;


    public TextHitFragment() {
        // Required empty public constructor
    }


    public static TextHitFragment newInstance() {
        TextHitFragment fragment = new TextHitFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.text_hit_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        subcribeEditText();
    }

    public void subcribeEditText(){
        RxTextView.textChanges(answerText)
            .debounce(BUTTON_PRESS_DELAY_MILLIS, TimeUnit.MILLISECONDS)
            .doOnNext(new Action1<CharSequence>() {
                @Override
                public void call(CharSequence charSequence) {
                    if (answerText.getText().toString().length() > 0){
                        bus.post(new HitSubmitEnableEvent());
                    }
                }

            })
            .doOnError(new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    throwable.printStackTrace();
                }

            })
            .subscribe();

    }

    @Override
    public String getAnswer(){

        return answerText.getText().toString();
    }
}
