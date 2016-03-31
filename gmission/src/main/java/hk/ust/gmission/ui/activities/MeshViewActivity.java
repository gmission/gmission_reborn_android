package hk.ust.gmission.ui.activities;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.kevinsawicki.wishlist.Toaster;

import butterknife.Bind;
import hk.ust.gmission.R;
import hk.ust.gmission.core.Constants;
import hk.ust.gmission.models.Attachment;
import hk.ust.gmission.models.Hit;
import hk.ust.gmission.ui.view.GLView;
import hk.ust.gmission.util.DownloadUtils;
import hk.ust.gmission.util.PlyUtils;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static hk.ust.gmission.core.Constants.Extra.HIT_ID;

public class MeshViewActivity extends BootstrapFragmentActivity {
    MeshViewActivity mActivity;
    Hit mHit;

    @Bind(R.id.loading_notification) TextView loadingNotificationText;

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.ply_activity);
        mActivity = this;

        downloadPly();
    }

    private void addGLView(String plyFilePath){
        LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = vi.inflate(R.layout.gl_view_layout, null);
        GLView glView = (GLView) v.findViewById(R.id.gl_view);

        glView.setNewRenderer(plyFilePath);

        ViewGroup insertPoint = (ViewGroup) findViewById(R.id.item_wrapper);
        insertPoint.addView(v, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private void downloadPly(){
        if (getIntent() != null && getIntent().getExtras() != null) {
            String hitId = getIntent().getExtras().getString(HIT_ID);
            serviceProvider.getService().getHitService().getHit(hitId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .flatMap(new Func1<Hit, Observable<Attachment>>() {
                        @Override
                        public Observable<Attachment> call(Hit hit) {
                            mHit = hit;
                            return serviceProvider.getService().getAttachmentService()
                                    .getAttachment(hit.getAttachment_id()); // if no attachment, the flow will stop here
                        }
                    })
                    .observeOn(Schedulers.io())
                    .flatMap(new Func1<Attachment, Observable<String>>() {
                        @Override
                        public Observable<String> call(Attachment attachment) {
                            if (attachment.getType().equals("3d")) {
                                try {
                                    String fileUrl = Constants.Http.URL_BASE + Constants.Http.URL_PLY + "/" +mHit.getId() + "/"+ attachment.getValue();

                                    String plyFileName = mHit.getId() +"_"+ attachment.getValue();
                                    String plyFilePath = PlyUtils.getPlyDir(mActivity) + "/" + plyFileName;

                                    boolean result = DownloadUtils.downloadFileFromIO(fileUrl, plyFilePath);
                                    Toaster.showLong(MeshViewActivity.this, "Finish Downloading PLY file");
                                    if (result){
                                        return Observable.just(plyFilePath);
                                    } else {
                                        return Observable.just("null");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            return null;
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(new Action1<String>() {
                        @Override
                        public void call(String plyFilePath) {
                            if (!plyFilePath.equals("null")){
                                addGLView(plyFilePath);
                            }
                        }
                    })
                    .doOnCompleted(new Action0() {
                        @Override
                        public void call() {
                            loadingNotificationText.setVisibility(View.INVISIBLE);
                        }
                    })
                    .subscribe();
        }
    }




    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onConfigurationChanged(Configuration conf) {
        super.onConfigurationChanged(conf);
    }

    public void Reset(View v) {
  }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;

    }
}
