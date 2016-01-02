package hk.ust.gmission.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.IBinder;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import hk.ust.gmission.Injector;
import hk.ust.gmission.events.LocationUpdateEvent;
import hk.ust.gmission.events.RequestLocationEvent;
import hk.ust.gmission.util.Ln;

public class LocationTraceService extends Service implements GoogleApiClient.OnConnectionFailedListener{
    @Inject protected Bus bus;

    private final IBinder binder = new LocationTraceBinder();

    //google
    LocationManager locationManager;
    private boolean isTowerEnabled = false;
    private boolean isGpsEnabled = false;


    private static final int GPS_SCAN_SPAN = 10*1000;

    private LocationRequest mLocationRequest;
    private static final long POLLING_FREQ = 1000 * 5;
    private static final long FASTEST_UPDATE_FREQ = 1000 * 5;


    private GoogleApiClient client=null;

    private static final int CHECKSTATUS_TIME_SPAN = 1000;

    private void CheckTowerAndGpsStatus() {
        locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        isTowerEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }


    @Override
    public IBinder onBind(Intent arg0) {
        return binder;
    }

    @Override
    public void onCreate() {
        Injector.inject(this);
//        CheckTowerAndGpsStatus();

        initGPSLocalization();
        startLocating();

        bus.register(this);

    }

    @Override
    public void onDestroy() {
        Ln.d("GpsLoggingService is being destroyed by Android OS.");

        client.disconnect();
        bus.unregister(this);
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        Ln.d("Android is low on memory.");
        super.onLowMemory();
    }



    @Subscribe
    public void onLocationRequest(RequestLocationEvent event){
        Ln.d("Start Locating");
        startLocating();
    }

    public void initGPSLocalization(){
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_FREQ);


        client=new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addOnConnectionFailedListener(this)
                .build();

        client.connect();
    }

    public void startLocating() {
        if (!client.isConnected()){
            client.connect();
        }

        if (client.isConnected()){
            LocationServices.FusedLocationApi.requestLocationUpdates(client,mLocationRequest,new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    Ln.d(location.toString());
                    bus.post(new LocationUpdateEvent(location));
                    stopLocating();
                }
            });
        }
    }

    /**
     * Stops locating, removes notification, stops GPS manager
     */
    public void stopLocating() {
        Ln.d("GpsLoggingService.StopLocating");
        stopForeground(true);
        if (client.isConnected()){
            client.disconnect();
        }
    }

    void RestartGpsManagers() {
        Ln.d("GpsLoggingService.RestartGpsManagers");
        stopLocating();
        startLocating();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public class LocationTraceBinder extends Binder {
        public LocationTraceService getService() {
            return LocationTraceService.this;
        }
    }

}