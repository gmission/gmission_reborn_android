package hk.ust.gmission.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import hk.ust.gmission.R;
import hk.ust.gmission.models.Hit;

import static hk.ust.gmission.core.Constants.Extra.HIT_ID;

public class HitSummaryActivity extends BootstrapFragmentActivity {

    private Hit mHit = null;
    private HitActivity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hit_summary_activity);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.hit_summary_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getTitle().equals(getString(R.string.title_activity_answer_list))){
            startActivity(new Intent(this, AnswerListActivity.class).putExtra(HIT_ID, mHit.getId()));
        } else {
            finish();
        }

        return true;

    }
}
