package hk.ust.gmission.models;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class Coordinate implements Serializable{

    private String id;
    private double latitude;
    private double longitude;
    private double altitude;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(final double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(final double longitude) {
        this.longitude = longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }


    public LatLng getLatLng(){
        return new LatLng(latitude, longitude);
    }
}
