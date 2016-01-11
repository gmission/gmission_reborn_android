package hk.ust.gmission.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import hk.ust.gmission.BootstrapServiceProvider;
import hk.ust.gmission.Injector;
import hk.ust.gmission.R;
import hk.ust.gmission.core.Constants;
import hk.ust.gmission.core.api.QueryObject;
import hk.ust.gmission.models.Answer;
import hk.ust.gmission.models.Hit;
import hk.ust.gmission.models.ModelWrapper;
import hk.ust.gmission.models.User;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func3;

/**
 * Created by bigstone on 3/1/2016.
 */
public class UserProfilePFragment extends Fragment {

    @Bind(R.id.tv_answernum) TextView mTvAnswernum;
    @Bind(R.id.tv_requestnum) TextView mTvRequestnum;
    @Bind(R.id.username) TextView mTvUsername;
    @Bind(R.id.credit) TextView mTvCredit;
    @Bind(android.R.id.empty) TextView mTvError;

    @Bind(R.id.rl_profile) RelativeLayout mRlProfile;
    @Bind(R.id.list_container) FrameLayout mFlContainer;
    @Bind(R.id.pb_loading) ProgressBar mPorgressbar;

    @Inject protected BootstrapServiceProvider serviceProvider;



    private User currentUser;
    private int requestNum =0;
    private int answerNum =0;

    private TaskRecyclerViewFragment questionFragment = new TaskRecyclerViewFragment();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.user_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Injector.inject(this);
        ButterKnife.bind(this, view);
        loadUserProfile();

        final FragmentManager fragmentManager = getFragmentManager();

        if (mFlContainer.getId() != questionFragment.getId()){
            fragmentManager.beginTransaction()
                    .replace(mFlContainer.getId(), questionFragment)
                    .commitAllowingStateLoss();
        } else {
            fragmentManager
                    .beginTransaction()
                    .detach(questionFragment)
                    .attach(questionFragment)
                    .commit();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void loadUserProfile(){

        Observable<User> userObservable = serviceProvider.getService(this.getActivity()).getUserService().getUser(Constants.Http.PARAM_USER_ID);


        QueryObject queryObject = new QueryObject();
        queryObject.push("requester_id", "eq", Constants.Http.PARAM_USER_ID);
        Observable<ModelWrapper<Hit>> hitObservable = serviceProvider.getService(this.getActivity()).getHitService().getHits(queryObject.toString());

        queryObject = new QueryObject();
        queryObject.push("worker_id", "eq", Constants.Http.PARAM_USER_ID);
        Observable<ModelWrapper<Answer>> answerObservable = serviceProvider.getService(this.getActivity()).getAnswerService().getAnswers(queryObject.toString());

        Observable.combineLatest(userObservable, hitObservable, answerObservable,
                new Func3<User, ModelWrapper<Hit>, ModelWrapper<Answer>, Boolean>() {
                    @Override
                    public Boolean call(User user, ModelWrapper<Hit> hitModelWrapper, ModelWrapper<Answer> answerModelWrapper) {
                        currentUser = user;
                        requestNum = hitModelWrapper.getNum_results();
                        answerNum = answerModelWrapper.getNum_results();
                        return true;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        mTvUsername.setText(currentUser.getUsername());
                        mTvCredit.setText(String.format("Credit: %d", currentUser.getCredit()));
                        mTvAnswernum.setText(String.valueOf(answerNum));
                        mTvRequestnum.setText(String.valueOf(requestNum));
                    }
                })
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        mRlProfile.setVisibility(View.VISIBLE);
                        mPorgressbar.setVisibility(View.GONE);
                    }
                })
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        mTvError.setVisibility(View.VISIBLE);
                    }
                })
                .subscribe();
    }
}
