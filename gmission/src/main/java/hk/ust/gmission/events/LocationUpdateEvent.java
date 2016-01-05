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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
