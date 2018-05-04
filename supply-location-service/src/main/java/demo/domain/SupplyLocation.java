package demo.domain;

import lombok.Data;
import org.springframework.data.geo.Point;

@Data
public class SupplyLocation {

    private String id;
    private String address1;
    private String address2;
    private String city;

    private final Point location;
    private String state;
    private String zip;
    private String type;

    public SupplyLocation() {
        this.location = new Point(0,0);
    }

    public SupplyLocation(double longitude, double latitude) {
        this.location = new Point(longitude, latitude);
    }

    public double getLongitude() {
        return location.getX();
    }

    public double getLatitude() {
        return location.getY();
    }
}