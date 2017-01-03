package ua.pp.rudiki.geotrigger;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class AreaTrigger {

    private GeoPoint center;
    private double radius;
    private boolean inAreaAtThisTick = false;
    private boolean inAreaAtPreviousTick = false;

    public AreaTrigger(GeoPoint center, double radius) {
        this.center = center;
        this.radius = radius;
    }

    private boolean inTriggerArea(GeoPoint p) {
        return center.distanceTo(p) < radius;
    }

    public void changeLocation(double latitude, double longitude) {
        changeLocation(new GeoPoint(latitude, longitude));
    }

    public void changeLocation(GeoPoint p) {
        inAreaAtPreviousTick = inAreaAtThisTick;
        inAreaAtThisTick = inTriggerArea(p);
    }

    public boolean entered() {
        return inAreaAtThisTick && !inAreaAtPreviousTick;
    }

    public boolean exited() {
        return !inAreaAtThisTick && inAreaAtPreviousTick;
    }

    public boolean inside() {
        return inAreaAtThisTick;
    }
}

