package ua.pp.rudiki.geotrigger;


import org.junit.Test;

import static org.junit.Assert.*;

public class AreaTriggerTest {

    @Test
    public void testAreaInitiallyOut() {
        AreaTrigger area = new AreaTrigger(new GeoPoint(10, 10), 1);
        area.changeLocation(10, 7);
        assertFalse(area.entered());
        assertFalse(area.exited());
        assertFalse(area.inside());
    }

    @Test
    public void testAreaInitiallyIn() {
        AreaTrigger area = new AreaTrigger(new GeoPoint(10, 10), 1);
        area.changeLocation(10, 10);
        assertTrue(area.entered());
        assertFalse(area.exited());
        assertTrue(area.inside());
    }

    @Test
    public void testAreaOutToOut() {
        AreaTrigger area = new AreaTrigger(new GeoPoint(10, 10), 1);
        area.changeLocation(10,7);
        area.changeLocation(10,6);
        assertFalse(area.entered());
        assertFalse(area.exited());
        assertFalse(area.inside());
    }

    @Test
    public void testAreaInToIn() {
        AreaTrigger area = new AreaTrigger(new GeoPoint(10, 10), 1);
        area.changeLocation(10,10);
        area.changeLocation(10,10.5);
        assertFalse(area.entered());
        assertFalse(area.exited());
        assertTrue(area.inside());
    }

    @Test
    public void testAreaInToOut() {
        AreaTrigger area = new AreaTrigger(new GeoPoint(10, 10), 1);
        area.changeLocation(10,10);
        area.changeLocation(10,12);
        assertFalse(area.entered());
        assertTrue(area.exited());
        assertFalse(area.inside());
    }

    @Test
    public void testAreaOutToIn() {
        AreaTrigger area = new AreaTrigger(new GeoPoint(10, 10), 1);
        area.changeLocation(10,8);
        area.changeLocation(10,10);
        assertTrue(area.entered());
        assertFalse(area.exited());
        assertTrue(area.inside());
    }
}
