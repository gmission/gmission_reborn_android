package hk.ust.gmission.ui.activities;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.kevinsawicki.wishlist.Toaster;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.jakewharton.rxbinding.view.RxView;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import hk.ust.gmission.BootstrapServiceProvider;
import hk.ust.gmission.Injector;
import hk.ust.gmission.R;
import hk.ust.gmission.core.Constants;
import hk.ust.gmission.events.LocationUpdateEvent;
import hk.ust.gmission.events.RequestLocationEvent;
import hk.ust.gmission.models.Answer;
import hk.ust.gmission.models.Attachment;
import hk.ust.gmission.models.Coordinate;
import hk.ust.gmission.models.GeoLocation;
import hk.ust.gmission.models.Hit;
import hk.ust.gmission.models.ImageVideoResult;
import hk.ust.gmission.util.ImageUtils;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static hk.ust.gmission.core.Constants.Extra.HIT_ID;

public class SpatialDirectHitActivity extends BootstrapFragmentActivity implements GoogleMap.OnMapLoadedCallback {

    @Bind(R.id.map) MapView mMapView;
    @Bind(R.id.verifying_notification) TextView notificationView;
    @Bind(R.id.image_capture_btn) ImageButton captureButton;
    @Bind(R.id.image_photopreview_iv) ImageView previewView;
    @Bind(R.id.submit_btn) Button submitButton;

    @Inject Bus bus;
    @Inject protected BootstrapServiceProvider serviceProvider;

    SpatialDirectHitActivity mActivity = null;
    Hit mHit = null;

    private GoogleMap mMap;
    private Coordinate taskCoordinate = null;
    private Coordinate currentCoordinate = null;
    private Location latestLocation = null;



    private File currentPicFile = null;
    Answer answer = new Answer();

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private final int CAMERA_ZOOM_LEVEL = 17;
    private static final double DEFAULT_RANGE_METER = 100;
    private static int BUTTON_PRESS_DELAY_MILLIS = 1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spatial_direct_hit_activity);

        Injector.inject(this);
        ButterKnife.bind(this);

        mActivity = this;

        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                initialMap();
            }
        });

        subscribeButtons();

        answer.setBrief("an answer for 3D Reconstruction task");
        answer.setWorker_id(Constants.Http.PARAM_USER_ID);

    }

    @Override
    public void onResume() {
        super.onResume();
        bus.register(this);
        mMapView.onResume();
        bus.post(new RequestLocationEvent(true));
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
        bus.post(new RequestLocationEvent(false));
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        bus.unregister(this);
        super.onDestroy();
    }

    public void downloadHit(){
        if (getIntent() != null && getIntent().getExtras() != null) {
            final String hitId = getIntent().getExtras().getString(HIT_ID);
            serviceProvider.getService(mActivity).getHitService().getHit(hitId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .flatMap(new Func1<Hit, Observable<GeoLocation>>() {
                        @Override
                        public Observable<GeoLocation> call(Hit hit) {
                            mHit = hit;
                            answer.setHit_id(mHit.getId());
                            answer.setType(mHit.getType());
                            return serviceProvider.getService(mActivity).getGeoService().getGeoLocation(hit.getLocation_id());
                        }
                    })
                    .flatMap(new Func1<GeoLocation, Observable<Coordinate>>() {
                        @Override
                        public Observable<Coordinate> call(GeoLocation geoLocation) {
                            return serviceProvider.getService(mActivity).getGeoService().getCoordinate(geoLocation.getCoordinate_id());
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .flatMap(new Func1<Coordinate, Observable<String>>() {
                        @Override
                        public Observable<String> call(Coordinate coordinate) {
                            taskCoordinate = coordinate;
                            LatLng taskLatLng = new LatLng(taskCoordinate.getLatitude(), taskCoordinate.getLongitude());

                            mMap.addMarker(new MarkerOptions()
                                    .position(taskLatLng)
                                    .title(getString(R.string.label_ask_here))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_3d_model)));

                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(taskLatLng, CAMERA_ZOOM_LEVEL));

                            CircleOptions co = new CircleOptions();
                            co.center(taskLatLng);
                            co.radius(DEFAULT_RANGE_METER);
                            co.fillColor(0x2F0000FF);
                            co.strokeColor(0xff38bfc3);
                            co.strokeWidth(8);
                            mMap.addCircle(co);

                            return serviceProvider.getService(mActivity).getExtraService().get3DReconstructionTaskDirection(hitId);
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(new Action1<String>() {
                        @Override
                        public void call(String s) {
                            float nextDirection = Float.valueOf(s);
                            float arrowLeft = nextDirection - 0.25f;
                            float arrowRight = nextDirection + 0.25f;

                            LatLng taskLatLng = new LatLng(taskCoordinate.getLatitude(), taskCoordinate.getLongitude());

                            drawDirectionLine(taskLatLng, nextDirection, 0.001f);
                            drawDirectionLine(taskLatLng, arrowLeft, 0.0005f);
                            drawDirectionLine(taskLatLng, arrowRight, 0.0005f);
                        }
                    })
                    .subscribe();

        }

    }

    private void drawDirectionLine(LatLng taskLocation, float direction, float length){
        double targetLatitude = taskLocation.latitude + Math.sin(direction) * length;
        double targetLongitude = taskLocation.longitude + Math.cos(direction) * length;



        PolylineOptions lineOptions = new PolylineOptions();
        lineOptions.color(Color.RED);
        lineOptions.width(8);
        lineOptions.geodesic(true);
        lineOptions.add(taskLocation).add(new LatLng(targetLatitude, targetLongitude));

        mMap.addPolyline(lineOptions);
    }

    private void initialMap() {
        mMap.setOnMapLoadedCallback(this);
        mMap.setIndoorEnabled(true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    @Override
    public void onMapLoaded() {
        downloadHit();
    }

    @Subscribe
    public void onLocationUpdate(LocationUpdateEvent event){
        currentCoordinate = new Coordinate(event.getLocation().getAltitude(), event.getLocation().getLongitude());

        if (taskCoordinate != null){
            Location taskLocation = new Location(event.getLocation());
            taskLocation.setAltitude(taskCoordinate.getAltitude());
            taskLocation.setLongitude(taskCoordinate.getLongitude());

            double distanceToTask = event.getLocation().distanceTo(new Location(taskLocation));

            if (distanceToTask <= DEFAULT_RANGE_METER) {
                notificationView.setVisibility(View.GONE);
                captureButton.setVisibility(View.VISIBLE);

            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                Picasso.with(mActivity)
                        .load(currentPicFile)
                        .resize(previewView.getWidth(), previewView.getHeight())
                        .centerInside()
                        .into(previewView);
                previewView.setVisibility(View.VISIBLE);
                previewView.bringToFront();
                previewView.invalidate();
                submitButton.setEnabled(true);
            }
        }
    }


    private void subscribeButtons() {
        RxView.clicks(captureButton)
                .debounce(BUTTON_PRESS_DELAY_MILLIS, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        ContentValues values = new ContentValues();
                        values.put(MediaStore.Images.Media.TITLE, "gmission_task_image");
                        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                        currentPicFile = ImageUtils.getTempFile(mActivity);

                        if (currentPicFile == null){
                            Toast.makeText(mActivity, getString(R.string.message_cannot_create_image), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(currentPicFile));
                        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                    }
                })
                .subscribe();



        RxView.clicks(submitButton)
                .debounce(BUTTON_PRESS_DELAY_MILLIS, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<Object, Object>() {
                    @Override
                    public Object call(Object o) {
                        mActivity.showProgress();
                        return o;
                    }
                })
                .observeOn(Schedulers.io())
                .flatMap(new Func1<Object, Observable<ImageVideoResult>>() {
                    @Override
                    public Observable<ImageVideoResult> call(Object stringObservable) {

                        File imageFile = currentPicFile;
                        TypedFile typedFile = new TypedFile("image/jpeg", imageFile);
                        TypedString typedString = new TypedString("file");
                        return serviceProvider.getService(mActivity).getAttachmentService().createImage(typedFile, typedString);
                    }
                })
                .flatMap(new Func1<ImageVideoResult, Observable<Attachment>>() {
                    @Override
                    public Observable<Attachment> call(ImageVideoResult imageVideoResult) {

                        File imageFile = currentPicFile;
                        Attachment attachment = new Attachment();
                        attachment.setType("image");
                        attachment.setName(imageFile.getName());
                        attachment.setValue(imageVideoResult.getFilename());
                        return serviceProvider.getService(mActivity).getAttachmentService().createAttachment(attachment);
                    }
                })
                .flatMap(new Func1<Attachment, Observable<String>>() {
                    @Override
                    public Observable<String> call(Attachment attachment) {
                        answer.setAttachment_id(attachment.getId());
                        return Observable.just("dummy");
                    }
                })
                .flatMap(new Func1<String, Observable<Coordinate>>() {
                    @Override
                    public Observable<Coordinate> call(String o) {
                        return serviceProvider.getService(mActivity).getGeoService().createCoordinate(currentCoordinate);
                    }
                })
                .flatMap(new Func1<Coordinate, Observable<GeoLocation>>() {
                    @Override
                    public Observable<GeoLocation> call(Coordinate coordinate) {
                        GeoLocation geoLocation = new GeoLocation();
                        geoLocation.setCoordinate_id(coordinate.getId());
                        geoLocation.setName("dummy");

                        return serviceProvider.getService(mActivity).getGeoService().createGeoLocation(geoLocation);
                    }
                })
                .flatMap(new Func1<GeoLocation, Observable<Answer>>() {
                    @Override
                    public Observable<Answer> call(GeoLocation geoLocation) {
                        answer.setLocation_id(geoLocation.getId());
                        return serviceProvider.getService(mActivity).getAnswerService().postAnswer(answer);
                    }
                })
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<Answer>() {
                    @Override
                    public void call(Answer answer) {
                        Toaster.showShort(mActivity, mActivity.getString(R.string.message_answer_success));
                        mActivity.hideProgress();
                        finish();
                    }
                })
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable e) {
                        Throwable cause = e.getCause() != null ? e.getCause() : e;
                        String message;
                        message = cause.getMessage();

                        Toaster.showLong(SpatialDirectHitActivity.this, message);

                    }
                })
                .subscribe();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;
    }

}
