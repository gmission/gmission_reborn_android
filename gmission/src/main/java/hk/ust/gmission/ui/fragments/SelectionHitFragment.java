package hk.ust.gmission.ui.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.jakewharton.rxbinding.widget.RxRadioGroup;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import hk.ust.gmission.BootstrapServiceProvider;
import hk.ust.gmission.R;
import hk.ust.gmission.core.api.QueryObject;
import hk.ust.gmission.events.HitSubmitEnableEvent;
import hk.ust.gmission.models.Hit;
import hk.ust.gmission.models.ModelWrapper;
import hk.ust.gmission.models.Selection;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import static hk.ust.gmission.core.Constants.Extra.HIT;
import static hk.ust.gmission.core.Constants.Extra.ANSWRE_BRIEF;


public class SelectionHitFragment extends BaseAnswerFragment {


    private Hit mHit;
    private String answerBrief = null;


    @Bind(R.id.selection_radio) RadioGroup selectionRadioGroup;


    @Inject protected BootstrapServiceProvider serviceProvider;

    private int radioButtonCount = 0;

    private List<Selection> selections;


    public SelectionHitFragment() {
        // Required empty public constructor
    }

    public static SelectionHitFragment newInstance(Hit hit, String answerBrief) {
        SelectionHitFragment fragment = new SelectionHitFragment();
        Bundle args = new Bundle();
        args.putSerializable(HIT, hit);
        args.putString(ANSWRE_BRIEF, answerBrief);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mHit = (Hit)getArguments().get(HIT);
            this.answerBrief = getArguments().getString(ANSWRE_BRIEF);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.selection_hit_fragment, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadHitSelection();

        subcribeRadioGroup();
    }

    public void subcribeRadioGroup(){
        RxRadioGroup.checkedChanges(selectionRadioGroup)
                .doOnNext(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        bus.post(new HitSubmitEnableEvent());
                    }
                })
                .subscribe();
    }


    public void addSelectionRadioButton(Selection selection){
        RadioButton rdbtn = new RadioButton(this.getContext());
        radioButtonCount++;
        rdbtn.setId(R.id.selection_radio + radioButtonCount);
        rdbtn.setText(selection.getBrief());
        rdbtn.setTextColor(ContextCompat.getColor(this.getContext(), R.color.text_shadow));
        selectionRadioGroup.addView(rdbtn);
    }

    @Override
    public String getAnswer(){
        int index = selectionRadioGroup.getCheckedRadioButtonId() - R.id.selection_radio - 1;
        Selection selection = selections.get(index);
        return selection.getId();
    }

    public void loadHitSelection(){
        QueryObject queryObject = new QueryObject();
        queryObject.push("hit_id", "eq", mHit.getId());
        serviceProvider.getService(getActivity()).getHitService().getSelections(queryObject.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnNext(new Action1<ModelWrapper<Selection>>() {
                    @Override
                    public void call(ModelWrapper<Selection> selectionModelWrapper) {
                        selections = selectionModelWrapper.getObjects();
                    }
                })
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable e) {
                        e.printStackTrace();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<ModelWrapper<Selection>, Object>() {

                    @Override
                    public Object call(ModelWrapper<Selection> selectionModelWrapper) {
                        for (Selection selection : selectionModelWrapper.getObjects()){
                            addSelectionRadioButton(selection);
                        }
                        return null;
                    }
                })
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {

                    }
                })
                .subscribe();
    }
}