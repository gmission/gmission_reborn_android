package hk.ust.gmission.ui.activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import hk.ust.gmission.R;
import hk.ust.gmission.ui.view.GLView;

public class MeshViewActivity extends BootstrapFragmentActivity {
    GLView view;

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.ply_activity);
        view = (GLView)findViewById(R.id.gl_view);
        view.setNewRenderer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        view.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        view.onResume();
    }

    @Override
    public void onConfigurationChanged(Configuration conf) {
        super.onConfigurationChanged(conf);
    }

    public void Reset(View v) {
    view.Reset();
  }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;

    }
}
