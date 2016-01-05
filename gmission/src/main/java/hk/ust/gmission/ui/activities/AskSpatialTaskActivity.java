package hk.ust.gmission.ui.activities;

import android.os.Bundle;
import android.view.MenuItem;

import hk.ust.gmission.R;

public class AskSpatialTaskActivity extends BootstrapFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ask_spatial_task_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;

    }

}
