package hk.ust.gmission.events;


import android.location.Location;

/**
 * Created by bigstone on 29/12/2015.
 */
public class LocationUpdateEvent {
    Location location;

    public LocationUpdateEvent(Location location) {
        this.location = location;
    }
}
