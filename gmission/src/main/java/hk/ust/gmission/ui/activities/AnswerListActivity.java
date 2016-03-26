package hk.ust.gmission.ui.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;

import hk.ust.gmission.R;
import hk.ust.gmission.ui.fragments.AnswerRecyclerViewFragment;

import static hk.ust.gmission.core.Constants.Extra.HIT_ID;
import static hk.ust.gmission.core.Constants.Extra.IS_VIEW_ANSWER;


public class AnswerListActivity extends BootstrapFragmentActivity {


    private String mHitId = null;
    private boolean isViewOnly = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.answer_list_activity);
        if (getIntent() != null && getIntent().getExtras() != null) {
            mHitId =  getIntent().getExtras().getString(HIT_ID);
            isViewOnly = getIntent().getBooleanExtra(IS_VIEW_ANSWER, true);
        }

        AnswerRecyclerViewFragment answerListFragment = new AnswerRecyclerViewFragment();
        answerListFragment.setHitId(mHitId);
        answerListFragment.setViewOnly(isViewOnly);
        replaceCurrentFragment(answerListFragment);
    }


    private void replaceCurrentFragment(Fragment newFragment){
        final FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .replace(R.id.container, newFragment)
                .commitAllowingStateLoss();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;

    }

}
