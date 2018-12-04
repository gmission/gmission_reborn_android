package hk.ust.gmission.ui.fragments;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import hk.ust.gmission.R;
import hk.ust.gmission.events.HitSubmitEnableEvent;
import hk.ust.gmission.util.ImageUtils;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class ImageHitFragment extends BaseAnswerFragment {


    @Bind(R.id.image_capture_btn) ImageButton captureButton;
    @Bind(R.id.image_photopreview_iv) ImageView previewView;

    ImageHitFragment mFragment;
    private File currentPicFile = null;

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static int BUTTON_PRESS_DELAY_MILLIS = 1000;



    public ImageHitFragment() {
        // Required empty public constructor
    }


    public static ImageHitFragment newInstance() {
        ImageHitFragment fragment = new ImageHitFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragment = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.image_hit_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RxView.clicks(captureButton)
                .debounce(BUTTON_PRESS_DELAY_MILLIS, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        ContentValues values = new ContentValues();
                        values.put(MediaStore.Images.Media.TITLE, "gmission_task_image");
                        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                        currentPicFile = ImageUtils.getTempFile(getActivity());

                        if (currentPicFile == null){
                            Toast.makeText(getActivity(), getString(R.string.message_cannot_create_image), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(currentPicFile));
                        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                    }
                })
                .subscribe();

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                Picasso.with(this.getActivity())
                        .load(currentPicFile)
                        .resize(previewView.getWidth(), previewView.getHeight())
                        .centerInside()
                        .into(previewView);
                previewView.setVisibility(View.VISIBLE);
                previewView.bringToFront();
                previewView.invalidate();
                bus.post(new HitSubmitEnableEvent());

            }
        }
    }

    @Override
    public File getImageFile() {
        if (currentPicFile == null){
            return null;
        } else {
            return currentPicFile;
        }
    }
}
