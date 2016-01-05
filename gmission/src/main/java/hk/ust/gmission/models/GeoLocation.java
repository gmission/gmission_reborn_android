package hk.ust.gmission.models;

/**
 * Created by bigstone on 4/1/2016.
 */
public class GeoLocation {
    String id;
    String name;
    String coordinate_id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCoordinate_id() {
        return coordinate_id;
    }

    public void setCoordinate_id(String coordinate_id) {
        this.coordinate_id = coordinate_id;
    }
}
