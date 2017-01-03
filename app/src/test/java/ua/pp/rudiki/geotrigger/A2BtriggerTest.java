package ua.pp.rudiki.geotrigger;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.junit.Assert.*;

public class A2BTriggerTest {

    @Test
    public void testInitialOutside() {
        A2BTrigger a2bTrigger = new A2BTrigger(new GeoPoint(10,10), new GeoPoint(10,11));
        a2bTrigger.changeLocation(10,7);
        assertFalse(a2bTrigger.isTriggered());
    }

    @Test
    public void testInitialInsideA() {
        A2BTrigger a2bTrigger = new A2BTrigger(new GeoPoint(10,10), new GeoPoint(10,11));
        a2bTrigger.changeLocation(10,9.5);
        assertFalse(a2bTrigger.isTriggered());
    }

    @Test
    public void testInitialInsideB() {
        A2BTrigger a2bTrigger = new A2BTrigger(new GeoPoint(10,10), new GeoPoint(10,11));
        a2bTrigger.changeLocation(10,11.5);
        assertFalse(a2bTrigger.isTriggered());
    }

    @Test
    public void testOut2Out() {
        A2BTrigger a2bTrigger = new A2BTrigger(new GeoPoint(10,10), new GeoPoint(10,11));
        a2bTrigger.changeLocation(10,7);
        a2bTrigger.changeLocation(10,6);
        assertFalse(a2bTrigger.isTriggered());
    }

    @Test
    public void testOut2A() {
        A2BTrigger a2bTrigger = new A2BTrigger(new GeoPoint(10,10), new GeoPoint(10,11));
        a2bTrigger.changeLocation(10,7);
        a2bTrigger.changeLocation(10,9.5);
        assertFalse(a2bTrigger.isTriggered());
    }

    @Test
    public void testOut2B() {
        A2BTrigger a2bTrigger = new A2BTrigger(new GeoPoint(10,10), new GeoPoint(10,11));
        a2bTrigger.changeLocation(10,7);
        a2bTrigger.changeLocation(10,11.5);
        assertFalse(a2bTrigger.isTriggered());
    }

    @Test
    public void testA2Out() {
        A2BTrigger a2bTrigger = new A2BTrigger(new GeoPoint(10,10), new GeoPoint(10,11));
        a2bTrigger.changeLocation(10,9.5);
        a2bTrigger.changeLocation(10,7);
        assertFalse(a2bTrigger.isTriggered());
    }

    @Test
    public void testA2A() {
        A2BTrigger a2bTrigger = new A2BTrigger(new GeoPoint(10,10), new GeoPoint(10,11));
        a2bTrigger.changeLocation(10,9.5);
        a2bTrigger.changeLocation(10,9.6);
        assertFalse(a2bTrigger.isTriggered());
    }

    @Test
    public void testA2B() {
        A2BTrigger a2bTrigger = new A2BTrigger(new GeoPoint(10,10), new GeoPoint(10,11));
        a2bTrigger.changeLocation(10,9.5);
        a2bTrigger.changeLocation(10,11.5);
        assertTrue(a2bTrigger.isTriggered());
    }

    @Test
    public void testB2Out() {
        A2BTrigger a2bTrigger = new A2BTrigger(new GeoPoint(10,10), new GeoPoint(10,11));
        a2bTrigger.changeLocation(10,11.5);
        a2bTrigger.changeLocation(10,13);
        assertFalse(a2bTrigger.isTriggered());
    }

    @Test
    public void testB2A() {
        A2BTrigger a2bTrigger = new A2BTrigger(new GeoPoint(10,10), new GeoPoint(10,11));
        a2bTrigger.changeLocation(10,11.5);
        a2bTrigger.changeLocation(10,9.5);
        assertFalse(a2bTrigger.isTriggered());
    }

    @Test
    public void testB2B() {
        A2BTrigger a2bTrigger = new A2BTrigger(new GeoPoint(10,10), new GeoPoint(10,11));
        a2bTrigger.changeLocation(10,11.5);
        a2bTrigger.changeLocation(10,11.6);
        assertFalse(a2bTrigger.isTriggered());
    }

    //@Test
    public void checkWhenTriggered() throws Exception {
        GeoPoint pedestrianXingPoint = new GeoPoint(50.196465, 30.664731);
        GeoPoint junctionPoint = new GeoPoint(50.195399, 30.665456);
        A2BTrigger a2bTrigger = new A2BTrigger(pedestrianXingPoint, junctionPoint);
        //A2BTrigger a2bTrigger = new A2BTrigger(junctionPoint, pedestrianXingPoint);

        InputStream is = this.getClass().getClassLoader().getResourceAsStream("rudiki-gps-log.txt");
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String str;

            if (is != null) {
                while ((str = reader.readLine()) != null) {

                    String[] parts = str.split("\\s+");
                    if(parts.length == 4) {
                        String date = parts[0];
                        String time = parts[1];
                        double latitude = Double.parseDouble(parts[2]);
                        double longitude = Double.parseDouble(parts[3]);

                        a2bTrigger.changeLocation(latitude, longitude);
                        if(a2bTrigger.isTriggered()) {
                            System.out.println("triggered at "+date+" "+time+" "+latitude+" "+longitude);
                        }
                    }
                }
            }
        } finally {
            try { is.close(); } catch (Throwable ignore) {}
        }
    }


}