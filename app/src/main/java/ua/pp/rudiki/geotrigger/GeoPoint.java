package ua.pp.rudiki.geotrigger;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class GeoPoint {
    public double latitude;
    public double longitude;

    public GeoPoint(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double distanceTo(GeoPoint p) {
        // Pythagorean formula works for small distances
        return sqrt(pow(p.latitude - latitude, 2) + pow(p.longitude - longitude, 2));
    }
}