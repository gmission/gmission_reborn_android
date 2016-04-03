package hk.ust.gmission.ui.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.kevinsawicki.wishlist.Toaster;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxSeekBar;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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
import hk.ust.gmission.events.TaskCreateSuccessEvent;
import hk.ust.gmission.models.Coordinate;
import hk.ust.gmission.models.GeoLocation;
import hk.ust.gmission.models.Hit;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

import static hk.ust.gmission.core.Constants.Extra.CAMPAIGN_ID;
import static hk.ust.gmission.core.Constants.Extra.COORDINATE;
import static hk.ust.gmission.core.Constants.Extra.HIT_ID;
import static hk.ust.gmission.core.Constants.Extra.LOCATION_NAME;

public class Create3DHitActivity extends BootstrapFragmentActivity implements GoogleMap.OnMapLoadedCallback {
    private static int TEXT_CHECK__DELAY_MILLIS = 1000;
    private static int BUTTON_PRESS_DELAY_MILLIS = 1000;

    @Bind(R.id.map) MapView mMapView;
    @Bind(R.id.et_content) EditText mEtContent;
    @Bind(R.id.et_title) EditText mEtTitle;
    @Bind(R.id.submit_btn) Button submitButton;

    @Inject Bus bus;
    @Inject protected BootstrapServiceProvider serviceProvider;

    private GoogleMap mMap;
    private Marker askHereMarker;
    private Create3DHitActivity mActivity;
    private boolean hasInitialMap = false;
    private boolean hasInitialLocation = false;

    private String campaignId = null;

    private final int CAMERA_ZOOM_LEVEL = 17;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_3d_hit_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

        if (getIntent() != null && getIntent().getExtras() != null) {
            campaignId = getIntent().getExtras().getString(CAMPAIGN_ID);
        }

        subcribeViews();
        subcribeSubmitButton();

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

        //make a marker follow the center of the screen
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            public void onCameraChange(CameraPosition arg0) {
                mMap.clear();
                askHereMarker = mMap.addMarker(new MarkerOptions().position(arg0.target));

            }
        });
    }


    @Override
    public void onMapLoaded() {
        hasInitialMap = true;
    }

    @Subscribe
    public void onLocationUpdate(LocationUpdateEvent event){
        if (!hasInitialMap) return;
        if (hasInitialLocation) {
            bus.post(new RequestLocationEvent(false));
            return;
        }

        LatLng taskLatLng = new LatLng(event.getLocation().getLatitude(), event.getLocation().getLongitude());

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(taskLatLng, CAMERA_ZOOM_LEVEL));
        hasInitialLocation = true;

    }


    public void subcribeSubmitButton(){
        RxView.clicks(submitButton)
                .debounce(BUTTON_PRESS_DELAY_MILLIS, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<Object, Observable<LatLng>>() {
                    @Override
                    public Observable<LatLng> call(Object o) {
                        mActivity.showProgress();
                        LatLng askHereLocation = askHereMarker.getPosition();
                        return Observable.just(askHereLocation);
                    }
                })
                .observeOn(Schedulers.io())
                .flatMap(new Func1<LatLng, Observable<Coordinate>>() {
                    @Override
                    public Observable<Coordinate> call(LatLng askHereLocation) {
                        Coordinate coordinate = new Coordinate();

                        coordinate.setLatitude(askHereLocation.latitude);
                        coordinate.setLongitude(askHereLocation.longitude);

                        return serviceProvider.getService().getGeoService().createCoordinate(coordinate);
                    }
                })
                .flatMap(new Func1<Coordinate, Observable<GeoLocation>>() {
                    @Override
                    public Observable<GeoLocation> call(Coordinate coordinate) {
                        GeoLocation geoLocation = new GeoLocation();
                        geoLocation.setCoordinate_id(coordinate.getId());
                        Geocoder geocoder = new Geocoder(mActivity, Locale.getDefault());
                        List<Address> list = null;
                        try {
                            list = geocoder.getFromLocation(coordinate.getLatitude(), coordinate.getLongitude(), 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        String locationName = null;
                        if (list != null && list.size() > 0) {
                            Address address = list.get(0);
                            locationName = address.getLocality()+ " " + address.getThoroughfare() + " " + address.getFeatureName();
                            locationName = locationName.replace("null", "");
                            locationName = locationName.trim();
                        }

                        geoLocation.setName(locationName);

                        return serviceProvider.getService().getGeoService().createGeoLocation(geoLocation);
                    }
                })
                .flatMap(new Func1<GeoLocation, Observable<Hit>>() {
                    @Override
                    public Observable<Hit> call(GeoLocation geoLocation) {
                        Hit hit = new Hit();

                        hit.setRequester_id(Constants.Http.PARAM_USER_ID);
                        hit.setLocation_id(geoLocation.getId());
                        hit.setTitle(mEtTitle.getText().toString());
                        hit.setDescription(mEtContent.getText().toString());
                        hit.setStatus("open");
                        hit.setType("3d");
                        hit.setCredit(50);
                        hit.setCampaign_id(campaignId);
                        hit.setRequired_answer_count(50);
                        hit.setEnd_time(new Date(Calendar.getInstance().getTime().getTime() + (10 * 24 * 60 * 60 * 1000)));

                        return serviceProvider.getService().getHitService().createHit(hit);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<Hit>() {
                    @Override
                    public void call(Hit hit) {
                        Toaster.showShort(mActivity, mActivity.getString(R.string.message_ask_question_success));
                        bus.post(new TaskCreateSuccessEvent());
                        mActivity.hideProgress();
                        finish();
                    }
                })
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                        mActivity.hideProgress();
                    }
                })
                .subscribe();
    }

    public void subcribeViews(){

        Observable<CharSequence> titleObserver = RxTextView.textChanges(mEtTitle).debounce(TEXT_CHECK__DELAY_MILLIS, TimeUnit.MILLISECONDS);
        Observable<CharSequence> contentObserver = RxTextView.textChanges(mEtContent).debounce(TEXT_CHECK__DELAY_MILLIS, TimeUnit.MILLISECONDS);

        Observable.combineLatest(titleObserver, contentObserver, new Func2<CharSequence, CharSequence, Boolean>() {
                    @Override
                    public Boolean call(CharSequence title, CharSequence content) {
                        return title.length() >= 5 && content.length() >= 5;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean enableButten) {
                        if (enableButten){
                            submitButton.setEnabled(true);
                        }
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
