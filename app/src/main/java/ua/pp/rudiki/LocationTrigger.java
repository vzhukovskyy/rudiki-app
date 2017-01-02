package ua.pp.rudiki;

import android.graphics.Point;
import android.graphics.PointF;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class LocationTrigger {

    Point pedestrianXingPoint = new Point(50.196465, 30.664731);
    Point junctionPoint = new Point(50.195399, 30.665456);

    Area pedestrianXingArea = new Area(pedestrianXingPoint, junctionPoint);
    Area junctionArea = new Area(junctionPoint, pedestrianXingPoint);

    public void changeLocation(double latitude, double longitude) {
        pedestrianXingArea.changeLocation(latitude, longitude);
        junctionArea.changeLocation(latitude, longitude);
    }

    public boolean isTriggered() {
        return pedestrianXingArea.exited() && junctionArea.entered();
    }

    class Point {
        public double latitude;
        public double longitude;

        public Point(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }

    class Area {
        private Point center;
        private double radius;
        private boolean inAreaAtThisTick = false;
        private boolean inAreaAtPreviousTick = false;

        Area(Point center, Point edge) {
            this.center = center;
            this.radius = distance(center.latitude, center.longitude, edge.latitude, edge.longitude);
        }

        private double distance(double x1, double y1, double x2, double y2) {
            // formula for small distances
            return sqrt(pow(x2 - x1, 2) + pow(y2 - y1, 2));
        }

        private boolean inTriggerArea(double latitude, double longitude) {
            return distance(center.latitude, center.longitude, latitude, longitude) < radius;
        }

        public void changeLocation(double latitude, double longitude) {
            inAreaAtPreviousTick = inAreaAtThisTick;
            inAreaAtThisTick = inTriggerArea(latitude, longitude);
        }

        public boolean entered() {
            return inAreaAtThisTick && !inAreaAtPreviousTick;
        }

        public boolean exited() {
            return !inAreaAtThisTick && inAreaAtPreviousTick;
        }
    }


}
