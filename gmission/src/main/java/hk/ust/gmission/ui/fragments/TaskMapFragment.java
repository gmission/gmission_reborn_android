package hk.ust.gmission.ui.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jakewharton.rxbinding.view.RxView;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import hk.ust.gmission.BootstrapServiceProvider;
import hk.ust.gmission.Injector;
import hk.ust.gmission.R;
import hk.ust.gmission.core.Constants;
import hk.ust.gmission.core.api.QueryObject;
import hk.ust.gmission.events.LocationUpdateEvent;
import hk.ust.gmission.events.RequestLocationEvent;
import hk.ust.gmission.events.TaskCreateSuccessEvent;
import hk.ust.gmission.models.Coordinate;
import hk.ust.gmission.models.GeoLocation;
import hk.ust.gmission.models.Hit;
import hk.ust.gmission.models.MapObject;
import hk.ust.gmission.models.ModelWrapper;
import hk.ust.gmission.services.GeoService;
import hk.ust.gmission.services.HitService;
import hk.ust.gmission.ui.activities.AnswerListActivity;
import hk.ust.gmission.ui.activities.AskSpatialTaskActivity;
import hk.ust.gmission.ui.activities.HitActivity;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

import static hk.ust.gmission.core.Constants.Extra.COORDINATE;
import static hk.ust.gmission.core.Constants.Extra.HIT_ID;
import static hk.ust.gmission.core.Constants.Extra.LOCATION_NAME;

/**
 * Created by bigstone on 3/1/2016.
 */
public class TaskMapFragment extends Fragment implements GoogleMap.OnMapLoadedCallback, GoogleMap.OnMapLongClickListener {
    @Bind(R.id.map) MapView mMapView;
    @Bind(R.id.bt_location) ImageButton mMyLocationBtn;
    @Bind(R.id.bt_refresh) ImageButton mRefreshBtn;
    @Bind(R.id.bt_add_task) ImageButton mAddTaskBtn;

    @Inject Bus bus;
    @Inject protected BootstrapServiceProvider serviceProvider;

    private Fragment mFragment;

    private GoogleMap mMap;
    private Geocoder geocoder;
    private Location currentLocation;

    private Marker askHereMarker;

    private boolean isCameraInitialized = false;

    private List<MapObject> mSpatialTasks = new ArrayList<>();
    private HashMap<Marker, MapObject> mMarkerMapObjectHashMap;

    private final int CAMERA_ZOOM_LEVEL = 15;

    private final String MINE_TASK = "MINE TASK";
    private final String OTHERS_TASK = "OTHERS TASK";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragment = this;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.task_map_fragment, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Injector.inject(this);
        ButterKnife.bind(this, view);

        mMapView.onCreate(savedInstanceState);

        Locale locale = Locale.getDefault();
        geocoder = new Geocoder(this.getContext(), Locale.getDefault());
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                initialMap();
            }
        });


        subcribeButtons();
    }

    private void initialMap() {

        mMap.setOnMapLoadedCallback(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setIndoorEnabled(true);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                if (askHereMarker!= null && marker.getId().equals(askHereMarker.getId())) {
                    askPosition(marker.getPosition());
                    return true;
                }
                return false;
            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {//answer spatial task
            @Override
            public void onInfoWindowClick(Marker marker) {
                MapObject mapObject = mMarkerMapObjectHashMap.get(marker);

                if (marker.getTitle().equals(MINE_TASK)) {
                    startActivity(new Intent(getActivity(), AnswerListActivity.class).putExtra(HIT_ID, mapObject.getHit().getId()));
                } else if (marker.getTitle().equals(OTHERS_TASK)){
                    startActivity(new Intent(getActivity(), HitActivity.class).putExtra(HIT_ID, mapObject.getHit().getId()));
                }

            }
        });

        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(false);

//        //make a marker follow the center of the screen
//        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
//            public void onCameraChange(CameraPosition arg0) {
//                mMap.clear();
//                mMap.addMarker(new MarkerOptions().position(arg0.target));
//
//            }
//        });


    }

    private void askPosition(LatLng position){
        List<Address> list = null;
        try {
            list = geocoder.getFromLocation(position.latitude, position.longitude, 1);
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

        Coordinate coordinate = new Coordinate();
        coordinate.setAltitude(0);
        coordinate.setLatitude(position.latitude);
        coordinate.setLongitude(position.longitude);

        Intent intent = new Intent(mFragment.getContext(), AskSpatialTaskActivity.class);
        intent.putExtra(COORDINATE, coordinate);
        intent.putExtra(LOCATION_NAME, locationName);
        startActivity(intent);
    }


    @Override
    public void onMapLongClick(LatLng point) {
        if (askHereMarker != null){
            askHereMarker.remove();
        }
        askHereMarker = mMap.addMarker(new MarkerOptions()
                .position(point)
                .title(getString(R.string.label_ask_here))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));

//        askHereMarker = mMap.addMarker(new MarkerOptions()
//                .position(point)
//                .title(getString(R.string.label_ask_here))
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_3d_model)));
    }

    private void subcribeButtons(){
        RxView.clicks(mMyLocationBtn)
                .doOnNext(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if (currentLocation != null){
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), CAMERA_ZOOM_LEVEL));
                        } else {
                            Toast.makeText(getContext(), getString(R.string.message_still_locating), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .subscribe();

        RxView.clicks(mRefreshBtn)
                .observeOn(Schedulers.io())
                .doOnNext(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        refreshSpaitalTasks();
                    }
                })
                .subscribe();

        RxView.clicks(mAddTaskBtn)
                .doOnNext(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if (currentLocation == null){
                            Toast.makeText(getContext(), getString(R.string.message_current_location_unavailable), Toast.LENGTH_SHORT).show();
                        } else {
                            askPosition(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
                        }
                    }
                })
                .subscribe();
    }


    @Subscribe
    public void onTaskCreatedEvent(TaskCreateSuccessEvent event){
        askHereMarker.remove();
        askHereMarker = null;
        mRefreshBtn.callOnClick();
    }

    private void refreshSpaitalTasks(){
        mSpatialTasks.clear();

        QueryObject queryObject = new QueryObject();
        queryObject.push("status", "eq", "open");
        queryObject.push("campaign_id", "is_null", "");

        final HitService hitService= serviceProvider.getService(this.getActivity()).getHitService();
        final GeoService geoService = serviceProvider.getService(this.getActivity()).getGeoService();


        Observable<ModelWrapper<Hit>> tasksObservable = hitService.getHits(queryObject.toString());

        if (tasksObservable != null){
            tasksObservable
                    .flatMap(new Func1<ModelWrapper<Hit>, Observable<Hit>>() {
                        @Override
                        public Observable<Hit> call(ModelWrapper<Hit> hitModelWrapper) {
                            return Observable.from(hitModelWrapper.getObjects());
                        }
                    })
                    .flatMap(new Func1<Hit, Observable<MapObject>>() {
                        @Override
                        public Observable<MapObject> call(Hit hit) {
                            Observable<GeoLocation> geoLocationObservable = geoService.getGeoLocation(hit.getLocation_id());

                            return Observable.combineLatest(Observable.just(hit), geoLocationObservable, new Func2<Hit, GeoLocation, MapObject>() {
                                @Override
                                public MapObject call(Hit hit, GeoLocation geoLocation) {
                                    MapObject mapObject = new MapObject();
                                    mapObject.setHit(hit);
                                    mapObject.setLocation(geoLocation);
                                    return mapObject;
                                }
                            }).flatMap(new Func1<MapObject, Observable<MapObject>>() {
                                @Override
                                public Observable<MapObject> call(MapObject mapObject) {
                                    Observable<Coordinate> coordinateObservable = geoService.getCoordinate(mapObject.getLocation().getCoordinate_id());
                                    return Observable.combineLatest(Observable.just(mapObject), coordinateObservable, new Func2<MapObject, Coordinate, MapObject>() {
                                        @Override
                                        public MapObject call(MapObject mapObject, Coordinate coordinate) {
                                            mapObject.setCoordinate(coordinate);
                                            return mapObject;
                                        }
                                    });
                                }
                            });
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(new Action1<MapObject>() {
                        @Override
                        public void call(MapObject mapObject) {
                            mSpatialTasks.add(mapObject);
                            refreshMarkers();
                        }
                    })
                    .doOnCompleted(new Action0() {
                        @Override
                        public void call() {
                            if (!isCameraInitialized){
                                updateCamera();
                            }

                        }
                    })
                    .subscribe();
        }

    }

    private void refreshMarkers(){
        mMap.clear();
        mMap.setInfoWindowAdapter(new TaskAdapter());
        mMarkerMapObjectHashMap = new HashMap<>();
        for (MapObject mapObject : mSpatialTasks) {
            MarkerOptions markerOptions = new MarkerOptions();
            if (mapObject.getHit().getRequester_id().equals(Constants.Http.PARAM_USER_ID)){
                markerOptions.title(MINE_TASK);
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            } else {
                markerOptions.title(OTHERS_TASK);
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            }

            markerOptions.position(new LatLng(mapObject.getCoordinate().getLatitude(), mapObject.getCoordinate().getLongitude()));
            mMarkerMapObjectHashMap.put(mMap.addMarker(markerOptions), mapObject);
        }

    }

    class TaskAdapter implements GoogleMap.InfoWindowAdapter {

        @Override
        public View getInfoContents(Marker m) {
            return null;
        }

        @Override
        public View getInfoWindow(Marker m) {
            MapObject mapObject = mMarkerMapObjectHashMap.get(m);
            View infoWindow = View.inflate(mFragment.getContext(), R.layout.task_infowindow_fragment, null);
            infoWindow.setFitsSystemWindows(false);

            TextView content = (TextView) infoWindow.findViewById(R.id.tv_content);
            TextView location = (TextView) infoWindow.findViewById(R.id.tv_locaton);
            TextView time = (TextView) infoWindow.findViewById(R.id.tv_deadline);
            content.setText(mapObject.getHit().getTitle());
            location.setText(mapObject.getLocation().getName());

            time.setText(getString(R.string.label_deadline_time)+ mapObject.getHit().getEnd_time().toLocaleString());

            return infoWindow;
        }
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
        isCameraInitialized = false;
        bus.unregister(this);
        super.onDestroy();
    }

    @Override
    public void onMapLoaded() {
        mRefreshBtn.callOnClick();

    }

    private void updateCamera(){
        isCameraInitialized = true;
        if (currentLocation != null){
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), CAMERA_ZOOM_LEVEL));
        } else if (!mSpatialTasks.isEmpty()) {
            MapObject mapObject = mSpatialTasks.get(0);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mapObject.getCoordinate().getLatLng(), CAMERA_ZOOM_LEVEL));
        }


    }


    @Subscribe
    public void onLocationUpdate(LocationUpdateEvent event){
        currentLocation = event.getLocation();
        if (!isCameraInitialized){
            mMyLocationBtn.callOnClick();
        }
    }

}
