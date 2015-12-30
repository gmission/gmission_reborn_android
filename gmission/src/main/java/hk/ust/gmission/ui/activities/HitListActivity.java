package hk.ust.gmission.ui.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import hk.ust.gmission.R;
import hk.ust.gmission.ui.fragments.HitRecyclerViewFragment;

import static hk.ust.gmission.core.Constants.Extra.CAMPAIGN_ID;


public class HitListActivity extends BootstrapFragmentActivity {

    private String campaignId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hit_list_activity);
        if (getIntent() != null && getIntent().getExtras() != null) {
            campaignId =  getIntent().getExtras().getString(CAMPAIGN_ID);
        }
        HitRecyclerViewFragment hitListFragment = new HitRecyclerViewFragment();
        hitListFragment.setCampaignId(campaignId);
        replaceCurrentFragment(hitListFragment);
    }


    private void replaceCurrentFragment(Fragment newFragment){
        final FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction()
            .replace(R.id.container, newFragment)
            .commitAllowingStateLoss();

    }
}
