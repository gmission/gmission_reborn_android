

package hk.ust.gmission.ui.adapters;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import hk.ust.gmission.R;
import hk.ust.gmission.ui.fragments.CampaignRecyclerViewFragment;
import hk.ust.gmission.ui.fragments.HitRecyclerViewFragment;
import hk.ust.gmission.ui.fragments.MessageRecyclerViewFragment;

/**
 * Pager adapter
 */
public class BootstrapPagerAdapter extends FragmentPagerAdapter {

    private final Resources resources;

    /**
     * Create pager adapter
     *
     * @param resources
     * @param fragmentManager
     */
    public BootstrapPagerAdapter(final Resources resources, final FragmentManager fragmentManager) {
        super(fragmentManager);
        this.resources = resources;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Fragment getItem(final int position) {
        final Fragment result;
        switch (position) {
            case 0:
                result = new CampaignRecyclerViewFragment();
                break;
            case 1:
                result = new HitRecyclerViewFragment();
                break;
            case 2:
                result = new MessageRecyclerViewFragment();
                break;
            default:
                result = null;
                break;
        }
        if (result != null) {
            result.setArguments(new Bundle()); //TODO do we need this?
        }
        return result;
    }

    @Override
    public CharSequence getPageTitle(final int position) {
        switch (position) {
            case 0:
                return resources.getString(R.string.page_news);
            case 1:
                return resources.getString(R.string.page_users);
            case 2:
                return resources.getString(R.string.page_checkins);
            default:
                return null;
        }
    }
}
