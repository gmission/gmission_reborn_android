package hk.ust.gmission.ui.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;

import com.github.kevinsawicki.wishlist.Toaster;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import hk.ust.gmission.R;
import hk.ust.gmission.core.Constants;
import hk.ust.gmission.models.WorkingRegion;


public class WorkingAreaSetupActivity extends BootstrapFragmentActivity implements GoogleMap.OnMarkerDragListener, GoogleMap.OnMyLocationChangeListener {

    private GoogleMap mMap;
    private static String TAG = "WorkingAreaActivity";

    private Location currentLocation = null;
    private double circleDistance = 0;
    private double fanshapeDistance = 0;

    private Marker nw;
    private Marker sw;
    private Marker se;
    private Marker ne;
    private Polyline polyline;

    private Marker rangeMarker;
    private Circle rangeCircle;
    private Marker minAngleMarker;
    private Marker maxAngleMarker;
    private Polyline minPolyline;
    private Polyline maxPolyline;
    private Polyline arcPolyline;

    private boolean isMarkerAdded = false;
    private Location baseLocation = null;

    // Loading dialog
    private ProgressDialog dialog;

    // Button for task inquiries
    private Button buttonTaskInquiry;

    private CheckBox fanshapeCKB;

    /**
     * Keep track of the task inquiry task to ensure we can cancel it if
     * requested.
     */
//    private TaskInquiryTask mTaskInquiryTask = null;

    private static final double EarthRadiusMeters = 6371000; // meters
    private double adjustBearing = 0;
    private static final double DEFAULT_RANGE_METER = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.working_area_activity);

        fanshapeCKB = (CheckBox) findViewById(R.id.fanshapecheckBox);
        ImageButton mBtConfirm = (ImageButton) findViewById(R.id.bt_confirm);
        setUpMapIfNeeded();

        mBtConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toaster.showLong(WorkingAreaSetupActivity.this, "Workering Area Has Updated!");
                WorkingRegion wr = new WorkingRegion();
                wr.setWorker_id(Integer.valueOf(Constants.Http.PARAM_USER_ID));
                wr.setLatitude(currentLocation.getLatitude());
                wr.setLongitude(currentLocation.getLongitude());
                if (fanshapeCKB.isChecked()) {
                    wr.setRange(fanshapeDistance);
                } else {
                    wr.setRange(circleDistance);
                    wr.setMin_angle(0);
                    wr.setMax_angle(6.3);
                    wr.setCreated_on(new Date());
                    wr.setComments("null");
                }
               //TODO: save working area to server.

                finish();

            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }


    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the
        // map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }


    private void setUpMap() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMarkerDragListener(this);
        mMap.setOnMyLocationChangeListener(this);


        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

    }


    private void dismissDialog() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    public void onCheckFanShape(View v){

        if(fanshapeCKB.isChecked()) {
            rangeMarker.setVisible(false);
            rangeCircle.setVisible(false);

            minAngleMarker.setVisible(true);
            minPolyline.setVisible(true);
            maxAngleMarker.setVisible(true);
            maxPolyline.setVisible(true);
            arcPolyline.setVisible(true);
        } else {
            rangeMarker.setVisible(true);
            rangeCircle.setVisible(true);

            minAngleMarker.setVisible(false);
            minPolyline.setVisible(false);
            maxAngleMarker.setVisible(false);
            maxPolyline.setVisible(false);
            arcPolyline.setVisible(false);
        }
    }


    private void addMarkersToMap(Location location) {
        if (isMarkerAdded){
            return;
        }

        baseLocation = location;

        LatLng locationLatLng = new LatLng(location.getLatitude(),location.getLongitude());

        Location rangeLocation = new Location(location);
        rangeLocation.setLongitude(rangeLocation.getLongitude() + 0.001);
        double rangeBearing = location.bearingTo(rangeLocation);
        LatLng rangeMarkerLatLng = forwardDistance(locationLatLng, rangeBearing, DEFAULT_RANGE_METER);

        rangeMarker = mMap.addMarker(new MarkerOptions().position(rangeMarkerLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.edit_rect)).anchor(0.5f, 0.5f).draggable(true));


        CircleOptions co = new CircleOptions();
        co.center(locationLatLng);
        co.radius(DEFAULT_RANGE_METER);
        co.fillColor(0x2F0000FF);
        co.strokeColor(0xff38bfc3);
        co.strokeWidth(8);
        rangeCircle = mMap.addCircle(co);



        maxAngleMarker = mMap.addMarker(new MarkerOptions().position(rangeMarkerLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.edit_rect)).anchor(0.5f, 0.5f).draggable(true));
        rangeLocation.setLongitude(baseLocation.getLongitude());
        rangeLocation.setLatitude(rangeLocation.getLatitude() + 0.001);

        double bearing1 = rangeBearing;
        double bearing2 = location.bearingTo(rangeLocation);
        LatLng maxAngleLatLng = forwardDistance(locationLatLng, bearing2, DEFAULT_RANGE_METER);
        minAngleMarker = mMap.addMarker(new MarkerOptions().position(maxAngleLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.edit_rect)).anchor(0.5f, 0.5f).draggable(true));

        PolylineOptions minAngleOptions = new PolylineOptions();
        minAngleOptions.color(0xff38bfc3);
        minAngleOptions.width(8);
        minAngleOptions.geodesic(true);
        minAngleOptions.add(locationLatLng).add(minAngleMarker.getPosition());
        minPolyline = mMap.addPolyline(minAngleOptions);

        PolylineOptions maxAngleOptions = new PolylineOptions();
        maxAngleOptions.color(0xff38bfc3);
        maxAngleOptions.width(8);
        maxAngleOptions.geodesic(true);
        maxAngleOptions.add(locationLatLng).add(maxAngleMarker.getPosition());
        maxPolyline = mMap.addPolyline(maxAngleOptions);


        PolylineOptions arcOptions = new PolylineOptions();
        arcOptions.color(0xff38bfc3);
        arcOptions.width(8);
        arcOptions.geodesic(true);

        List<LatLng> arcPoints = null;
        if (bearing1 < bearing2){
            arcPoints = prepareArcPoints(locationLatLng, bearing1, bearing2, DEFAULT_RANGE_METER);
        } else {
            arcPoints = prepareArcPoints(locationLatLng, bearing2, bearing1, DEFAULT_RANGE_METER);
        }
        arcOptions.addAll(arcPoints);
        arcPolyline = mMap.addPolyline(arcOptions);


        onCheckFanShape(null);

        mMap.moveCamera(CameraUpdateFactory.zoomOut());
        isMarkerAdded = true;
    }


    private void updateRange(Marker marker) {

        LatLng markerLatLng = marker.getPosition();

        if (marker.equals(rangeMarker)){
            rangeMarker.setPosition(new LatLng(markerLatLng.latitude, markerLatLng.longitude));
            Location rangeLocation = new Location(baseLocation);
            rangeLocation.setLongitude(markerLatLng.longitude);
            rangeLocation.setLatitude(markerLatLng.latitude);
            circleDistance = rangeLocation.distanceTo(baseLocation);
            rangeCircle.setRadius(circleDistance);
        } else {

            Marker dragMarker = null;
            Marker adjustMarker = null;

            Location minAngleLocation = covertToLocation(minAngleMarker.getPosition());
            Location maxAngleLocation = covertToLocation(maxAngleMarker.getPosition());

            Location dragLocation = null;

            if (marker.equals(minAngleMarker)) {
                dragMarker = minAngleMarker;
                adjustMarker = maxAngleMarker;
                dragLocation = minAngleLocation;
            } else if (marker.equals(maxAngleMarker)) {
                dragMarker = maxAngleMarker;
                adjustMarker = minAngleMarker;
                dragLocation = maxAngleLocation;
            } else {
                return;
            }

            dragMarker.setPosition(new LatLng(markerLatLng.latitude, markerLatLng.longitude));



            double dragMarkerDistance = baseLocation.distanceTo(dragLocation);
            fanshapeDistance = dragMarkerDistance;

            LatLng baseLatLng = new LatLng(baseLocation.getLatitude(),baseLocation.getLongitude());
            LatLng adjustLatLng = forwardDistance(baseLatLng, adjustBearing, dragMarkerDistance);


            adjustMarker.setPosition(adjustLatLng);

            List<LatLng> minPoints = new LinkedList<LatLng>();
            minPoints.add(baseLatLng);
            minPoints.add(minAngleMarker.getPosition());
            minPolyline.setPoints(minPoints);

            List<LatLng> maxPoints = new LinkedList<LatLng>();
            maxPoints.add(baseLatLng);
            maxPoints.add(maxAngleMarker.getPosition());
            maxPolyline.setPoints(maxPoints);

            List<LatLng> arcPoints = null;
            double minBearing = baseLocation.bearingTo(minAngleLocation);
            double maxBearing = baseLocation.bearingTo(maxAngleLocation);
            arcPoints = prepareArcPoints(baseLatLng, minBearing, maxBearing, dragMarkerDistance);
            arcPolyline.setPoints(arcPoints);
          }
    }

    private Location covertToLocation(LatLng position){
        Location dragLocation = new Location(baseLocation);
        dragLocation.setLongitude(position.longitude);
        dragLocation.setLatitude(position.latitude);
        return  dragLocation;
    }

    private List<LatLng> prepareArcPoints(LatLng baseLatLng, double minBearing, double maxBearing, double distance){
        List<LatLng> points = new LinkedList<LatLng>();

        if (minBearing > maxBearing){
            maxBearing += 360;
        }
        double cBearing = minBearing;
        while (cBearing <= maxBearing){
            points.add(forwardDistance(baseLatLng, cBearing, distance));
            cBearing += 1;
        }

        return points;
    }

    private LatLng forwardDistance(LatLng baseLatLng, double bearing, double distance){
        double R = EarthRadiusMeters;
        double bearingRad = Math.toRadians(bearing);
        double lat1 = Math.toRadians(baseLatLng.latitude);
        double lon1 = Math.toRadians(baseLatLng.longitude);
        double lat2 = Math.asin( Math.sin(lat1)* Math.cos(distance/R) + Math.cos(lat1)* Math.sin(distance/R)* Math.cos(bearingRad) );
        double lon2 = lon1 + Math.atan2(Math.sin(bearingRad)* Math.sin(distance/ R)* Math.cos(lat1), Math.cos(distance/R)- Math.sin(lat1)* Math.sin(lat2));

        return new LatLng(Math.toDegrees(lat2), Math.toDegrees(lon2));
    }



    @Override
    public void onMarkerDrag(Marker marker) {
        updateRange(marker);
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        updateRange(marker);
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

        Marker adjustMarker = null;
        if(!marker.equals(rangeMarker)){
            if (marker.equals(minAngleMarker)){
                adjustMarker = maxAngleMarker;
            } else {
                adjustMarker = minAngleMarker;
            }

            Location adjustLocation = new Location(baseLocation);
            adjustLocation.setLatitude(adjustMarker.getPosition().latitude);
            adjustLocation.setLongitude(adjustMarker.getPosition().longitude);

            adjustBearing = baseLocation.bearingTo(adjustLocation);
        }

        updateRange(marker);
    }

    @Override
    public void onMyLocationChange(final Location location) {
        if (location != null && currentLocation == null) {

            CameraPosition camera = new CameraPosition.Builder().target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(18.5f).bearing(0).tilt(20).build();

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera), new GoogleMap.CancelableCallback() {
                @Override
                public void onFinish() {
                    addMarkersToMap(location);
                }

                @Override
                public void onCancel() {

                }
            });
        }
        currentLocation = location;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;

    }

}
